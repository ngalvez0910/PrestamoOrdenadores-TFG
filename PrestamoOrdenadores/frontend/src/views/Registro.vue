<template>
  <MenuBarNoSession />

  <div class="register-container">
    <div class="register-box">
      <h1>Registro</h1>

      <form @submit.prevent="registerUser">
        <div class="row">
          <label for="numeroIdentificacion" class="input-label"><strong>Número de Identificación</strong></label>
          <input type="text" class="input-field" name="numeroIdentificacion" placeholder="Número de Identificación" v-model="form.numeroIdentificacion">
          <p class="error-message" v-if="errors.numeroIdentificacion">{{ errors.numeroIdentificacion }}</p>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="nombre" class="input-label"><strong>Nombre</strong></label>
            <input type="text" class="input-field" name="nombre" placeholder="Nombre" v-model="form.nombre">
            <p class="error-message" v-if="errors.nombre">{{ errors.nombre }}</p>
          </div>

          <div class="col-md-6 mb-3">
            <label for="apellidos" class="input-label"><strong>Apellidos</strong></label>
            <input type="text" class="input-field" name="apellidos" placeholder="Apellidos" v-model="form.apellidos">
            <p class="error-message" v-if="errors.apellidos">{{ errors.apellidos }}</p>
          </div>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="curso" class="input-label"><strong>Curso</strong></label>
            <input class="input-field" type="text" name="curso" placeholder="Curso"  v-model="form.curso">
            <p class="error-message" v-if="errors.curso">{{ errors.curso }}</p>
          </div>

          <div class="col-md-6 mb-3">
            <label for="tutor" class="input-label"><strong>Tutor</strong></label>
            <input class="input-field" type="text" name="tutor" placeholder="Tutor" v-model="form.tutor">
            <p class="error-message" v-if="errors.tutor">{{ errors.tutor }}</p>
          </div>
        </div>

        <div class="row mb-3">
          <label for="email" class="input-label"><strong>Correo electrónico</strong></label>
          <input class="input-field" type="email" name="email" placeholder="Correo electrónico" v-model="form.email" @blur="validateEmail">
          <p class="error-message" v-if="errors.email">{{ errors.email }}</p>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="password" class="input-label"><strong>Contraseña</strong></label>
            <input type="password" name="password" class="input-field" placeholder="Contraseña"  v-model="form.password">
            <p class="error-message" v-if="errors.password">{{ errors.password }}</p>
          </div>

          <div class="col-md-6 mb-3">
            <label for="confirmPassword" class="input-label"><strong>Confirmar contraseña</strong></label>
            <input type="password" name="confirmPassword" class="input-field" placeholder="Confirmar contraseña"  v-model="form.confirmPassword">
            <p class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</p>
          </div>
        </div>

        <button type="submit" :disabled="isSubmitting">Registrarse</button>
      </form>

      <p class="login-link"><a href="/">Login</a></p>

      <Toast />
    </div>
  </div>
</template>

<script lang="ts">
import MenuBarNoSession from "@/components/MenuBarNoSession.vue";
import Toast from 'primevue/toast';
import Button from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import { authService } from '@/services/AuthService.ts';

