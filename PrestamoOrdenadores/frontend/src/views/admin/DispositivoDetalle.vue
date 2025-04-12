<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/admin/dashboard/dispositivos">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
    <button class="editDispositivo-button" @click="toggleEdit">
      <i class="pi pi-pencil"></i>
    </button>
  </div>
  <div class="dispositivo-details" v-if="dispositivoData">
    <div class="iconoPortatil">
      <i class="pi pi-desktop"></i>
    </div>
    <div class="numeroSerie">
      <h2><strong>Numero de Serie:</strong> {{dispositivoData.numeroSerie}}</h2>
    </div>
    <div class="row">
      <div class="componentes col-6">
        <h5>Componentes</h5>
        <input :readonly="!editable" type="text" id="componentes" v-model="dispositivoData.componentes"/>
      </div>
      <div class="estado col-6">
        <h5>Estado</h5>
        <div v-if="editable">
          <select v-model="dispositivoData.estado">
            <option value="DISPONIBLE">Disponible</option>
            <option value="NO_DISPONIBLE">No Disponible</option>
            <option value="PRESTADO">Prestado</option>
          </select>
        </div>
        <div v-else>
          <input readonly type="text" :value="formatEstado(dispositivoData.estado)"/>
        </div>
      </div>
    </div>
    <div class="incidencias">
      <h5>Incidencias</h5>
      <input :readonly="!editable" type="text" id="incidencias" v-model="dispositivoData.incidenciaGuid"/>
    </div>
  </div>
  <transition name="fade">
    <button v-if="editable" class="update-button" @click="actualizarDispositivo">
      Actualizar
    </button>
  </transition>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import MenuBar from '@/components/AdminMenuBar.vue';
import {
  getDispositivoByGuid,
  actualizarDispositivo,
} from '@/services/DispositivoService';

export default defineComponent({
  name: 'DispositivoDetalle',
  components: { MenuBar },
  data() {
    return {
      dispositivoData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const dispositivo = await getDispositivoByGuid(guidString);
      this.dispositivoData = { ...dispositivo };
      this.originalData = { ...dispositivo };
      console.log(dispositivo);
    } catch (error) {
      console.error('Error al obtener los detalles del dispositivo:', error);
    }
  },
  methods: {
    formatEstado(estado: 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO' | undefined): string {
      if (!estado) {
        return '';
      }
      return estado.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    async actualizarDispositivo() {
      if (JSON.stringify(this.dispositivoData) !== JSON.stringify(this.originalData)) {
        try {
          await actualizarDispositivo(this.dispositivoData.guid, {
            componentes: this.dispositivoData.componentes,
            estadoDispositivo: this.dispositivoData.estadoDispositivo,
            incidenciaGuid: this.dispositivoData.incidenciaGuid,
          });
          this.originalData = { ...this.dispositivoData };
          alert('Dispositivo actualizado correctamente.');
          this.editable = false;
        } catch (error) {
          alert('No se pudo actualizar el dispositivo.');
        }
      }
    },
  },
});
</script>


<style scoped>
body{
  overflow-y: auto;
}

.boton-atras {
  margin-left: -70%;
  margin-top: -15%;
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

.dispositivo-details {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  width: 300%;
  max-width: 520px;
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
  width: 100%;
  max-width: max-content;
  transition: border 0.3s ease;
  outline: none;
}

input:focus, select:focus {
  border-color: #d6621e;
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

.editDispositivo-button {
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
  margin-right: -13%;
  margin-top: -7%;
}

.editDispositivo-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.editDispositivo-button i {
  pointer-events: none;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
  margin-top: 20%;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease-in-out;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>