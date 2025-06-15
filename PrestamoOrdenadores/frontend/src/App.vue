<template>
  <div class="app">
    <MenuBar v-if="isUserLoggedIn" />
    <MenuBarNoSession v-else />

    <main class="main-content">
      <router-view />
    </main>
    <Footer />
    <Toast position="bottom-right"/>
  </div>
</template>

<script lang="ts">
import {defineComponent, watch, ref, onBeforeUnmount, provide, readonly, computed, onMounted} from 'vue';
import Login from './views/Login.vue';
import { useToast } from 'primevue/usetoast';
import Toast from 'primevue/toast';
import { authService } from "@/services/AuthService";
import Footer from "@/components/Footer.vue";
import MenuBarNoSession from "@/components/MenuBarNoSession.vue";
import MenuBar from "@/components/MenuBar.vue";

type ToastSeverity = 'success' | 'info' | 'warn' | 'error';
type NotificationEntityType = 'info' | 'prestamo' | 'incidencia' | 'sistema' | 'advertencia' | 'error' | 'sancion';
interface Notificacion {
  id: string;
  titulo: string;
  mensaje?: string;
  fecha: Date | string;
  leida: boolean;
  tipo?: NotificationEntityType;
  enlace?: string;
  severidadSugerida?: ToastSeverity;
  mostrarToast?: boolean;
}

