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
            placeholder="email@loantech.com"
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
import MenuBarNoSession from "@/components/MenuBarNoSession.vue";
import Toast from "primevue/toast";
import Button from "primevue/button";
import { useToast } from "primevue/usetoast";
import axios from 'axios';
import * as jwt_decode from 'jwt-decode';

export default {
  name: 'Login',
  components: { MenuBarNoSession, Toast, Button },
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
    };
  },
  methods: {
    async login() {
      this.errors = {
        email: '',
        password: ''
      };

      let formIsValid = true;

      if (!this.form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = 'Correo electrónico inválido';
        formIsValid = false;
      }
      if (this.form.password.length < 8 || !/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.form.password)) {
        this.errors.password = 'Contraseña incorrecta';
        formIsValid = false;
      }

      if (formIsValid) {
        try {
          const response = await axios.post('http://localhost:8080/auth/signin', this.form);

          localStorage.setItem('token', response.data.token);
          const decodedToken: any = jwt_decode.jwtDecode(response.data.token);
          const rol = decodedToken.rol;

          console.log("Rol del usuario:", rol);

          if (rol === 'ADMIN') {
            this.$router.push('/admin/dashboard');
          } else {
            this.$router.push('/profile');
          }

        } catch (error) {
          console.error('Error al iniciar sesión:', error);
          this.toast.add({
            severity: 'error',
            summary: 'Error en el inicio de sesión',
            detail: 'Usuario o contraseña incorrectos.',
            life: 3000,
            styleClass: 'custom-toast-error'
          });
        }
      } else {
        this.toast.add({
          severity: 'error',
          summary: 'Error en el inicio de sesión',
          detail: 'Por favor, corrija los errores y vuelva a intentarlo.',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
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
  max-width: 600px !important;
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