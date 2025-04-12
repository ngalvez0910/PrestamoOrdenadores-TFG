<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
      Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="filterData" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 150%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
        <Column field="numeroSerie">
          <template #header>
            <b>Número de serie</b>
          </template>
        </Column>
        <Column field="componentes">
          <template #header>
            <b>Componentes</b>
          </template>
        </Column>
        <Column>
          <template #body="slotProps">
            {{ formatEstado(slotProps.data.estado) }}
          </template>
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
        <Column field="incidenciaGuid">
          <template #header>
            <b>Incidencias</b>
          </template>
        </Column>
        <Column field="edit">
          <template #body="slotProps">
            <button @click="editDispositivo(slotProps.data)" class="editDispositivo-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete">
          <template #body="slotProps">
            <button @click="deleteDispositivo(slotProps.data)" class="delete-button">
              <i class="pi pi-ban"></i>
            </button>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';

interface Dispositivo {
  guid: string;
  numeroSerie: string;
  componentes: string;
  estadoDispositivo: string;
  estado: string;
  incidencia: { guid: string } | null;
  incidenciaGuid: string | null;
  isActivo: boolean;
  createdDate: string;
  updatedDate: string;
}

export default {
  name: 'DispositivosDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Dispositivo[],
      filteredDatos: [] as Dispositivo[],
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    formatEstado(estado: 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO'): string {
      return estado.replace(/_/g, ' ');
    },
    async obtenerDatos() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const response = await axios.get(`http://localhost:8080/dispositivos`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        let dispositivos = response.data.content || response.data;

        if (!Array.isArray(dispositivos)) {
          dispositivos = [dispositivos];
        }

        this.datos = dispositivos.map((dispositivo: any) => {
          return {
            ...dispositivo,
            incidencia: dispositivo.incidencia ? { guid: dispositivo.incidencia.guid } : null,
            incidenciaGuid: dispositivo.incidencia ? dispositivo.incidencia.guid : null,
          };
        });

        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(dispositivo => {
        const searchText = this.search.toLowerCase();
        const numeroSerie = (dispositivo.numeroSerie || "").toLowerCase();
        const componentes = (dispositivo.componentes || "").toLowerCase();
        const estado = (dispositivo.estado || '').toLowerCase();
        const incidenciaGuid = (dispositivo.incidenciaGuid || "").toLowerCase();

        return (
            numeroSerie.includes(searchText) ||
            componentes.includes(searchText) ||
            estado.includes(searchText) ||
            incidenciaGuid.includes(searchText)
        );
      });
    },
    editDispositivo(dispositivo: Dispositivo) {
      this.$router.push({
        name: 'DispositivoDetalle',
        params: { guid: dispositivo.guid },
        query: {
          numeroSerie: dispositivo.numeroSerie,
          componentes: dispositivo.componentes,
          estado: dispositivo.estadoDispositivo,
          incidenciaGuid: dispositivo.incidenciaGuid
        }
      });
    }
  },
};
</script>

<style>
.filters {
  position: relative;
  width: 100%;
  padding: 1rem;
  display: flex;
  align-items: center;
}

.filters input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #d1d3e2;
  border-radius: 25px;
  margin-left: 2%;
  transition: border 0.3s ease;
  outline: none;
}

.filters input:focus {
  border-color: #d6621e;
}

.editDispositivo-button {
  padding: 0.5rem 0.8rem;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  margin-top: 20%;
  transition: all 0.3s ease-in-out;
  align-items: center;
  justify-content: center;
}

.editDispositivo-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.editDispositivo-button i {
  pointer-events: none;
  margin-top: 30%;
}

.delete-button {
  padding: 0.5rem 0.8rem;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20%
}

.delete-button:hover {
  background-color: #9b1616;
}

.delete-button i {
  pointer-events: none;
  margin-top: 30%;
}

a{
  background-color: inherit !important;
}
</style>