<template>
  <div class="page-container profile-page">

    <div class="profile-card">
      <div class="profile-card-header">
        <h2>Mi Perfil</h2>
        <i class="pi pi-user header-icon"></i>
      </div>

      <div class="profile-tab-content">
        <div class="profile-card-body">
          <div class="avatar-section">
            <img :src="avatar" alt="Avatar" class="avatar-image" />
            <button @click="changeAvatar" class="action-button avatar-button">
              <i class="pi pi-camera"></i> Cambiar Avatar
            </button>
          </div>

          <div class="user-info-section">
            <div class="form-group">
              <label for="nombre">Nombre</label>
              <div id="nombre" class="readonly-field">{{ nombre || '-' }}</div>
            </div>
            <div class="form-group">
              <label for="email">Correo electrónico</label>
              <div id="email" class="readonly-field">{{ email || '-' }}</div>
            </div>
            <div class="form-group">
              <label for="curso">Curso</label>
              <div id="curso" class="readonly-field">{{ curso || '-' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-card-footer">
        <button @click="openChangePasswordModal" class="action-button change-password-button">
          <i class="pi pi-key"></i> Cambiar Contraseña
        </button>
        <button @click="logout" class="action-button logout-button-tabs">
          <i class="pi pi-sign-out"></i> Cerrar sesión
        </button>
      </div>
    </div>
  </div>

  <Dialog header="Cambiar Contraseña" v-model:visible="isChangePasswordModalVisible" modal
          :style="{ width: 'clamp(300px, 40vw, 500px)', fontFamily: 'Montserrat, sans-serif' }"
          :draggable="false" @hide="resetChangePasswordForm">
    <form @submit.prevent="handleChangePassword" class="modal-form">
      <div class="form-group">
        <label for="oldPassword" class="input-label">Contraseña Antigua</label>
        <Password id="oldPassword" v-model="changePasswordForm.oldPassword" class="input-field full-width"
                  :feedback="false" toggleMask placeholder="Introduce tu contraseña actual" />
        <small v-if="changePasswordErrors.oldPassword" class="error-message p-error">{{ changePasswordErrors.oldPassword }}</small>
      </div>
      <div class="form-group">
        <label for="newPassword" class="input-label">Nueva Contraseña</label>
        <Password id="newPassword" v-model="changePasswordForm.newPassword" class="input-field full-width"
                  toggleMask placeholder="Introduce tu nueva contraseña" :feedback="true" />
        <small v-if="changePasswordErrors.newPassword" class="error-message p-error" style="white-space: pre-wrap;">{{ changePasswordErrors.newPassword }}</small>
      </div>
      <div class="form-group">
        <label for="confirmNewPassword" class="input-label">Confirmar Nueva Contraseña</label>
        <Password id="confirmNewPassword" v-model="changePasswordForm.confirmNewPassword" class="input-field full-width"
                  :feedback="false" toggleMask placeholder="Confirma tu nueva contraseña" />
        <small v-if="changePasswordErrors.confirmNewPassword" class="error-message p-error">{{ changePasswordErrors.confirmNewPassword }}</small>
      </div>
      <small v-if="changePasswordErrors.general" class="error-message p-error general-error">{{ changePasswordErrors.general }}</small>

      <div class="dialog-footer-buttons">
        <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button secondary-button" @click="isChangePasswordModalVisible = false" />
        <Button type="submit" label="Cambiar Contraseña" icon="pi pi-save" class="action-button primary-button" :loading="isSubmittingChangePassword" />
      </div>
    </form>
  </Dialog>

  <Toast />
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import Toast from 'primevue/toast';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Password from 'primevue/password';
import axios from 'axios';
import {useRouter} from "vue-router";
import { jwtDecode } from "jwt-decode";
import {useToast} from "primevue/usetoast";
import { authService } from '@/services/AuthService';

interface UserData {
  nombre: string;
  apellidos?: string;
  email: string;
  curso: string;
  avatar?: string;
}

export default defineComponent({
  nombre: "Profile",
  components: { Toast, Dialog, Button, Password },
  data() {
    return {
      nombre: '',
      email: '',
      curso: '',
      avatar: 'https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg',
      loading: true,
      isChangePasswordModalVisible: false,
      changePasswordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: '',
      },
      changePasswordErrors: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: '',
        general: '',
      },
      isSubmittingChangePassword: false,
    };
  },
  setup: function () {
    const router = useRouter();
    const toast = useToast();
    return {router, toast};
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    async obtenerDatos() {
      const token = localStorage.getItem("token");
      if (token) {
        try {
          const decodedToken = jwtDecode(token);
          const userEmail = decodedToken.sub;

          let apiUrl = `http://localhost:8080/users/email/${userEmail}`;

          const response = await axios.get<UserData>(apiUrl, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          this.nombre = response.data.apellidos ? `${response.data.nombre} ${response.data.apellidos}` : response.data.nombre;
          this.email = response.data.email;
          this.curso = response.data.curso;
          if (response.data.avatar) {
            this.avatar = response.data.avatar;
          }
        } catch (error) {
          console.error("Error al obtener los datos del usuario:", error);
        }
      }
    },
    async logout() {
      console.trace("Logout desde Profile.vue:");
      console.log("Cerrando sesión...");
      try {
        await authService.logout();
        console.log("[Profile.vue] authService.logout() completado.");
        this.$router.push("/");
      } catch (error) {
        console.error("[Profile.vue] Error durante el logout:", error);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cerrar la sesión completamente.', life: 3000 });
        this.$router.push("/");
      }
    },
    changeAvatar() {
      const newAvatarUrl = prompt("Introduce la URL de tu nuevo avatar:", this.avatar);
      if (newAvatarUrl) {
        this.avatar = newAvatarUrl;
        this.toast.add({ severity: 'info', summary: 'Avatar Actualizado', detail: 'El avatar se ha actualizado (simulación).', life: 3000 });
      }
    },
    resetChangePasswordForm() {
      this.changePasswordForm.oldPassword = '';
      this.changePasswordForm.newPassword = '';
      this.changePasswordForm.confirmNewPassword = '';
      this.changePasswordErrors.oldPassword = '';
      this.changePasswordErrors.newPassword = '';
      this.changePasswordErrors.confirmNewPassword = '';
      this.changePasswordErrors.general = '';
      this.isSubmittingChangePassword = false;
    },
    openChangePasswordModal() {
      this.resetChangePasswordForm();
      this.isChangePasswordModalVisible = true;
    },
    async handleChangePassword() {
      let isValid = true;
      this.changePasswordErrors = { oldPassword: '', newPassword: '', confirmNewPassword: '', general: '' };

      if (!this.changePasswordForm.oldPassword) {
        this.changePasswordErrors.oldPassword = 'La contraseña antigua es obligatoria.';
        isValid = false;
      }
      if (!this.changePasswordForm.newPassword) {
        this.changePasswordErrors.newPassword = 'La nueva contraseña es obligatoria.';
        isValid = false;
      } else if (!/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.changePasswordForm.newPassword)) {
        this.changePasswordErrors.newPassword = 'La contraseña debe cumplir los siguientes requisitos:\n- Mínimo 8 caracteres.\n- Al menos una mayúscula.\n- Al menos una minúscula.\n- Al menos un dígito.\n- Al menos un carácter especial.';
        isValid = false;
      }
      if (!this.changePasswordForm.confirmNewPassword) {
        this.changePasswordErrors.confirmNewPassword = 'Por favor, confirma la nueva contraseña.';
        isValid = false;
      } else if (this.changePasswordForm.newPassword && this.changePasswordForm.newPassword !== this.changePasswordForm.confirmNewPassword) {
        this.changePasswordErrors.confirmNewPassword = 'Las nuevas contraseñas no coinciden.';
        isValid = false;
      }
      if (this.changePasswordForm.oldPassword && this.changePasswordForm.newPassword && this.changePasswordForm.oldPassword === this.changePasswordForm.newPassword) {
        this.changePasswordErrors.newPassword = 'La nueva contraseña no puede ser igual a la antigua.';
        isValid = false;
      }

      if (!isValid) return;

      const token = localStorage.getItem("token");
      if (!token) {
        this.changePasswordErrors.general = 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.';
        this.toast.add({ severity: 'error', summary: 'Error de sesión', detail: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', life: 5000 });
        setTimeout(() => {
          this.logout();
        }, 2000);
        return;
      }

      this.isSubmittingChangePassword = true;
      try {
        await authService.changePassword({
          oldPassword: this.changePasswordForm.oldPassword,
          newPassword: this.changePasswordForm.newPassword,
          confirmPassword: this.changePasswordForm.confirmNewPassword
        });
        this.toast.add({ severity: 'success', summary: 'Éxito', detail: 'Contraseña actualizada correctamente.', life: 3000 });
        this.isChangePasswordModalVisible = false;
      } catch (error: any) {
        console.error('Error al cambiar la contraseña:', error);
        const errorMessage = error.message || 'No se pudo cambiar la contraseña.';
        this.changePasswordErrors.general = errorMessage;
        this.toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });

        if (errorMessage.includes('sesión') || errorMessage.includes('No hay sesión activa')) {
          setTimeout(() => {
            this.logout();
          }, 2000);
        }
      } finally {
        this.isSubmittingChangePassword = false;
      }
    },
  },
});
</script>