export default defineComponent({
  name: 'App',
  components: {
    MenuBarNoSession,
    MenuBar,
    Footer,
    Login,
    Toast
  },
  setup() {
    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://loantechoficial.onrender.com';
    const WS_BASE_URL = import.meta.env.VITE_WS_BASE_URL || 'ws://localhost:8080';

    provide('API_BASE_URL', API_BASE_URL);
    provide('WS_BASE_URL', WS_BASE_URL);

    const webSocket = ref<WebSocket | null>(null);
    const toast = useToast();
    const lastReceivedNotification = ref<Notificacion | null>(null);
    provide('lastReceivedNotification', readonly(lastReceivedNotification));

    const isWebSocketConnecting = ref(false);
    const webSocketManuallyClosed = ref(false);
    let keepAliveIntervalId: number | null = null;

    onMounted(() => {
      console.log('[App.vue onMounted] Sincronizando estado de autenticación desde localStorage...');
      authService.syncFromStorage();
      console.log('[App.vue onMounted] Estado de autenticación sincronizado. Usuario:', authService.user?.nombre);
      console.log('[App.vue onMounted] Rol actual:', authService.role);
    });

    const isUserLoggedIn = computed(() => {
      const isAuth = authService.isAuthenticated();
      console.log('[App.vue computed:isUserLoggedIn] Estado de autenticación:', isAuth, 'Role:', authService.role);
      return isAuth;
    });

    const parseBackendNotificationForApp = (backendNotif: any): Notificacion => {
      let severidad: ToastSeverity = 'info';

      if (backendNotif.severidadSugerida) {
        const lowerCaseSeverity = backendNotif.severidadSugerida.toLowerCase();
        if (['success', 'info', 'warn', 'error'].includes(lowerCaseSeverity)) {
          severidad = lowerCaseSeverity as ToastSeverity;
        } else {
          console.warn(`[App.vue parseBackend] Severidad desconocida recibida: ${backendNotif.severidadSugerida}, usando 'info' por defecto.`);
        }
      }

      return {
        id: backendNotif.id,
        titulo: backendNotif.titulo,
        mensaje: backendNotif.mensaje,
        fecha: new Date(backendNotif.fecha),
        leida: backendNotif.leida,
        tipo: backendNotif.tipo?.toLowerCase() as NotificationEntityType,
        enlace: backendNotif.enlace,
        severidadSugerida: severidad,
        mostrarToast: backendNotif.mostrarToast
      };
    };

    const handleGlobalNewNotification = (backendNotificationData: any) => {
      const nuevaNotificacion = parseBackendNotificationForApp(backendNotificationData);
      console.log("[App.vue WebSocket] Nueva notificación global procesada:", nuevaNotificacion);

      lastReceivedNotification.value = nuevaNotificacion;

      if (nuevaNotificacion.mostrarToast === false) {
        console.log("[App.vue WebSocket] Notificación no configurada para mostrar toast. Omitiendo toast.");
        return;
      }

      let toastSeverity: 'success' | 'info' | 'warn' | 'error';

      switch (nuevaNotificacion.severidadSugerida?.toLowerCase()) {
        case 'success':
          toastSeverity = 'success';
          break;
        case 'warning':
          toastSeverity = 'warn';
          break;
        case 'error':
          toastSeverity = 'error';
          break;
        case 'info':
        default:
          toastSeverity = 'info';
          break;
      }

      toast.add({
        severity: toastSeverity,
        summary: `${nuevaNotificacion.titulo}`,
        life: (toastSeverity === 'success' || toastSeverity === 'info') ? 3000 : 7000,
      });
    };

    const disconnectWebSocketsInApp = (isManual: boolean = false, reason: string = "Desconexión solicitada") => {
      if (isManual) {
        webSocketManuallyClosed.value = true;
      }
      if (webSocket.value) {
        console.log(`[App.vue WebSocket] Desconectando WebSocket global... Razón: ${reason}. Estado actual: ${webSocket.value.readyState}`);
        webSocket.value.onclose = null;
        webSocket.value.onerror = null;
        webSocket.value.onmessage = null;
        webSocket.value.onopen = null;

        if (webSocket.value.readyState === WebSocket.OPEN || webSocket.value.readyState === WebSocket.CONNECTING) {
          try {
            webSocket.value.close(1000, reason);
          } catch (error) {
            console.error("[App.vue WebSocket] Error al intentar cerrar WebSocket:", error);
          }
        }
        webSocket.value = null;
        console.log('[App.vue WebSocket] WebSocket global desconectado y referencia limpiada.');
      }
      isWebSocketConnecting.value = false;
      if (keepAliveIntervalId) {
        clearInterval(keepAliveIntervalId);
        keepAliveIntervalId = null;
        console.log('[App.vue WebSocket] Keep-alive detenido.');
      }
    };

    const connectWebSocketsInApp = () => {
      authService.syncFromStorage();
      const token = authService.token;

      if (!token) {
        console.warn('[App.vue WebSocket] Conexión ABORTADA: No hay token en authService.');
        if (webSocket.value) disconnectWebSocketsInApp(true, "Token no disponible");
        return;
      }

      if (isWebSocketConnecting.value) {
        console.log('[App.vue WebSocket] Conexión WebSocket ya en progreso, se omite nueva solicitud.');
        return;
      }

      const socketUrl = `${WS_BASE_URL}/ws/notificaciones?token=${encodeURIComponent(token)}`;

      if (webSocket.value && webSocket.value.readyState === WebSocket.OPEN && webSocket.value.url === socketUrl) {
        console.log('[App.vue WebSocket] Conexión WebSocket ya establecida y activa con la misma URL/token.');
        return;
      }

      if (webSocket.value) {
        console.log('[App.vue WebSocket] Limpiando conexión WebSocket anterior antes de un nuevo intento.');
        disconnectWebSocketsInApp(false, "Preparando para nueva conexión");
      }

      console.log(`[App.vue WebSocket] INTENTANDO CONECTAR a: ${WS_BASE_URL}/ws/notificaciones (token en query)`);
      isWebSocketConnecting.value = true;
      webSocketManuallyClosed.value = false;

      try {
        const newWsInstance = new WebSocket(socketUrl);
        webSocket.value = newWsInstance;

        newWsInstance.onopen = () => {
          isWebSocketConnecting.value = false;
          if (webSocket.value !== newWsInstance) {
            console.warn('[App.vue WebSocket] onopen: Instancia obsoleta. Cerrando.');
            newWsInstance.close(1000, "Instancia obsoleta en onopen");
            return;
          }
          console.log("[App.vue WebSocket] Conexión WebSocket global establecida.");
          if (webSocket.value && webSocket.value.readyState === WebSocket.OPEN) {
            setupKeepAlive();
          }
        };

        newWsInstance.onmessage = (event) => {
          if (webSocket.value !== newWsInstance) return;

          console.log("[App.vue WebSocket] Mensaje global recibido:", event.data);
          if (typeof event.data === 'string') {
            if (event.data.startsWith("Web socket:")) {
              console.log("[App.vue WebSocket] Mensaje de control/bienvenida del servidor:", event.data);
              return;
            }
            try {
              const notificacionRecibida = JSON.parse(event.data);
              if (notificacionRecibida && typeof notificacionRecibida.id === 'string' && typeof notificacionRecibida.titulo === 'string') {
                handleGlobalNewNotification(notificacionRecibida);
              } else {
                console.warn("[App.vue WebSocket] Mensaje JSON con formato inesperado:", notificacionRecibida);
              }
            } catch (error) {
              console.warn("[App.vue WebSocket] Error al parsear mensaje JSON o mensaje no es JSON:", event.data, error);
            }
          } else {
            console.warn("[App.vue WebSocket] Mensaje recibido no es string:", event.data);
          }
        };

        newWsInstance.onerror = (event) => {
          isWebSocketConnecting.value = false;
          if (webSocket.value !== newWsInstance) {
            console.warn('[App.vue WebSocket] onerror: Error en instancia obsoleta. Ignorando.');
            return;
          }
          console.error("[App.vue WebSocket] Error en WebSocket global:", event);
        };

        newWsInstance.onclose = (event) => {
          isWebSocketConnecting.value = false;
          const currentWsUrl = webSocket.value?.url;

          if (webSocket.value !== null && webSocket.value !== newWsInstance) {
            console.warn(`[App.vue WebSocket] onclose: Cierre de una instancia obsoleta (${newWsInstance.url.substring(0,70)}...). Código: ${event.code}. La instancia activa es ${currentWsUrl?.substring(0,70)}...`);
            return;
          }

          console.log(`[App.vue WebSocket] Conexión WebSocket global cerrada. Código: ${event.code}, Razón: "${event.reason}", Limpio: ${event.wasClean}`);

          if (webSocket.value === newWsInstance) {
            webSocket.value = null;
          }
          if (keepAliveIntervalId) {
            clearInterval(keepAliveIntervalId);
            keepAliveIntervalId = null;
            console.log('[App.vue WebSocket] Keep-alive detenido por cierre de conexión.');
          }

          if (!webSocketManuallyClosed.value && event.code !== 1000 && authService.token) {
            console.log("[App.vue WebSocket] Intentando reconexión en 5 segundos...");
            setTimeout(() => {
              authService.syncFromStorage();
              if (authService.token && !webSocketManuallyClosed.value) {
                console.log("[App.vue WebSocket] Reintentando conexión ahora...");
                connectWebSocketsInApp();
              } else {
                console.warn("[App.vue WebSocket] Reconexión abortada: Sin token o cierre fue manual.");
              }
            }, 5000);
          } else if (webSocketManuallyClosed.value) {
            console.log("[App.vue WebSocket] Cierre fue intencional (manual), no se reconectará.");
          } else if (event.code === 1000) {
            console.log("[App.vue WebSocket] Cierre fue normal (código 1000), no se reconectará automáticamente.");
          }
        };
      } catch (error) {
        console.error("[App.vue WebSocket] Error CRÍTICO al crear la instancia de WebSocket:", error);
        isWebSocketConnecting.value = false;
        webSocket.value = null;
      }
    };

    const setupKeepAlive = () => {
      if (keepAliveIntervalId) {
        clearInterval(keepAliveIntervalId);
      }
      console.log("[App.vue WebSocket] Configurando keep-alive...");
      keepAliveIntervalId = setInterval(() => {
        if (webSocket.value && webSocket.value.readyState === WebSocket.OPEN) {
          console.log("[App.vue WebSocket] Enviando keep-alive ping");
          webSocket.value.send(JSON.stringify({ type: "ping_keep_alive" }));
        }
      }, 30000);
    };

    onMounted(() => {
      console.log('[App.vue onMounted] Verificando estado de autenticación para WebSocket...');

      if (authService.token) {
        console.log('[App.vue onMounted] Token presente, conectando WebSocket...');
        connectWebSocketsInApp();
      }
    });

    watch(
        () => authService.token,
        (newToken, oldToken) => {
          const newTknShort = newToken ? newToken.substring(0, 10) + "..." : "null";
          const oldTknShort = oldToken ? oldToken.substring(0, 10) + "..." : "null";
          console.log(`[App.vue Watch Auth] Token cambió. Anterior: ${oldTknShort}, Nuevo: ${newTknShort}`);

          if (newToken) {
            console.log('[App.vue Watch Auth] Token presente. Se procederá a conectar/verificar WebSocket.');
            connectWebSocketsInApp();
          } else {
            console.warn('[App.vue Watch Auth] Token ausente (logout o expirado). Desconectando WebSocket.');
            disconnectWebSocketsInApp(true, "Usuario deslogueado o token invalidado");
          }
        },
        { immediate: true }
    );

    onBeforeUnmount(() => {
      console.log('[App.vue onBeforeUnmount] App.vue se va a desmontar. Desconectando WebSocket y limpiando keep-alive.');
      disconnectWebSocketsInApp(true, "Componente App.vue desmontado");
    });

    return {
      isUserLoggedIn
    };
  }
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=Inconsolata:wght@200..900&family=Montserrat:ital,wght@0,100..900;1,100..900&family=NTR&family=Quicksand&display=swap');

:root {
  --color-primary: #14124f;
  --color-primary-rgb: 20, 18, 79;
  --color-interactive: #4A90E2;
  --color-interactive-rgb: 74, 144, 226;
  --color-interactive-darker: #3B7ACC;
  --color-accent-soft: #AEC6F4;
  --color-background-main: #ffffff;
  --color-text-dark: #495057;
  --color-neutral-medium: #CED4DA;
  --color-text-on-dark: #E0E7FF;
  --color-text-on-dark-hover: #FFFFFF;
  --color-error: #DC3545;
  --color-error-rgb: 220, 53, 69;
  --color-success: #198754;
  --color-success-rgb: 25, 135, 84;
  --color-warning: #FFC107;
  --color-warning-rgb: 255, 193, 7;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Montserrat', sans-serif;
  background-color: var(--color-background-main);
  color: var(--color-text-dark);
  line-height: 1.5;
}

a {
  color: var(--color-interactive);
  text-decoration: none;
  background-color: inherit !important;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
}

.main-content {
  flex-grow: 1;
  overflow-y: auto;
}

</style>