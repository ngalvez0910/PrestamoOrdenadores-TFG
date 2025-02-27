<template>
  <div class="register-container">
    <div class="register-box">
      <h1>Registro</h1>

      <label for="name" class="input-label">Nombre</label>
      <input type="text" class="input-field" name="name" placeholder="Nombre">

      <label for="surname" class="input-label">Apellidos</label>
      <input type="text" class="input-field" name="surname" placeholder="Apellidos">

      <label for="email" class="input-label">Correo electrónico</label>
      <input class="input-field" type="email" name="email" placeholder="Correo electrónico">

      <label for="password" class="input-label">Contraseña</label>
      <input type="password" name="password" class="input-field" placeholder="Contraseña">

      <label for="passwordConfirm" class="input-label">Confirmar contraseña</label>
      <input type="password" name="passwordConfirm" class="input-field" placeholder="Confirmar contraseña">

      <label for="grade" class="input-label">Curso</label>
      <input class="input-field" type="text" name="grade" placeholder="Curso">

      <label for="image" class="input-label">Foto carnet de estudiante</label>
      <input class="input-field" type="file" name="image">

      <button label="Register">Registrarse</button>

      <p class="register-link"><a href="/">Login</a></p>
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
  margin-top: 18px;
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