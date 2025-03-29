<template>
  <MenuBarNoSession />

  <div class="register-container">
    <div class="register-box">
      <h1>Registro</h1>

      <form @submit.prevent="validateForm">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="name" class="input-label"><strong>Nombre</strong></label>
            <input type="text" class="input-field" name="name" placeholder="Nombre" v-model="form.name">
            <p class="error-message" v-if="errors.name">{{ errors.name }}</p>
          </div>

          <div class="col-md-6 mb-3">
            <label for="surname" class="input-label"><strong>Apellidos</strong></label>
            <input type="text" class="input-field" name="surname" placeholder="Apellidos" v-model="form.surname">
            <p class="error-message" v-if="errors.surname">{{ errors.surname }}</p>
          </div>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="grade" class="input-label"><strong>Curso</strong></label>
            <input class="input-field" type="text" name="grade" placeholder="Curso"  v-model="form.grade">
          </div>

          <div class="col-md-6 mb-3">
            <label for="email" class="input-label"><strong>Correo electrónico</strong></label>
            <input class="input-field" type="email" name="email" placeholder="Correo electrónico"  v-model="form.email">
            <p class="error-message" v-if="errors.email">{{ errors.email }}</p>
          </div>
        </div>

        <div class="row">
          <label for="image" class="input-label"><strong>Foto carnet de estudiante</strong></label>
          <input class="input-field" type="file" name="image" @change="handleFileUpload">
          <p class="error-message" v-if="errors.image">{{ errors.image }}</p>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="password" class="input-label"><strong>Contraseña</strong></label>
            <input type="password" name="password" class="input-field" placeholder="Contraseña"  v-model="form.password">
            <p class="error-message" v-if="errors.password">{{ errors.password }}</p>
          </div>

          <div class="col-md-6 mb-3">
            <label for="passwordConfirm" class="input-label"><strong>Confirmar contraseña</strong></label>
            <input type="password" name="passwordConfirm" class="input-field" placeholder="Confirmar contraseña"  v-model="form.passwordConfirm">
            <p class="error-message" v-if="errors.passwordConfirm">{{ errors.passwordConfirm }}</p>
          </div>
        </div>

        <button type="submit">Registrarse</button>
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
        name: '',
        surname: '',
        grade: '',
        email: '',
        password: '',
        passwordConfirm: ''
      },
      errors: {
        name: '',
        surname: '',
        email: '',
        password: '',
        passwordConfirm: '',
        image: ''
      }
    };
  },
  methods: {
    validateForm() {
      this.errors = {
        name: '',
        surname: '',
        email: '',
        password: '',
        passwordConfirm: '',
        image: ''
      };

      let formIsValid = true;

      if (!this.form.name) {
        this.errors.name = 'Campo obligatorio';
        formIsValid = false;
      }
      if (!this.form.surname) {
        this.errors.surname = 'Campo obligatorio';
        formIsValid = false;
      }
      if (!this.form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = 'Correo electrónico inválido';
        formIsValid = false;
      }
      if (this.form.password.length < 8 || !/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.form.password)) {
        this.errors.password = 'La contraseña debe tener al menos 8 caracteres';
        formIsValid = false;
      }
      if (this.form.password !== this.form.passwordConfirm) {
        this.errors.passwordConfirm = 'Las contraseñas no coinciden';
        formIsValid = false;
      }

      if (formIsValid) {
        this.toast.add({
          severity: 'success',
          summary: 'Registro Exitoso',
          detail: 'El registro ha sido completado exitosamente.',
          life: 3000,
          styleClass: 'custom-toast-success'
        });
      } else if (!formIsValid) {
        this.toast.add({
          severity: 'error',
          summary: 'Error en el Registro',
          detail: 'Por favor, corrija los errores y vuelva a intentarlo.',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
      }
    },
    handleFileUpload(event: Event) {
      const inputElement = event.target as HTMLInputElement;
      const file = inputElement.files?.[0];

      if (!file) {
        this.errors.image = 'Debe subir una imagen';
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
  padding: 20px 0;
  margin-top: 45%;
}

.register-box {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 100%;
  max-width: 500px;
  box-sizing: border-box;
  margin-top: 25%;
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
  margin-top: -10%;
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