export default {
  name: 'Register',
  components: { MenuBarNoSession, Toast, Button },
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      form: {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        curso: '',
        tutor: '',
        email: '',
        password: '',
        confirmPassword: '',
      },
      errors: {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        email: '',
        curso: '',
        tutor: '',
        password: '',
        confirmPassword: '',
      },
      isSubmitting: false
    };
  },
  methods: {
    validateEmail() {
      if (!this.form.email || !/^[^\s@]+@loantech\.com$/.test(this.form.email)) {
        this.errors.email = 'El correo electrónico debe terminar en @loantech.com';
      } else {
        this.errors.email = '';
        this.validateEmailDependencies();
      }
    },
    validateEmailDependencies() {
      this.errors.curso = '';
      this.errors.tutor = '';
      if (this.form.email.endsWith('@loantech.com')) {
        if (!this.form.curso) {
          this.errors.curso = 'Campo obligatorio';
        }
        if (!this.form.tutor) {
          this.errors.tutor = 'Campo obligatorio';
        }
      } else if (this.form.email.endsWith('@profesor.loantech.com')) {
        if (!this.form.curso) {
          this.errors.curso = 'Campo obligatorio';
        }
      }
    },
    validateForm(): boolean {
      this.errors = {
        numeroIdentificacion: '',
        nombre: '',
        apellidos: '',
        curso: '',
        tutor: '',
        email: '',
        password: '',
        confirmPassword: '',
      };

      let formIsValid = true;

      if (!this.form.numeroIdentificacion) {
        this.errors.numeroIdentificacion = 'Campo obligatorio';
        formIsValid = false;
      }

      if (!this.form.nombre) {
        this.errors.nombre = 'Campo obligatorio';
        formIsValid = false;
      }

      if (!this.form.apellidos) {
        this.errors.apellidos = 'Campo obligatorio';
        formIsValid = false;
      }

      this.validateEmailDependencies();
      if (this.errors.curso) formIsValid = false;
      if (this.errors.tutor) formIsValid = false;

      if (this.form.password.length < 8 || !/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.form.password)) {
        this.errors.password = 'La contraseña debe contener: 8 caracteres, Mayúscula, 1 caracter especial, Número';
        formIsValid = false;
      }

      if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = 'Las contraseñas no coinciden';
        formIsValid = false;
      }

      return formIsValid;
    },
    async registerUser() {
      if (!this.validateForm()) {
        this.toast.add({
          severity: 'error',
          summary: 'Error en el Registro',
          detail: 'Por favor, corrija los errores y vuelva a intentarlo.',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
        return;
      }

      this.isSubmitting = true;

      const userData = {
        numeroIdentificacion: this.form.numeroIdentificacion,
        nombre: this.form.nombre,
        apellidos: this.form.apellidos,
        curso: this.form.curso,
        email: this.form.email,
        tutor: this.form.tutor,
        password: this.form.password,
        confirmPassword: this.form.confirmPassword,
      };

      try {
        const token = await authService.register(userData);
        if (token) {
          this.toast.add({
            severity: 'success',
            summary: 'Registro Exitoso',
            detail: 'El registro se completó y la sesión se inició automáticamente.',
            life: 3000,
            styleClass: 'custom-toast-success'
          });
          this.$router.push('/profile');
        } else {
          this.toast.add({
            severity: 'error',
            summary: 'Error en el Registro',
            detail: 'Hubo un problema al iniciar sesión automáticamente después del registro.',
            life: 5000,
            styleClass: 'custom-toast-error'
          });
        }
      } catch (error: any) {
        console.error('Error al registrar el usuario:', error.response?.data?.message || error.message);
        this.toast.add({
          severity: 'error',
          summary: 'Error en el Registro',
          detail: error.response?.data?.message || 'Hubo un error al registrar el usuario. Inténtelo de nuevo.',
          life: 5000,
          styleClass: 'custom-toast-error'
        });
      } finally {
        this.isSubmitting = false;
      }
    }
  },
};
</script>

<style>
body {
  font-family: 'Arial', sans-serif;
  background-color: #e6e8f0;
  color: #14124f;
}

.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 100vh;
  padding: 10px 0px;
  margin-top: 15%;
}

.register-box {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: max-content;
  max-width: 550px;
  box-sizing: border-box;
  margin-top: 55%;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.input-label {
  color: #14124f;
}

.input-field {
  border-radius: 30px;
  padding: 8px;
  border: 1px solid #d1d3e2;
  transition: border 0.3s ease;
  outline: none;
}

.input-field:focus {
  border-color: #d6621e;
}

button {
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 5px;
  font-size: 1.1rem;
  width: 50%;
  transition: background-color 0.3s ease;
  margin-bottom: 10px;
  margin-left: 50%;
}

button:hover {
  background-color: #a14916;
}

.login-link a {
  color: #14124f;
  text-decoration: none;
  justify-content: center;
  display: flex;
}

.login-link a:hover {
  background-color: inherit !important;
}

.error-message {
  color: red;
  margin-top: -2%;
}

.custom-toast-success, .custom-toast-error {
  background-color: white !important;
  border-radius: 10px !important;
  padding: 10px !important;
}

.custom-toast-success button, .custom-toast-error button {
  background-color: white !important;
  color: #14124f !important;
  width: 100% !important;
  margin-top: -75%
}
</style>