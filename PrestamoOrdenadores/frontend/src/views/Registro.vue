<template>
  <MenuBarNoSession />

  <div class="register-container">
    <div class="register-box">
      <h1>Registro</h1>

      <div class="row">
        <div class="col-md-6 mb-3">
          <label for="name" class="input-label"><strong>Nombre</strong></label>
          <input type="text" class="input-field" name="name" placeholder="Nombre">
        </div>

        <div class="col-md-6 mb-3">
          <label for="surname" class="input-label"><strong>Apellidos</strong></label>
          <input type="text" class="input-field" name="surname" placeholder="Apellidos">
        </div>
      </div>

      <div class="row">
        <div class="col-md-6 mb-3">
          <label for="grade" class="input-label"><strong>Curso</strong></label>
          <input class="input-field" type="text" name="grade" placeholder="Curso">
        </div>

        <div class="col-md-6 mb-3">
          <label for="email" class="input-label"><strong>Correo electrónico</strong></label>
          <input class="input-field" type="email" name="email" placeholder="Correo electrónico">
        </div>
      </div>

      <div class="row">
        <label for="image" class="input-label"><strong>Foto carnet de estudiante</strong></label>
        <input class="input-field" type="file" name="image">
      </div>

      <div class="row">
        <div class="col-md-6 mb-3">
          <label for="password" class="input-label"><strong>Contraseña</strong></label>
          <input type="password" name="password" class="input-field" placeholder="Contraseña">
        </div>

        <div class="col-md-6 mb-3">
          <label for="passwordConfirm" class="input-label"><strong>Confirmar contraseña</strong></label>
          <input type="password" name="passwordConfirm" class="input-field" placeholder="Confirmar contraseña">
        </div>
      </div>

      <button label="Register">Registrarse</button>

      <p class="login-link"><a href="/">Login</a></p>
    </div>
  </div>
</template>

<script lang="ts">
  import MenuBarNoSession from "@/components/MenuBarNoSession.vue";

  export default {
    name: 'Register',
    components: {MenuBarNoSession},
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
</style>