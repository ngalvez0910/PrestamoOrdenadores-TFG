<template>
  <div class="login-page-container">
    <div class="login-box">
      <h2>Iniciar Sesión</h2> <form @submit.prevent="login" novalidate>
      <div class="form-group">
        <label for="email" class="input-label">Correo electrónico</label>
        <input
            type="email"
            id="email"
            class="input-field"
            name="email"
            placeholder="email.loantech@gmail.com"
            v-model="form.email"
            :aria-invalid="errors.email ? 'true' : 'false'"
            aria-describedby="email-error"
        >
        <p id="email-error" class="error-message" v-if="errors.email">{{ errors.email }}</p>
      </div>

      <div class="form-group">
        <label for="password" class="input-label">Contraseña</label>
        <div class="password-input-wrapper">
          <input
              :type="passwordFieldType"
              id="password"
              name="password"
              class="input-field"
              placeholder="Introduce tu contraseña"
              v-model="form.password"
              :aria-invalid="errors.password ? 'true' : 'false'"
              aria-describedby="password-error"
          >
          <i
              :class="['pi', passwordFieldType === 'password' ? 'pi-eye' : 'pi-eye-slash', 'password-toggle-icon']"
              @click="togglePasswordVisibility"
              title="Mostrar/Ocultar contraseña"
          ></i>
        </div>
        <p id="password-error" class="error-message" v-if="errors.password">{{ errors.password }}</p>
      </div>

      <button type="submit" class="submit-button">Entrar</button>
    </form>

      <p class="register-link">
        ¿No tienes cuenta? <a href="/registro">Regístrate aquí</a>
      </p>

      <Toast />
    </div>
  </div>
</template>

<script lang="ts">
import Toast from "primevue/toast";
import Button from "primevue/button";
import { useToast } from "primevue/usetoast";
import axios from 'axios';
import {authService} from "@/services/AuthService.ts";

export default {
  name: 'Login',
  components: { Toast, Button },
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      form: {
        email: '',
        password: ''
      },
      errors: {
        email: '',
        password: ''
      },
      passwordFieldType: 'password',
      redirectPath: '/',
    };
  },
  mounted() {
    this.redirectPath = (this.$route.query.redirect as string) || '/profile';
  },
  methods: {
    validateForm(): boolean {
      this.errors = { email: '', password: '' };
      let formIsValid = true;

      if (!this.form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = 'Correo electrónico inválido';
        formIsValid = false;
      }
      if (this.form.password.length < 8) {
        this.errors.password = 'La contraseña debe tener al menos 8 caracteres.';
        formIsValid = false;
      }

      return formIsValid;
    },
    async login() {
      if (!this.validateForm()) {
        this.toast.add({
          severity: 'error',
          summary: 'Error en el formulario',
          detail: 'Por favor, corrija los errores indicados.',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
        return;
      }

      try {
        this.toast.add({ severity: 'info', summary: 'Iniciando sesión...', detail: 'Por favor espera.', life: 1500 });

        const response = await axios.post('http://localhost:8080/auth/signin', this.form);
        const receivedToken = response.data.token;

        if (receivedToken) {
          authService.setToken(receivedToken);
          try {
            await authService.fetchUser();
            console.log("[Login.vue] authService.fetchUser completado.");

            const userRoleFromService = authService.role;
            console.log("[Login.vue] Rol obtenido DESDE authService:", userRoleFromService);

            if (!userRoleFromService) {
              console.error("[Login.vue] No se encontró el rol en authService.user después de fetchUser.");
              this.toast.add({ severity: 'error', summary: 'Error de Datos', detail: 'No se pudo verificar el rol del usuario.', life: 3000 });
              await authService.logout();
              return;
            }

            this.toast.add({ severity: 'success', summary: '¡Éxito!', detail: 'Sesión iniciada.', life: 2000 });

            if (userRoleFromService === 'ADMIN') {
              this.$router.push('/admin/dashboard');
            } else {
              this.$router.push(this.redirectPath);
            }

          } catch (fetchError) {
            console.error("[Login.vue] Error durante fetchUser post-login:", fetchError);
            this.toast.add({ severity: 'warn', summary: 'Info Parcial', detail: 'Sesión iniciada pero no se pudieron cargar detalles completos.', life: 3000 });
            if (authService.token) {
              this.$router.push(this.redirectPath);
            } else {
              this.$router.push('/');
            }
          }
        } else {
          console.error("[Login.vue] Respuesta OK de API pero sin token.");
          this.toast.add({ severity: 'error', summary: 'Error de Respuesta', detail: 'No se recibió token del servidor.', life: 3000 });
          await authService.logout();
        }
      } catch (error: any) {
        console.error('[Login.vue] Error inesperado en el método login:', error);
        let errorMessage = 'Ocurrió un problema al intentar iniciar sesión.';

        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401) {
            errorMessage = 'Credenciales incorrectas. Por favor, verifica tu correo y contraseña.';
          } else if (error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message;
          }
        }

        this.toast.add({
          severity: 'error',
          summary: 'Error al iniciar sesión',
          detail: errorMessage,
          life: 3000,
        });
        if (authService.token) {
          await authService.logout();
        }
      }
    },
    togglePasswordVisibility() {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    },
  },
};
</script>

