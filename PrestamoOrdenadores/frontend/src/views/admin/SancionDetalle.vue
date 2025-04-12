<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/admin/dashboard/sanciones">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
    <button class="editSancion-button" @click="toggleEdit">
      <i class="pi pi-pencil"></i>
    </button>
  </div>
  <div class="sancion-details" v-if="sancionData">
    <div class="iconoSancion">
      <i class="pi pi-ban"></i>
    </div>
    <div class="row">
      <div class="tipo col-6">
        <h6>Tipo: </h6>
        <div v-if="editable">
          <select id="tipo" v-model="sancionData.tipoSancion">
            <option value="ADVERTENCIA">ADVERTENCIA</option>
            <option value="BLOQUEO_TEMPORAL">BLOQUEO TEMPORAL</option>
            <option value="INDEFINIDO">INDEFINIDO</option>
          </select>
        </div>
        <div v-else>
          <input readonly type="text" id="tipo" :value="formatTipoSancion(sancionData.tipoSancion)"/>
        </div>
      </div>
      <div class="userGuid col-6">
          <h6>UserGuid: </h6>
          <input readonly type="text" id="userGuid" v-model="sancionData.userGuid"/>
      </div>
    </div>
    <div class="fecha row">
      <h6>Fecha de Sanción: </h6>
      <input readonly type="text" id="fechaSancion" v-model="sancionData.fechaSancion" style="margin-left: 2%;"/>
    </div>
  </div>
  <transition name="fade">
    <button v-if="editable" class="update-button" @click="actualizarSancion">
      Actualizar
    </button>
  </transition>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {getSancionByGuid, actualizarSancion} from "@/services/SancionService";

export default defineComponent({
  name: "SancionDetalle",
  components: {MenuBar},
  data() {
    return {
      sancionData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const sancion = await getSancionByGuid(guidString);
      this.sancionData = sancion;
      console.log(sancion)
    } catch (error) {
      console.error("Error al obtener los detalles de la sancion:", error);
    }
  },
  methods: {
    formatTipoSancion(tipoSancion: 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'INDEFINIDO' | undefined): string {
      if (!tipoSancion) {
        return '';
      }
      return tipoSancion.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    async actualizarSancion() {
      if (JSON.stringify(this.sancionData) !== JSON.stringify(this.originalData)) {
        try {
          await actualizarSancion(this.sancionData.guid, {
            tipoSancion: this.sancionData.tipoSancion,
          });
          this.originalData = { ...this.sancionData };
          alert("Sancion actualizada correctamente.");
          this.editable = false;
        } catch (error) {
          alert("No se pudo actualizar la sanción.");
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
  margin-top: -7%;
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

.sancion-details {
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

.pi-ban {
  font-size: 4.5rem;
  margin-left: 80%;
  margin-bottom: 5%;
  margin-top: 2%
}

h6{
  font-weight: bold;
}

.tipo, .fecha, .userGuid {
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

.editSancion-button {
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

.editSancion-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.editSancion-button i {
  pointer-events: none;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 8%;
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