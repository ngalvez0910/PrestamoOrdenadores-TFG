<template>
  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>

      <label for="username" class="input-label">Nombre de usuario</label>
      <input type="text" class="input-field" name="username" placeholder="Nombre de usuario">

      <label for="password" class="input-label">Contraseña</label>
      <input type="password" name="password" class="input-field" placeholder="Contraseña">

      <button label="Login">Login</button>

      <p class="register-link"><a href="/registro">Registrarse</a></p>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'Login',
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
}

.login-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 500px;
}

h1{
  margin: 10px 0;
}

.input-label {
  font-size: 1rem;
  color: #14124f;
  font-weight: 500;
  margin-bottom: 8px;
}

.input-field {
  border-radius: 8px;
  padding: 14px;
  border: 1px solid #d1d3e2;
  width: 100%;
  font-size: 1rem;
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
  border-radius: 8px;
  padding: 14px;
  font-size: 1.1rem;
  width: 100%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-bottom: 20px;
}

button:hover {
  background-color: #a14916;
}

.register-link a {
  color: #14124f;
  text-decoration: none;
}

.register-link a:hover {
  background-color: inherit !important;
}

</style>