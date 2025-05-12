<template>
  <div class="page-container notifications-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Mis Notificaciones</h2>
      </div>
      <div class="header-right">
        <Button
            v-if="notifications.length > 0 && unreadCount > 0"
            label="Marcar todas como leídas"
            icon="pi pi-check-square"
            class="action-button secondary-button"
            @click="markAllAsRead"
            :loading="markingAllAsRead"
        />
      </div>
    </div>

    <div class="notifications-list-container">
      <div v-if="loading" class="loading-indicator">
        <ProgressSpinner strokeWidth="4" animationDuration=".5s" style="width: 50px; height: 50px" />
        <p>Cargando notificaciones...</p>
      </div>
      <template v-else-if="notifications.length > 0">
        <div
            v-for="notificacion in sortedNotifications"
            :key="notificacion.id"
            class="notification-item"
            :class="{ 'is-unread': !notificacion.leida }"
            @click="handleNotificationClick(notificacion)"
        >
          <div class="notification-icon-area">
            <i :class="getNotificationIcon(notificacion.tipo)" class="notification-icon"></i>
            <span v-if="!notificacion.leida" class="unread-dot" title="No leída"></span>
          </div>
          <div class="notification-details">
            <h5 class="notification-title">{{ notificacion.titulo }}</h5>
            <p v-if="notificacion.mensaje" class="notification-message-preview">{{ truncate(notificacion.mensaje, 120) }}</p>
            <small class="notification-timestamp">{{ formatRelativeTime(notificacion.fecha) }}</small>
          </div>
          <div class="notification-actions">
            <Button
                v-if="!notificacion.leida"
                icon="pi pi-eye"
                class="p-button-rounded p-button-text action-button-table"
                @click.stop="markAsRead(notificacion)"
                v-tooltip.top="'Marcar como leída'"
            />
            <Button
                icon="pi pi-trash"
                class="p-button-rounded p-button-text p-button-danger action-button-table"
                @click.stop="deleteNotification(notificacion)"
                v-tooltip.top="'Eliminar notificación'"
            />
          </div>
        </div>
      </template>
      <p v-else class="no-data-message">
        No tienes notificaciones nuevas.
      </p>
    </div>
  </div>
  <Toast />
</template>

<script lang="ts">
import {defineComponent, ref, onMounted, computed, inject, watch, type Ref} from 'vue';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import ProgressSpinner from 'primevue/progressspinner';
import Tooltip from 'primevue/tooltip';
import axios from 'axios';

import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

type NotificationType = 'info' | 'prestamo' | 'incidencia' | 'sistema' | 'advertencia' | 'error' | 'sancion';

interface Notificacion {
  id: string;
  titulo: string;
  mensaje?: string;
  fecha: Date | string;
  leida: boolean;
  tipo?: NotificationType;
  enlace?: string;
}