<style scoped>
.page-container.profile-page {
  padding: 80px 30px 40px 30px;
  max-width: 800px;
  margin: 0 auto;
  box-sizing: border-box;
}

.profile-card {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.1);
  width: 100%;
  box-sizing: border-box;
}

.profile-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--color-neutral-medium);
}

.profile-card-header h2 {
  color: var(--color-primary);
  margin: 0;
  font-size: 1.6rem;
  font-weight: 600;
}

.header-icon {
  font-size: 2.5rem;
  color: var(--color-primary);
  opacity: 0.7;
}

.profile-card-body {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 30px;
  align-items: flex-start;
  padding-top: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-image {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  object-fit: cover;
  border: 3px solid var(--color-neutral-medium);
}

.user-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.85rem;
  color: var(--color-text-dark);
  font-weight: 500;
  text-transform: uppercase;
  opacity: 0.8;
}

.readonly-field {
  padding: 10px 12px;
  font-size: 1rem;
  line-height: 1.4;
  color: var(--color-text-dark);
  background-color: var(--color-background-main);
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  min-height: 44px;
  height: 44px;
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
  word-wrap: break-word;
}

.profile-card-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.action-button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease, transform 0.1s ease;
  text-decoration: none;
  line-height: 1.2;
}

.action-button:active {
  transform: scale(0.98);
}

