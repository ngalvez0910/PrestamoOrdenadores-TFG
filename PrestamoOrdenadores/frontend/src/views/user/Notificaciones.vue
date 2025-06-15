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
      <div v-else class="notifications-scroll-container">
        <template v-if="sortedNotifications.length > 0">
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
        <p v-else-if="!loading && notifications.length === 0" class="no-data-message">
          No tienes notificaciones nuevas.
        </p>
      </div>
    </div>
  </div>
  <Dialog
      v-if="selectedNotificationForDialog"
      v-model:visible="isDialogVisible"
      modal
      :header="selectedNotificationForDialog.titulo"
      class="notification-dialog"
      :style="{ width: '90vw', maxWidth: '600px' }"
      :breakpoints="{'640px': '95vw'}"
      @hide="selectedNotificationForDialog = null"
  >
    <div class="dialog-content">
      <div class="dialog-header-info">
        <i :class="getNotificationIcon(selectedNotificationForDialog.tipo)" class="dialog-notification-icon"></i>
        <small class="dialog-timestamp">{{ formatRelativeTime(selectedNotificationForDialog.fecha) }}</small>
      </div>
      <p v-if="selectedNotificationForDialog.mensaje" class="dialog-message">
        {{ selectedNotificationForDialog.mensaje }}
      </p>
      <p v-else class="dialog-message no-message">
        <em>Esta notificación no tiene un mensaje detallado.</em>
      </p>
    </div>
    <template #footer>
      <Button label="Ir al enlace" icon="pi pi-link" class="p-button-text" v-if="selectedNotificationForDialog.enlace" @click="navigateFromDialog" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import { defineComponent, type Ref } from 'vue';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import ProgressSpinner from 'primevue/progressspinner';
