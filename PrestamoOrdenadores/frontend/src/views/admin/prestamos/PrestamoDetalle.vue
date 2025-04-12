<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/admin/dashboard/prestamos">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
  </div>
  <div class="prestamo-details" v-if="prestamoData">
    <div class="iconoPrestamo">
      <i class="pi pi-list-check"></i>
    </div>
    <div class="row">
      <div class="userGuid col-4">
        <h6>UserGuid: </h6>
        <input readonly type="text" id="userGuid" v-model="prestamoData.userGuid"/>
      </div>
      <div class="dispositivoGuid col-4">
        <h6>DispositivoGuid: </h6>
        <input readonly type="text" id="dispositivoGuid" v-model="prestamoData.dispositivoGuid"/>
      </div>
    </div>
    <div class="row">
      <div class="estado col-4">
        <h6>Estado: </h6>
        <div v-if="editable">
          <select id="estado" v-model="prestamoData.estadoPrestamo">
            <option value="VENCIDO">VENCIDO</option>
            <option value="EN_CURSO">EN CURSO</option>
            <option value="CANCELADO">CANCELADO</option>
          </select>
        </div>
        <div v-else>
          <input readonly type="text" id="estado" :value="formatEstado(prestamoData.estadoPrestamo)"/>
        </div>
      </div>
      <div class="creacion col-4">
        <h6>Fecha de Préstamo: </h6>
        <input readonly type="text" id="fechaPrestamo" v-model="prestamoData.fechaPrestamo"/>
      </div>
      <div class="devolucion col-4">
        <h6>Fecha de Devolución: </h6>
        <input readonly type="text" id="fechaDevolucion" v-model="prestamoData.fechaDevolucion"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {getPrestamoByGuid} from "@/services/PrestamoService.ts";

export default defineComponent({
  name: "PrestamoDetalle",
  components: {MenuBar},
  data() {
    return {
      prestamoData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const prestamo = await getPrestamoByGuid(guidString);
      this.prestamoData = prestamo;
      console.log(prestamo)
    } catch (error) {
      console.error("Error al obtener los detalles de la prestamo:", error);
    }
  },
  methods: {
    formatEstado(estadoPrestamo: 'VENCIDO' | 'EN_CURSO' | 'CANCELADO' | undefined): string {
      if (!estadoPrestamo) {
        return '';
      }
      return estadoPrestamo.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    /*
    async actualizarPrestamo() {
      if (JSON.stringify(this.prestamoData) !== JSON.stringify(this.originalData)) {
        try {
          await actualizarPrestamo(this.prestamoData.guid, {
            estadoPrestamo: this.prestamoData.estadoPrestamo,
          });
          this.originalData = { ...this.prestamoData };
          alert("Prestamo actualizada correctamente.");
          this.editable = false;
        } catch (error) {
          alert("No se pudo actualizar el dispositivo.");
        }
      }
    }*/
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
  margin-top: -80%;
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

.prestamo-details {
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

.pi-list-check {
  font-size: 4.5rem;
  margin-left: 80%;
  margin-bottom: 5%;
  margin-top: 2%
}

h6{
  font-weight: bold;
}

.estado, .creacion, .userGuid, .dispositivoGuid, .devolucion {
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

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 13%;
}
</style>