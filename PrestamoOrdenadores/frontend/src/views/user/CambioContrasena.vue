<template>
  <AdminMenuBar/>

  <div class="botones-container">
    <div class="boton-atras">
      <a href="/profile">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
  </div>

  <div class="cambio-container">
    <div class="cambio-box">
      <h3>Cambio de Contraseña</h3>

      <label for="oldPassword" class="input-label"><strong>Contraseña antigua</strong></label>
      <input type="text" class="input-field" name="oldPassword" placeholder="Contraseña antigua">

      <label for="newPassword" class="input-label"><strong>Contraseña nueva</strong></label>
      <input type="password" name="newPassword" class="input-field" placeholder="Contraseña nueva">

      <label for="confirmPassword" class="input-label"><strong>Confirmar contraseña</strong></label>
      <input type="password" name="confirmPassword" class="input-field" placeholder="Confirmar contraseña">

      <button class="buttonCambio" label="Cambiar contraseña">Cambiar contraseña</button>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import AdminMenuBar from "@/components/AdminMenuBar.vue";

export default defineComponent({
  name: "CambioContrasena",
  components: {AdminMenuBar}
})
</script>

<style>
body {
  font-family: 'Arial',sans-serif;
  background-color: #e6e8f0;
  color: #14124f;
  margin-left: 28%;
  height: 100vh;
}

.boton-atras {
  margin-left: -70%;
  margin-top: 20%;
}

.back-button {
  padding: 0.7rem 1.2rem;
  font-size: 0.875rem;
  background-color: #14124f;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  margin-top: 20%;
  width: 5%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-button:hover{
  background-color: #0d0c34;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(72, 70, 159);
}

.back-button i {
  pointer-events: none;
}

a{
  background-color: inherit !important;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
  margin-top: 20%;
}

.cambio-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  margin-top: -30%;
}

.cambio-box {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 500px;
}

h3{
  text-align: center;
  margin-bottom: 6%;
}

.input-label {
  color: #14124f;
  font-weight: 500;
  margin-bottom: 8px;
}

.input-field {
  border-radius: 30px;
  padding: 8px;
  border: 1px solid #d1d3e2;
  width: 50%;
  margin-bottom: 18px;
  transition: border 0.3s ease;
  outline: none;
}

.input-field:focus {
  border-color: #d6621e;
}

.buttonCambio {
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

.buttonCambio:hover {
  background-color: #a14916;
}

</style>