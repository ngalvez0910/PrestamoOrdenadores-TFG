<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -35%; margin-top: 20%">
    <div class="filter col-3" style="margin-left: 2%;">
      <label for="username">Usuario:</label>
      <input id="username" type="text" v-model="searchUser" placeholder="Buscar por usuario..." @input="filterData"/>
    </div>
    <div class="filter col-3" style="margin-left: -4%;">
      <label for="asunto">Asunto:</label>
      <input id="asunto" type="text" v-model="searchAsunto" placeholder="Buscar por asunto..." @input="filterData"/>
    </div>
    <p class="filter col-3" style="margin-left: -5%; margin-top: 1%">
      Fecha:
      <input type="date" v-model="searchDate" @input="filterData"/>
    </p>
    <p class="filter col-3" style="margin-left: -5%; margin-top: 1%">
      <label for="state">Estado:</label>
      <select id="state" type="text" v-model="searchState" @input="filterData">
        <option value="">Todos</option>
        <option value="Pendiente">Pendiente</option>
        <option value="Resuelto">Resuelto</option>
      </select>
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -25%;">
    <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
      <Column field="guid" header="GUID"></Column>
      <Column field="asunto" header="Asunto"></Column>
      <Column field="estadoIncidencia" header="Estado"></Column>
      <Column field="userGuid" header="Usuario"></Column>
      <Column field="createdDate" header="Fecha de incidencia"></Column>
      <Column field="edit"></Column>
      <Column field="ver">
        <template #body="slotProps">
          <button @click="verIncidencia(slotProps.data)" class="verIncidencia-button">
            <i class="pi pi-eye"></i>
          </button>
        </template>
      </Column>
      <Column field="edit">
        <template #body="slotProps">
          <button @click="editIncidencia(slotProps.data)" class="editIncidencia-button">
            <i class="pi pi-pencil"></i>
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
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';

interface Incidencia {
  guid: string;
  asunto: string;
  descripcion: string;
  estadoIncidencia: string;
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
      searchUser: '',
      searchAsunto: '',
      searchDate: '',
      searchState: '',
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
        const response = await axios.get(`http://localhost:8080/incidencias`);
        console.log("Datos recibidos:", response.data);
        this.datos = response.data.content || response.data;
        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(incidencia => {
        const formattedSearchDate = this.searchDate ? this.searchDate.split('-').reverse().join('-') : '';
        const incidenciaFecha = incidencia.createdDate.split(' ')[0];
        return (
            (this.searchUser === '' || incidencia.userGuid.toLowerCase().startsWith(this.searchUser.toLowerCase())) &&
            (this.searchAsunto === '' || incidencia.asunto.toLowerCase().startsWith(this.searchAsunto.toLowerCase())) &&
            (this.searchState === '' || incidencia.estadoIncidencia.toString().includes(this.searchState)) &&
            (formattedSearchDate === '' || incidenciaFecha === formattedSearchDate)
        );
      });
    },
  },
};
</script>

<style>
body{
  overflow-x: hidden;
}

.filters {
  display: flex;
  align-items: center;
  margin-top: -15%;
}

.filter {
  display: flex;
  align-items: center;
}

.filter input, select {
  width: 200px;
  padding: 0.75rem;
  border: 1px solid #d6621e;
  border-radius: 25px;
}

.editIncidencia-button, .verIncidencia-button {
  padding: 0.5rem 0.6rem;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20%
}

.editIncidencia-button:hover, .verIncidencia-button:hover {
  background-color: #a14916;
}

.editIncidencia-button i, .verIncidencia-button i {
  pointer-events: none;
  margin-top: 30%;
}

.deleteIncidencia-button {
  padding: 0.5rem 0.6rem;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20%
}

.deleteIncidencia-button:hover {
  background-color: #9b1616;
}

.deleteIncidencia-button i {
  pointer-events: none;
  margin-top: 30%;
}
</style>