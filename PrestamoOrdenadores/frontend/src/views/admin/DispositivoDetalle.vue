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
    <div class="iconoPortatil">
      <i class="pi pi-desktop"></i>
    </div>
    <div class="numeroSerie">
      <h2><strong>Numero de Serie:</strong> {{numeroSerie}}</h2>
    </div>
    <div class="componentes">
      <h5>Componentes</h5>
      <input type="text" id="componentes" v-model="componentes"/>
    </div>
    <div class="estado">
      <h5>Estado</h5>
      <select v-model="dispositivo.estado">
        <option value="DISPONIBLE">Disponible</option>
        <option value="NO_DISPONIBLE">No Disponible</option>
        <option value="PRESTADO">Prestado</option>
      </select>
    </div>
    <div class="incidencias">
      <h5>Incidencias</h5>
      <input type="text" id="incidencias" v-model="incidenciaGuid"/>
    </div>
    <button class="update-button" @click="actualizarDispositivo">
      Actualizar
    </button>
  </div>
</template>

<script lang="ts">
import {defineComponent, reactive} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import dispositivoService from '@/services/DispositivoService';

export default defineComponent({
  name: "DispositivoDetalle",
  components: {MenuBar},
  props: {
    guid: String,
    numeroSerie: String,
    componentes: String,
    estado: String,
    incidenciaGuid: String
  },
  mounted() {
    console.log("Dispositivo recibido:", this.guid, this.numeroSerie, this.componentes, this.estado, this.incidenciaGuid);
  },
  setup(props) {
    const dispositivo = reactive({
      componentes: props.componentes || '',
      estado: props.estado || 'Disponible',
      incidenciaGuid: props.incidenciaGuid || ''
    });

    const actualizarDispositivo = async () => {
      try {
        await dispositivoService.actualizarDispositivo(props.guid!, { ...dispositivo });
        alert("Dispositivo actualizado correctamente.");
      } catch (error) {
        alert("No se pudo actualizar el dispositivo.");
      }
    };

    return { dispositivo, actualizarDispositivo };
  }
})
</script>

<style scoped>
body{
  overflow-y: auto;
}

.boton-atras{
  margin-top: -2%;
  margin-left: -65%
}

.back-button {
  padding: 0.7rem 1rem;
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
  margin-left: -5%;
  margin-top: -15%;
}

.pi-desktop {
  font-size: 4.5rem;
  margin-left: 80%;
  margin-bottom: 5%;
  margin-top: 2%

}

.numeroSerie {
  margin-top: 0;
  font-size: 1.5rem;
}

.componentes, .estado, .incidencias {
  margin-top: 15px;
}

input, select{
  border-radius: 20px;
  padding: 8px;
  border: 1px solid #d1d3e2;
  width: 40%;
  transition: border 0.3s ease;
  outline: none;
}

input:focus, select:focus {
  border-color: #d6621e;
}

.update-button {
  margin-top: 20px;
  padding: 10px 15px;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  width: 35%;
  margin-left: 63%;
  transition: all 0.3s ease-in-out;
}

.update-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>