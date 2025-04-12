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
        <Column field="guid">
          <template #header>
            <b>GUID</b>
          </template>
        </Column>
        <Column field="asunto">
          <template #header>
            <b>Asunto</b>
          </template>
        </Column>
        <Column field="estadoIncidencia">
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
        <Column field="userGuid">
          <template #header>
            <b>Usuario</b>
          </template>
        </Column>
        <Column field="createdDate">
          <template #header>
            <b>Fecha de incidencia</b>
          </template>
        </Column>
        <Column field="ver">
          <template #body="slotProps">
            <button @click="verIncidencia(slotProps.data)" class="verIncidencia-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete">
          <template #body="slotProps">
            <button @click="deleteIncidencia(slotProps.data)" class="deleteIncidencia-button">
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

interface Incidencia {
  guid: string;
  asunto: string;
  descripcion: string;
  estadoIncidencia: string;
  estado: string;
  user: { guid: string } | null;
  userGuid: string;
  createdDate: string;
  updatedDate: string;
}

export default {
  name: 'IncidenciasDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Incidencia[],
      filteredDatos: [] as Incidencia[]
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    async obtenerDatos() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const response = await axios.get(`http://localhost:8080/incidencias`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        console.log("Datos recibidos:", response.data);
        let incidencias = response.data.content || response.data;

        if (!Array.isArray(incidencias)) {
          incidencias = [incidencias];
        }

        this.datos = incidencias.map((incidencia: any) => {
          return {
            ...incidencia,
            user: incidencia.user ? { guid: incidencia.user.guid } : null,
            userGuid: incidencia.user ? incidencia.user.guid : null,
          };
        });

        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(incidencia => {
        const searchText = this.search.toLowerCase();
        const guid = (incidencia.guid || "").toLowerCase();
        const asunto = (incidencia.asunto || "").toLowerCase();
        const estado = (incidencia.estado || '').toLowerCase();
        const userGuid = (incidencia.userGuid || "").toLowerCase();

        return (
            guid.includes(searchText) ||
            asunto.includes(searchText) ||
            estado.includes(searchText) ||
            userGuid.includes(searchText)
        );
      });
    },
    verIncidencia(incidencia: Incidencia) {
      console.log("Navegando a detalle de incidencia con estos datos:", incidencia);
      this.$router.push({
        name: 'IncidenciaDetalle',
        params: { guid: incidencia.guid }
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

.verIncidencia-button {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
}

.verIncidencia-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.verIncidencia-button i {
  transform: scale(1.1);
}

.deleteIncidencia-button {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: -2%;
}

.deleteIncidencia-button:hover {
  background-color: #9b1616;
}

.deleteIncidencia-button i {
  margin-top: 1%;
  margin-left: 3%
}
</style>