.change-password-button {
  background-color: var(--color-interactive);
  color: white;
  margin-right: 270px;
}

.change-password-button:hover {
  background-color: var(--color-interactive-darker);
}

.logout-button-tabs {
  background-color: var(--color-error);
  color: white;
}

.logout-button-tabs:hover {
  background-color: var(--color-error);
}

.avatar-button {
  background-color: var(--color-interactive);
  color: white;
  width: auto;
}
.avatar-button:hover {
  background-color: var(--color-interactive-darker);
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.modal-form .form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.modal-form .input-label {
  font-size: 0.9rem;
  color: var(--color-text-dark);
  font-weight: 500;
}

.modal-form :deep(.p-password.input-field .p-inputtext) {
  border-radius: 8px !important;
  border: 1px solid var(--color-neutral-medium) !important;
  padding: 10px 12px !important;
  font-family: 'Montserrat', sans-serif;
  font-size: 1rem;
  width: 100% !important;
  box-sizing: border-box;
}
.modal-form :deep(.p-password.input-field .p-inputtext:focus) {
  border-color: var(--color-interactive) !important;
  box-shadow: 0 0 0 2px rgba(var(--color-interactive-rgb), 0.2) !important;
}

.error-message.p-error {
  color: var(--color-error);
  font-size: 0.85rem;
}
.error-message.general-error {
  text-align: center;
  font-weight: 500;
  margin-top: 10px;
}

.dialog-footer-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
}

.action-button.primary-button {
  background-color: var(--color-interactive);
  color: white;
  border: 1px solid var(--color-interactive);
}
.action-button.primary-button:hover {
  background-color: var(--color-interactive-darker);
  color: white;
}

.action-button.secondary-button {
  background-color: transparent;
  color: var(--color-interactive);
  border: 1px solid var(--color-interactive);
}
.action-button.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05);
}

:deep(.p-dialog .p-dialog-header) {
  background-color: var(--color-background-main);
  color: var(--color-primary);
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-neutral-medium);
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
:deep(.p-dialog .p-dialog-header .p-dialog-title) {
  font-family: 'Montserrat', sans-serif;
  font-weight: 600;
  font-size: 1.25rem;
}
:deep(.p-dialog .p-dialog-header .p-dialog-header-icon) {
  color: var(--color-text-dark) !important;
}
:deep(.p-dialog .p-dialog-header .p-dialog-header-icon:hover) {
  color: var(--color-primary) !important;
  background-color: rgba(var(--color-primary-rgb), 0.1) !important;
}
:deep(.p-dialog .p-dialog-content) {
  background-color: white;
  padding: 1.5rem;
  font-family: 'Montserrat', sans-serif;
}

@media (max-width: 992px) {
  .profile-card-body {
    grid-template-columns: 1fr;
    text-align: center;
  }
  .avatar-section {
    align-items: center;
  }
  .user-info-section {
    text-align: left;
  }
  .profile-tabs-container.internal-tabs ul {
    flex-wrap: wrap;
  }
  .profile-tabs-container.internal-tabs .tab-link {
    padding: 8px 12px;
    font-size: 0.9rem;
  }
}
@media (max-width: 768px) {
  .page-container.profile-page {
    padding: 70px 20px 30px 20px;
  }
  .profile-card-header h2 {
    font-size: 1.4rem;
  }
  .header-icon {
    font-size: 2rem;
  }
  .profile-card-footer {
    flex-direction: column;
    gap: 10px;
  }
  .action-button {
    width: 100%;
    justify-content: center;
  }
}
</style>