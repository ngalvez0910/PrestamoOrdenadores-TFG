<template>
  <div class="login-container">
    <div class="login-box">
      <h1>Login</h1>

      <h3>Nombre de usuario</h3>
      <input class="username" type="text" name="username" placeholder="Nombre de usuario">

      <h3>Contraseña</h3>
      <input type="password" name="password" placeholder="Contraseña">

      <button label="Login">Login</button>

      <p><a href="/registro">Registrarse</a></p>
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

h1, h3{
  margin: 10px 0;
}

input {
  border-radius: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  height: 35px;
  width: 100%;
}

button {
  background-color: #d6621e;
  color: white;
  margin-top: 10%;
  margin-bottom: 10%;
  border: none;
  border-radius: 7px;
  height: 35px;
  width: 100%;
  cursor: pointer;
}

button:hover {
  background-color: #a14916;
}

a{
  color: #14124f;
}

a:hover{
  color: inherit !important;
  background-color: inherit !important;
}
</style>