<template>
  <div class="register-container">
    <div class="register-box">
      <h1>Registro</h1>

      <h3>Nombre</h3>
      <input class="name" type="text" name="name" placeholder="Nombre">

      <h3>Apellidos</h3>
      <input class="surname" type="text" name="surname" placeholder="Apellidos">

      <h3>Nombre de usuario</h3>
      <input class="username" type="text" name="username" placeholder="Nombre de usuario">

      <h3>Contraseña</h3>
      <input type="password" name="password" placeholder="Contraseña">

      <h3>Correo electrónico</h3>
      <input class="email" type="email" name="email" placeholder="Correo electrónico">

      <h3>Curso</h3>
      <input class="grade" type="text" name="grade" placeholder="Curso">

      <h3>Foto carnet de estudiante</h3>
      <input class="image" type="file" name="image">

      <button label="Register">Registrarse</button>

      <p><a href="/">Login</a></p>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'Register',
  };
</script>

<style>
body {
  font-family: 'Arial',sans-serif;
  background-color: #e6e8f0;
  color: #14124f;
  margin-left: 28%;
  height: 100%;
}

.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  margin-top: 5%;
  margin-bottom: 5%;
}

.register-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 500px;
  box-sizing: border-box;
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