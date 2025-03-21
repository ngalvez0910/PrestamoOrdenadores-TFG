<template>
  <MenuBar />
  <div class="boton-atras">
    <a href="/admin/dashboard/dispositivos">
      <button class="back-button">
        <i class="pi pi-arrow-left"></i>
      </button>
    </a>
  </div>
  <div class="dispositivo-details">
    <div class="numeroSerie">
      <h2><strong>Numero de Serie:</strong> </h2>
    </div>
    <div class="componentes">
      <h5>Componentes</h5>
      <input readonly type="componentes" id="componentes" v-model="componentes"/>
    </div>
    <div class="estado">
      <h5>Estado</h5>
      <input readonly type="text" id="estado" v-model="estado"/>
    </div>
    <div class="incidencias">
      <h5>Incidencias</h5>
      <input readonly type="text" id="incidencias" v-model="incidencias"/>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";

export default defineComponent({
  name: "DispositivoDetalle",
  components: {MenuBar}
})
</script>

<style scoped>
.boton-atras{
  margin-top: -60%;
  margin-left: -165%
}

.back-button {
  padding: 0.8rem 0.9rem;
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

.back-button:hover {
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

.dispositivo-details {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  width: 300%;
  max-width: 600px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  margin-left: -60%;
  margin-top: -20%;
}

.numeroSerie {
  margin-top: 0;
  font-size: 1.5rem;
}

.componentes, .estado, .incidencias {
  margin-top: 15px;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}
</style>