import Tooltip from 'primevue/tooltip';
import axios from 'axios';
import Dialog from "primevue/dialog";
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
  components: { Button, Toast, ProgressSpinner, Dialog },
  directives: { Tooltip },
  data() {
    return {
      notifications: [] as Notificacion[],
      loading: true,
      markingAllAsRead: false,
      isDialogVisible: false,
      selectedNotificationForDialog: null as Notificacion | null,
      API_BASE_URL: 'https://loantechoficial.onrender.com',
    };
  },
  setup() {
    const toast = useToast();
    const router = useRouter();
    const lastReceivedNotification = (window as any).__VUE_APP_LAST_NOTIFICATION_REF__;
    return { toast, router, lastReceivedNotification };
  },
  computed: {
    sortedNotifications(): Notificacion[] {
      return [...this.notifications].sort((a, b) => {
        if (a.leida !== b.leida) {
          return a.leida ? 1 : -1;
        }
        return new Date(b.fecha).getTime() - new Date(a.fecha).getTime();
      });
    },
    unreadCount(): number {
      return this.notifications.filter(n => !n.leida).length;
    }
  },
  methods: {
    parseBackendNotificationForPage(backendNotif: any): Notificacion {
      return {
        ...backendNotif,
        fecha: new Date(backendNotif.fecha),
        tipo: backendNotif.tipo?.toLowerCase() as NotificationType,
      };
    },
    addNotificationToList(nuevaNotificacionData: Notificacion): void {
      console.log("[Notificaciones.vue addNotificationToList] Intentando añadir:", JSON.parse(JSON.stringify(nuevaNotificacionData)));
      console.log("[Notificaciones.vue addNotificationToList] Lista actual ANTES:", JSON.parse(JSON.stringify(this.notifications)));

      if (!this.notifications.some(n => n.id === nuevaNotificacionData.id)) {
        this.notifications.unshift(nuevaNotificacionData);
        console.log("[Notificaciones.vue] Notificación AÑADIDA a la lista local (unshift):", nuevaNotificacionData.id);
      } else {
        const index = this.notifications.findIndex(n => n.id === nuevaNotificacionData.id);
        if (index !== -1) {
          this.notifications[index] = { ...this.notifications[index], ...nuevaNotificacionData };
          console.log("[Notificaciones.vue] Notificación ACTUALIZADA en la lista local:", nuevaNotificacionData.id);
        }
      }
      console.log("[Notificaciones.vue addNotificationToList] Lista actual DESPUÉS:", JSON.parse(JSON.stringify(this.notifications)));
    },
    async fetchInitialNotifications(): Promise<void> {
      this.loading = true;
      console.log("[Notificaciones.vue:fetchInitialNotifications] Iniciando carga...");
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.warn("[Notificaciones.vue:fetchInitialNotifications] No hay token. Abortando.");
          this.notifications = [];
          this.loading = false;
          return;
        }
        const response = await axios.get<Notificacion[]>(`${this.API_BASE_URL}/notificaciones`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.data && Array.isArray(response.data)) {
          this.notifications = response.data.map(this.parseBackendNotificationForPage);
        } else {
          this.notifications = [];
        }
      } catch (error: any) {
        console.error("[Notificaciones.vue:fetchInitialNotifications] Error:", error.response?.data || error.message);
        this.notifications = [];
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar las notificaciones.', life: 3000 });
      } finally {
        this.loading = false;
        console.log(`[Notificaciones.vue:fetchInitialNotifications] Carga finalizada. Total: ${this.notifications.length}`);
      }
    },
    async markAsRead(notification: Notificacion, fromDialog: boolean = false): Promise<void> {
      const originalLeidaState = notification.leida;
      const index = this.notifications.findIndex(n => n.id === notification.id);

      let notificationToUpdateInDialog = false;
      if (this.selectedNotificationForDialog && this.selectedNotificationForDialog.id === notification.id) {
        notificationToUpdateInDialog = true;
      }

      if (index !== -1 && !this.notifications[index].leida) {
        this.notifications[index].leida = true;
        if (notificationToUpdateInDialog) {
          this.selectedNotificationForDialog!.leida = true;
        }
      } else if (index === -1 && !fromDialog) {
        return;
      } else if (fromDialog && this.selectedNotificationForDialog && !this.selectedNotificationForDialog.leida) {
        this.selectedNotificationForDialog.leida = true;
        if (index !== -1) this.notifications[index].leida = true;
      }

      try {
        await axios.post(`${this.API_BASE_URL}/notificaciones/${notification.id}/read`, {}, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        if (!fromDialog) {
          this.toast.add({ severity: 'success', summary: 'Leída', detail: 'Notificación marcada como leída.', life: 1500 });
        }
      } catch (error) {
        if (index !== -1) {
          this.notifications[index].leida = originalLeidaState;
        }
        if (notificationToUpdateInDialog && this.selectedNotificationForDialog) {
          this.selectedNotificationForDialog.leida = originalLeidaState;
        }
        console.error("Error al marcar como leída:", error);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo marcar como leída.', life: 3000 });
      }
    },
    async markAllAsRead(): Promise<void> {
      this.markingAllAsRead = true;
      const originalNotificationsState = JSON.parse(JSON.stringify(this.notifications));
      this.notifications.forEach(n => n.leida = true);

      try {
        await axios.post(`${this.API_BASE_URL}/notificaciones/read-all`, {}, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        this.toast.add({ severity: 'success', summary: 'Leídas', detail: 'Todas las notificaciones marcadas como leídas.', life: 2000 });
      } catch (error) {
        this.notifications = originalNotificationsState.map(this.parseBackendNotificationForPage);
        console.error("Error al marcar todas como leídas:", error);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron marcar todas como leídas.', life: 3000 });
      } finally {
        this.markingAllAsRead = false;
      }
    },
    async deleteNotification(notification: Notificacion): Promise<void> {
      const originalNotifications = [...this.notifications];
      this.notifications = this.notifications.filter(n => n.id !== notification.id);

      try {
        await axios.delete(`${this.API_BASE_URL}/notificaciones/${notification.id}`, {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        this.toast.add({ severity: 'warn', summary: 'Eliminada', detail: 'Notificación eliminada.', life: 1500 });
      } catch (error) {
        this.notifications = originalNotifications;
        console.error("Error al eliminar notificación:", error);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo eliminar la notificación.', life: 3000 });
      }
    },
    handleNotificationClick(notification: Notificacion): void {
      this.selectedNotificationForDialog = { ...notification, fecha: new Date(notification.fecha) };
      this.isDialogVisible = true;
      if (!notification.leida) {
        this.markAsRead(notification, true);
      }
    },
    getNotificationIcon(tipo?: NotificationType): string {
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
    },
    formatRelativeTime(dateInput: Date | string): string {
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
    },
    truncate(text: string | undefined, length: number): string {
      if (!text) return '';
      return text.length > length ? text.substring(0, length) + "..." : text;
    },
    closeNotificationDialog(): void {
      this.isDialogVisible = false;
    },
    navigateFromDialog(): void {
      if (this.selectedNotificationForDialog && this.selectedNotificationForDialog.enlace) {
        this.router.push(this.selectedNotificationForDialog.enlace);
        this.closeNotificationDialog();
      }
    }
  },
  mounted() {
    this.fetchInitialNotifications();
    if (this.lastReceivedNotification) {
      this.$watch('lastReceivedNotification.value', (newNotificationPayload: Notificacion | null) => {
        if (newNotificationPayload) {
          console.log("[Notificaciones.vue Watcher] Nueva notificación global detectada:", newNotificationPayload);
          this.addNotificationToList(newNotificationPayload);
        }
      });
    } else {
      console.warn("[Notificaciones.vue] No se pudo inyectar 'lastReceivedNotification'. Las actualizaciones en tiempo real podrían no funcionar en esta página.");
    }
  }
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
  display: flex;
  flex-direction: column;
}

.notifications-scroll-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 0;
}

.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  color: var(--color-text-dark);
}

.loading-indicator p {
  margin-top: 15px;
  font-size: 0.9rem;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid var(--color-neutral-medium, #e9ecef);
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
  background-color: var(--color-text-on-dark, #e0f2fe);
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
.notification-icon.pi-times-circle, .notification-icon.pi-ban { color: var(--color-error); }
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

.notification-dialog .dialog-content {
  padding-top: 0.5rem;
  font-family: 'Montserrat', sans-serif;
}

.dialog-header-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.dialog-notification-icon {
  font-size: 1.8rem;
}

.dialog-timestamp {
  font-size: 0.8rem;
  color: var(--color-text-dark);
}

.dialog-message {
  font-size: 0.95rem;
  line-height: 1.6;
  color: var(--color-text-dark);
  white-space: pre-wrap;
  word-break: break-word;
}

.dialog-message.no-message {
  font-style: italic;
  color: var(--color-text-dark);
}

:global(.notification-dialog .p-dialog-header) {
  background-color: #f0f0f0;
  font-family: 'Montserrat', sans-serif;
}

:global(.notification-dialog .p-dialog-title){
  max-width: calc(100% - 3rem);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-dialog .p-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding-top: 1rem;
  padding-bottom: 1rem;
}

.notification-dialog .p-button-text {
  font-family: 'Montserrat', sans-serif;
  color: var(--color-text-dark);
}

.notification-dialog .p-button-text:hover {
  color: var(--color-interactive);
  background-color: transparent !important;
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