<style scoped>
.login-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  min-width: 500px;
  padding: 80px 20px 40px 20px;
  box-sizing: border-box;
}

.login-box {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  width: 100%;
  max-width: 500px !important;
  text-align: center;
}

.login-box h2 {
  color: var(--color-primary);
  margin-top: 0;
  margin-bottom: 30px;
  font-size: 1.8rem;
  font-weight: 700;
}

form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.input-label {
  font-size: 0.9rem;
  color: var(--color-text-dark);
  font-weight: 500;
  margin-bottom: 8px;
}

.input-field {
  border-radius: 8px;
  padding: 12px 15px;
  border: 1px solid var(--color-neutral-medium);
  background-color: white;
  color: var(--color-text-dark);
  width: 100%;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
  font-size: 1rem;
}

.input-field:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.input-field[aria-invalid="true"] {
  border-color: var(--color-error);
}

.input-field[aria-invalid="true"]:focus {
  box-shadow: 0 0 0 3px rgba(var(--color-error-rgb), 0.2);
}

.error-message {
  color: var(--color-error);
  font-size: 0.85rem;
  margin-top: 6px;
  min-height: 1.2em;
}

.submit-button {
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  font-size: 1.1rem;
  font-weight: bold;
  width: 100%;
  transition: background-color 0.3s ease, transform 0.1s ease;
  cursor: pointer;
  margin-top: 10px;
}

.submit-button:hover {
  background-color: var(--color-interactive-darker);
}

.submit-button:active {
  transform: scale(0.98);
}

.register-link {
  margin-top: 25px;
  font-size: 0.9rem;
  color: var(--color-text-dark);
}

.register-link a {
  color: var(--color-interactive);
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

:global(.p-toast .p-toast-message) {
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid transparent !important;
  border-left-width: 5px !important;
}

:global(.p-toast .p-toast-message-error) {
  background-color: #FFF1F2 !important;
  border-color: var(--color-error) !important;
}
:global(.p-toast .p-toast-message-error .p-toast-message-icon),
:global(.p-toast .p-toast-message-error .p-toast-summary),
:global(.p-toast .p-toast-message-error .p-toast-detail) {
  color: #B91C1C !important;
}

.password-input-wrapper {
  position: relative;
  width: 100%;
}

.password-toggle-icon {
  position: absolute;
  top: 50%;
  right: 15px;
  transform: translateY(-50%);
  cursor: pointer;
  color: var(--color-neutral-medium);
  transition: color 0.2s ease;
}

.password-toggle-icon:hover {
  color: var(--color-interactive);
}
</style>