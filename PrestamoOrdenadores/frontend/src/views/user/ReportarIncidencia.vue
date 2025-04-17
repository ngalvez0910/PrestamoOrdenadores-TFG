<template>
  <MenuBar />
  <div class="botones-container">
    <div class="boton-atras">
      <a href="/incidencias/me">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
  </div>
  <div class="incidencia-details">
    <div class="iconoIncidencia">
      <i class="pi pi-exclamation-triangle"></i>
    </div>
    <div class="row">
      <h6>Asunto</h6>
      <input type="text" id="asunto" v-model="asunto" />
    </div>
    <div class="row">
      <div class="descripcion">
        <h6>Descripción:</h6>
        <textarea type="text" id="descripcion" v-model="descripcion" />
      </div>
    </div>
    <button id="buttonIncidencia" @click="reportarIncidencia">Reportar Incidencia</button>
  </div>

  <Toast />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import MenuBar from '@/components/AdminMenuBar.vue';
import { createIncidencia, type Incidencia } from '@/services/IncidenciaService';
import { useToast } from 'primevue/usetoast';
import Toast from "primevue/toast";

export default defineComponent({
  name: 'ReportarIncidencia',
  components: { MenuBar },
  data() {
    return {
      asunto: '',
      descripcion: '',
      incidenciaData: null as Incidencia | null,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  methods: {
    async reportarIncidencia() {
      try {
        const incidenciaData = {
          asunto: this.asunto,
          descripcion: this.descripcion,
        };

        const response = await createIncidencia(incidenciaData);

        if (response) {
          console.log('Incidencia reportada con éxito:', response);
          this.toast.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Incidencia reportada correctamente',
            life: 3000 ,
            styleClass: 'custom-toast'
          });
          this.asunto = '';
          this.descripcion = '';
        } else {
          console.error('Error al reportar la incidencia.');
          this.toast.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo reportar la incidencia',
            life: 3000,
            styleClass: 'custom-toast-error'
          });
        }
      } catch (error) {
        console.error('Error al reportar la incidencia:', error);
        this.toast.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo reportar la incidencia',
          life: 3000,
          styleClass: 'custom-toast-error'
        });
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
  margin-left: -150%;
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
  margin-left: -60%;
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

#asunto {
  margin-bottom: 4%;
  margin-left: 2%;
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
  margin-bottom: 20px;
}

#buttonIncidencia{
  background-color: #d6621e;
  color: white;
  border: none;
  transition: all 0.3s ease-in-out;
}

#buttonIncidencia:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.custom-toast-success, .custom-toast-error {
  background-color: white !important;
  border-radius: 10px !important;
  padding: 10px !important;
}

.custom-toast-success button, .custom-toast-error button {
  background-color: white !important;
  color: #14124f !important;
  width: 100% !important;
  margin-top: -75%
}
</style>