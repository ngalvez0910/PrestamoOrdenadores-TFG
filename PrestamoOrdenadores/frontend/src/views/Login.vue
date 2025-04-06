<template>
  <MenuBarNoSession />

  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>

      <form @submit.prevent="login">
        <div class="row">
          <label for="email" class="input-label"><strong>Correo electrónico</strong></label>
          <input type="text" class="input-field" name="email" placeholder="Correo electrónico" v-model="form.email">
          <p class="error-message" v-if="errors.email">{{ errors.email }}</p>
        </div>

        <div class="row">
          <label for="password" class="input-label"><strong>Contraseña</strong></label>
          <input type="password" name="password" class="input-field" placeholder="Contraseña" v-model="form.password">
          <p class="error-message" v-if="errors.password">{{ errors.password }}</p>
        </div>

        <button type="submit">Login</button>
      </form>

      <p class="register-link"><a href="/registro">Registrarse</a></p>

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
      }
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
        this.errors.password = 'Contraseña inválida: 8 dígitos, 1 mayúscula, 1 caracter especial, 1 número';
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
            this.$router.push('/dashboard');
          }

          this.toast.add({
            severity: 'success',
            summary: 'Inicio de sesión exitoso',
            detail: '¡Has iniciado sesión correctamente!',
            life: 3000,
            styleClass: 'custom-toast-success'
          });
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
  },
};
</script>

<style>
body {
  font-family: 'Arial',sans-serif;
  background-color: #e6e8f0;
  color: #14124f;
  margin-left: 28%;
  height: 100vh;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  margin-top: 15%;
}

.login-box {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 500px;
}

h1{
  margin: 10px 0;
  text-align: center;
}

.input-label {
  font-size: 1rem;
  color: #14124f;
  margin-bottom: 8px;
  justify-content: left;
}

.input-field {
  border-radius: 30px;
  padding: 8px;
  border: 1px solid #d1d3e2;
  width: 100%;
  margin-bottom: 18px;
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

.register-link a {
  color: #14124f;
  text-decoration: none;
  justify-content: center;
  display: flex;
}

.register-link a:hover {
  background-color: inherit !important;
}

.error-message {
  color: red;
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