export default defineComponent({
  name: "Notificaciones",
  components: { Button, Toast, ProgressSpinner },
  directives: { Tooltip },
  setup() {
    const API_BASE_URL = inject<string>('API_BASE_URL', 'http://localhost:8080');

    const notifications = ref<Notificacion[]>([]);
    const loading = ref(true);
    const markingAllAsRead = ref(false);
    const toast = useToast();
    const router = useRouter();
    const lastReceivedNotification = inject<Readonly<Ref<Notificacion | null>>>('lastReceivedNotification');

    const parseBackendNotificationForPage = (backendNotif: any): Notificacion => {
      return {
        ...backendNotif,
        fecha: new Date(backendNotif.fecha),
        tipo: backendNotif.tipo?.toLowerCase() as NotificationType,
      };
    };

    const addNotificationToList = (nuevaNotificacionData: Notificacion) => {
      console.log("[Notificaciones.vue addNotificationToList] Intentando añadir:", JSON.parse(JSON.stringify(nuevaNotificacionData)));
      console.log("[Notificaciones.vue addNotificationToList] Lista actual ANTES:", JSON.parse(JSON.stringify(notifications.value)));

      if (!notifications.value.some(n => n.id === nuevaNotificacionData.id)) {
        notifications.value.unshift(nuevaNotificacionData);
        console.log("[Notificaciones.vue] Notificación AÑADIDA a la lista local (unshift):", nuevaNotificacionData.id);
      } else {
        const index = notifications.value.findIndex(n => n.id === nuevaNotificacionData.id);
        if (index !== -1) {
          notifications.value[index] = { ...notifications.value[index], ...nuevaNotificacionData };
          console.log("[Notificaciones.vue] Notificación ACTUALIZADA en la lista local:", nuevaNotificacionData.id);
        }
      }
      console.log("[Notificaciones.vue addNotificationToList] Lista actual DESPUÉS:", JSON.parse(JSON.stringify(notifications.value)));
    };

    if (lastReceivedNotification) {
      watch(lastReceivedNotification, (newNotificationPayload) => {
        if (newNotificationPayload) {
          console.log("[Notificaciones.vue Watcher] Nueva notificación global detectada:", newNotificationPayload);
          addNotificationToList(newNotificationPayload);
        }
      });
    } else {
      console.warn("[Notificaciones.vue] No se pudo inyectar 'lastReceivedNotification'. Las actualizaciones en tiempo real podrían no funcionar en esta página.");
    }

    const fetchInitialNotifications = async () => {
      loading.value = true;
      console.log("[Notificaciones.vue:fetchInitialNotifications] Iniciando carga...");
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.warn("[Notificaciones.vue:fetchInitialNotifications] No hay token. Abortando.");
          notifications.value = [];
          loading.value = false;
          return;
        }
        const response = await axios.get<Notificacion[]>(`${API_BASE_URL}/notificaciones`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.data && Array.isArray(response.data)) {
          notifications.value = response.data.map(parseBackendNotificationForPage);
        } else {
          notifications.value = [];
        }
      } catch (error: any) {
        console.error("[Notificaciones.vue:fetchInitialNotifications] Error:", error.response?.data || error.message);
        notifications.value = [];
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar las notificaciones.', life: 3000 });
      } finally {
        loading.value = false;
        console.log(`[Notificaciones.vue:fetchInitialNotifications] Carga finalizada. Total: ${notifications.value.length}`);
      }
    };

    onMounted(() => {
      fetchInitialNotifications();
    });

    const sortedNotifications = computed(() => {
      return [...notifications.value].sort((a, b) => {
        if (a.leida !== b.leida) {
          return a.leida ? 1 : -1;
        }
        return new Date(b.fecha).getTime() - new Date(a.fecha).getTime();
      });
    });

    const unreadCount = computed(() => notifications.value.filter(n => !n.leida).length);

    const markAsRead = async (notification: Notificacion) => {
      const originalLeidaState = notification.leida;
      const index = notifications.value.findIndex(n => n.id === notification.id);

      if (index !== -1 && !notifications.value[index].leida) {
        notifications.value[index].leida = true;
      } else if (index === -1){
        return;
      }

      try {
        await axios.post(`${API_BASE_URL}/notificaciones/${notification.id}/read`, {}, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        toast.add({ severity: 'success', summary: 'Leída', detail: 'Notificación marcada como leída.', life: 1500 });
      } catch (error) {
        if (index !== -1) {
          notifications.value[index].leida = originalLeidaState;
        }
        console.error("Error al marcar como leída:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo marcar como leída.', life: 3000 });
      }
    };

    const markAllAsRead = async () => {
      markingAllAsRead.value = true;
      const originalNotificationsState = JSON.parse(JSON.stringify(notifications.value));
      notifications.value.forEach(n => n.leida = true);

      try {
        await axios.post(`${API_BASE_URL}/notificaciones/read-all`, {}, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        toast.add({ severity: 'success', summary: 'Leídas', detail: 'Todas las notificaciones marcadas como leídas.', life: 2000 });
      } catch (error) {
        notifications.value = originalNotificationsState.map(parseBackendNotificationForPage);
        console.error("Error al marcar todas como leídas:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron marcar todas como leídas.', life: 3000 });
      } finally {
        markingAllAsRead.value = false;
      }
    };

    const deleteNotification = async (notification: Notificacion) => {
      const originalNotifications = [...notifications.value];
      notifications.value = notifications.value.filter(n => n.id !== notification.id);

      try {
        await axios.delete(`${API_BASE_URL}/notificaciones/${notification.id}`, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        toast.add({ severity: 'warn', summary: 'Eliminada', detail: 'Notificación eliminada.', life: 1500 });
      } catch (error) {
        notifications.value = originalNotifications;
        console.error("Error al eliminar notificación:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo eliminar la notificación.', life: 3000 });
      }
    };

    const handleNotificationClick = (notification: Notificacion) => {
      if (!notification.leida) {
        markAsRead(notification);
      }
      if (notification.enlace) {
        router.push(notification.enlace);
      }
    };

    const getNotificationIcon = (tipo?: NotificationType): string => {
      switch (tipo) {
        case 'prestamo': return 'pi pi-arrow-right-arrow-left';
        case 'incidencia': return 'pi pi-flag-fill';
        case 'sistema': return 'pi pi-cog';
        case 'advertencia': return 'pi pi-bell';
        case 'error': return 'pi pi-times-circle';
        case 'sancion': return 'pi pi-ban';
        case 'info':
        default:
          return 'pi pi-info-circle';
      }
    };

    const formatRelativeTime = (dateInput: Date | string): string => {
      const date = new Date(dateInput);
      const now = new Date();
      const diffSeconds = Math.round((now.getTime() - date.getTime()) / 1000);
      const diffMinutes = Math.round(diffSeconds / 60);
      const diffHours = Math.round(diffMinutes / 60);
      const diffDays = Math.round(diffHours / 24);

      if (diffSeconds < 60) return `Hace ${diffSeconds} seg`;
      if (diffMinutes < 60) return `Hace ${diffMinutes} min`;
      if (diffHours < 24) return `Hace ${diffHours} h`;
      if (diffDays === 1) return `Ayer`;
      if (diffDays < 7) return `Hace ${diffDays} días`;
      return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
    };

    const truncate = (text: string | undefined, length: number): string => {
      if (!text) return '';
      return text.length > length ? text.substring(0, length) + "..." : text;
    };

    return {
      notifications,
      loading,
      markingAllAsRead,
      sortedNotifications,
      unreadCount,
      markAsRead,
      markAllAsRead,
      deleteNotification,
      handleNotificationClick,
      getNotificationIcon,
      formatRelativeTime,
      truncate,
    };
  },
});
</script>

<style scoped>
.page-container.notifications-page {
  padding: 80px 30px 40px 30px;
  max-width: 650px;
  box-sizing: border-box;
  font-family: 'Montserrat', sans-serif;
  margin-left: auto;
  margin-right: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-header h2 {
  color: var(--color-primary);
  font-size: 1.8rem;
  font-weight: 600;
  margin: 0;
}

.action-button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  text-decoration: none;
  line-height: 1.2;
}

.action-button.secondary-button {
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
  border: 1px solid var(--color-neutral-medium);
}

.action-button.secondary-button:hover {
  background-color: var(--color-interactive-darker);
  border-color: var(--color-neutral-medium);
}

.notifications-list-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(var(--color-primary-rgb), 0.1);
  height: 600px;
  position: relative;
  overflow: hidden;
}

.notifications-scroll-container {
  height: 100%;
  overflow-y: auto;
  padding: 0;
}

.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  height: 100%;
  color: var(--color-text-on-dark);
}

.loading-indicator p {
  margin-top: 15px;
  font-size: 0.9rem;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid var(--color-neutral-medium);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: var(--color-background-main, #f8f9fa);
}

.notification-item.is-unread {
  background-color: var(--color-text-on-dark, #eff6ff);
}

.notification-item.is-unread .notification-title {
  font-weight: 600;
}

.notification-icon-area {
  margin-right: 15px;
  flex-shrink: 0;
  padding-top: 2px;
  position: relative;
}

.notification-icon {
  font-size: 1.5rem;
  color: var(--color-text-on-dark);
}

.notification-icon.pi-arrow-right-arrow-left { color: var(--color-interactive); }
.notification-icon.pi-flag-fill { color: var(--color-text-dark); }
.notification-icon.pi-cog { color: var(--color-primary); }
.notification-icon.pi-bell { color: var(--color-primary); }
.notification-icon.pi-times-circle { color: var(--color-error); }
.notification-icon.pi-info-circle { color: var(--color-interactive-darker); }

.unread-dot {
  position: absolute;
  top: 0px;
  right: -5px;
  width: 10px;
  height: 10px;
  background-color: var(--color-interactive);
  border-radius: 50%;
  border: 2px solid white;
}

.notification-details {
  flex-grow: 1;
  overflow: hidden;
}

.notification-title {
  font-size: 1rem;
  color: var(--color-text-dark);
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.notification-message-preview {
  font-size: 0.85rem;
  color: var(--color-text-dark);
  margin: 0 0 6px 0;
}

.notification-timestamp {
  font-size: 0.75rem;
  color: var(--color-text-dark);
}

.notification-actions {
  margin-left: 15px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.action-button-table {
  width: 2.2rem;
  height: 2.2rem;
  padding: 0;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  border-radius: 50% !important;
}

.action-button-table.p-button-text:hover {
  background-color: rgba(var(--color-primary-rgb), 0.08) !important;
}

.action-button-table.p-button-danger:hover {
  background-color: rgba(var(--color-error-rgb), 0.1) !important;
}

.no-data-message {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-dark);
  font-style: italic;
}

@media (max-width: 768px) {
  .page-container.notifications-page {
    padding: 70px 15px 30px 15px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }

  .page-header .header-left h2 {
    font-size: 1.5rem;
  }

  .action-button.secondary-button {
    width: 100%;
    justify-content: center;
  }

  .notifications-list-container {
    height: 500px;
  }

  .notification-item {
    padding: 12px 15px;
  }

  .notification-icon-area {
    margin-right: 12px;
  }

  .notification-icon {
    font-size: 1.3rem;
  }

  .notification-title {
    font-size: 0.95rem;
  }

  .notification-message-preview {
    font-size: 0.8rem;
  }
}
</style>