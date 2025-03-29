<template>
  <MenuBarNoSession />

  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>

      <label for="username" class="input-label"><strong>Nombre de usuario</strong></label>
      <input type="text" class="input-field" name="username" placeholder="Nombre de usuario">

      <label for="password" class="input-label"><strong>Contraseña</strong></label>
      <input type="password" name="password" class="input-field" placeholder="Contraseña">

      <button label="Login">Login</button>

      <p class="register-link"><a href="/registro">Registrarse</a></p>
    </div>
  </div>
</template>

<script lang="ts">
  import MenuBarNoSession from "@/components/MenuBarNoSession.vue";

  export default {
    name: 'Login',
    components: {MenuBarNoSession},
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

</style>