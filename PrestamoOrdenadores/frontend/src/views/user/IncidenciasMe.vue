<template>
  <AdminMenuBar/>
  <button class="back-button">
    <a href="/profile">
      <i class="pi pi-arrow-left"></i>
    </a>
  </button>

  <button class="buttonIncidencia" @click="crearIncidencia()" label="reportarIncidencia">Reportar Incidencia</button>

  <div style="margin-left: -40%; margin-top: 2%; width: 150%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable :value="incidencias" stripedRows tableStyle="min-width: 50rem">
        <Column field="asunto">
          <template #header>
            <b>Asunto</b>
          </template>
        </Column>
        <Column field="createdDate">
          <template #header>
            <b>Fecha Incidencia</b>
          </template>
        </Column>
        <Column field="estadoIncidencia">
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import {getIncidenciasByUserGuid, type Incidencia} from '@/services/IncidenciaService.ts';

export default defineComponent({
  name: "IncidenciasMe",
  components: { AdminMenuBar },
  setup() {
    const incidencias = ref<Incidencia[]>([]);

    onMounted(async () => {
      try {
        incidencias.value = await getIncidenciasByUserGuid();
        console.log("Datos de incidencias:", incidencias.value);
      } catch (error) {
        console.error("Error al obtener las incidencias:", error);
      }
    });

    return { incidencias, };
  },
  methods: {
    crearIncidencia() {
      console.log("Navegando a crear incidencia");
      this.$router.push({
        name: 'ReportarIncidencia'
      });
    }
  }
});
</script>

<style scoped>
.back-button {
  padding: 0.3rem 0.5rem;
  font-size: 0.875rem;
  background-color: #14124f;
  color: white;
  border: none;
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
  margin-left: -40%;
  margin-top: 40%;
  width: 5%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
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
  color: inherit !important;
}

.buttonIncidencia {
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 5px;
  font-size: 1.1rem;
  width: 25%;
  transition: all 0.3s ease-in-out;
  position: relative;
  top: -2%;
  left: 75%;
  margin-top: 0;
  margin-left: 0;
}


.buttonIncidencia:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>