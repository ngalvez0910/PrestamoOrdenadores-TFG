<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/admin/dashboard/incidencias">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
    <button class="editIncidencia-button" @click="toggleEdit">
      <i class="pi pi-pencil"></i>
    </button>
  </div>
  <div class="incidencia-details" v-if="incidenciaData">
    <div class="iconoIncidencia">
      <i class="pi pi-exclamation-triangle"></i>
    </div>
    <div class="asunto">
      <h2><strong>{{ incidenciaData.asunto }}</strong></h2>
    </div>
    <div class="row">
      <div class="descripcion">
        <h6>Descripción: </h6>
        <textarea readonly type="text" id="descripcion" v-model="incidenciaData.descripcion"/>
      </div>
    </div>
    <div class="row">
      <div class="estado col-4">
        <h6>Estado: </h6>
        <div v-if="editable">
          <select id="estado" v-model="incidenciaData.estadoIncidencia">
            <option value="PENDIENTE">PENDIENTE</option>
            <option value="RESUELTO">RESUELTO</option>
          </select>
        </div>
        <div v-else>
          <input readonly type="text" id="estado" v-model="incidenciaData.estadoIncidencia"/>
        </div>
      </div>
      <div class="userGuid col-4">
        <h6>UserGuid: </h6>
        <input readonly type="text" id="userGuid" v-model="incidenciaData.userGuid"/>
      </div>
      <div class="creacion col-4">
        <h6>Fecha de Creación: </h6>
        <input readonly type="text" id="createdDate" v-model="incidenciaData.createdDate"/>
      </div>
    </div>
  </div>
  <transition name="fade">
    <button v-if="editable" class="update-button" @click="actualizarIncidencia">
      Actualizar
    </button>
  </transition>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {getIncidenciaByGuid, actualizarIncidencia} from "@/services/IncidenciaService";

export default defineComponent({
  name: "IncidenciaDetalle",
  components: {MenuBar},
  data() {
    return {
      incidenciaData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const incidencia = await getIncidenciaByGuid(guidString);
      this.incidenciaData = incidencia;
      console.log(incidencia)
    } catch (error) {
      console.error("Error al obtener los detalles de la incidencia:", error);
    }
  },
  methods: {
    toggleEdit() {
      this.editable = !this.editable;
    },
    async actualizarIncidencia() {
      if (JSON.stringify(this.incidenciaData) !== JSON.stringify(this.originalData)) {
        try {
          await actualizarIncidencia(this.incidenciaData.guid, {
            estadoIncidencia: this.incidenciaData.estadoIncidencia,
          });
          this.originalData = { ...this.incidenciaData };
          alert("Incidencia actualizada correctamente.");
          this.editable = false;
        } catch (error) {
          alert("No se pudo actualizar el dispositivo.");
        }
      }
    }
  }
})
</script>

<style scoped>
body{
  overflow-y: auto;
}

.boton-atras {
  margin-left: -70%;
  margin-top: 15%;
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

.incidencia-details {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  width: 300%;
  max-width: 600px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  margin-left: -10%;
  margin-top: -15%;
  margin-bottom: 7%;
}

.pi-exclamation-triangle {
  font-size: 4.5rem;
  margin-left: 80%;
  margin-bottom: 5%;
  margin-top: 2%
}

h6{
  font-weight: bold;
}

.asunto {
  margin-bottom: 4%;
  font-size: 1.5rem;
}

.estado, .creacion, .userGuid {
  margin-top: 15px;
}

input, select, textarea{
  border-radius: 20px;
  padding: 8px;
  border: 1px solid #d6621e;
  width: 100%;
  max-width: max-content;
  transition: border 0.3s ease;
  outline: none;
}

textarea{
  resize: none;
  width: 100%;
  max-width: 100%;
}

.editIncidencia-button {
  padding: 0.7rem 1rem;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  width: 9%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: -25%;
  margin-top: 25%;
}

.editIncidencia-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.editIncidencia-button i {
  pointer-events: none;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease-in-out;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.update-button {
  padding: 5px 15px;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  width: 15%;
  transition: all 0.3s ease-in-out;
  margin-left: 47%;
  margin-top: -25%;
  position: absolute;
}

.update-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>