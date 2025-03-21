<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -33%; margin-top: 10%">
    <p class="filter col-4">
      Nº Serie:
      <input type="text" v-model="searchNumber" placeholder="Buscar por nº de serie..." @input="filterData" />
    </p>
    <p class="filter col-4">
      <label for="state">Estado:</label>
      <select v-model="searchEstado" @input="filterData">
        <option value="Todos" selected>Todos</option>
        <option value="Disponible">Disponible</option>
        <option value="No Disponible">No Disponible</option>
        <option value="Prestado">Prestado</option>
      </select>
    </p>
    <p class="filter col-4">
      Stock:
      <input type="text" v-model="searchStock" placeholder="Buscar por stock..." @input="filterData" />
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -20%">
    <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
      <Column field="guid" header="GUID"></Column>
      <Column field="numeroSerie" header="Número de serie"></Column>
      <Column field="componentes" header="Componentes"></Column>
      <Column field="estadoDispositivo" header="Estado">
        <template #body="slotProps">
          {{ getEstadoDispositivo(slotProps.data.estado) }}
        </template>
      </Column>
      <Column field="incidenciaGuid" header="Incidencias"></Column>
      <Column field="stock" header="Stock"></Column>
      <Column field="edit"></Column>
    </DataTable>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";
import router from "@/router";
import axios from 'axios';

interface Dispositivo {
  guid: string;
  numeroSerie: string;
  componentes: string;
  estadoDispositivo: string;
  incidenciaGuid: string | null;
  stock: number;
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
      searchNumber: '',
      searchEstado: '',
      searchStock: '',
      datos: [] as Dispositivo[],
      filteredDatos: [] as Dispositivo[],
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    async obtenerDatos() {
      try {
        const response = await axios.get(`http://localhost:8080/dispositivos`);
        console.log("Datos recibidos:", response.data);
        this.datos = response.data.content || response.data;
        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    getEstadoDispositivo(estado: string): string {
      const estadosMap: Record<string, string> = {
        DISPONIBLE: "Disponible",
        NO_DISPONIBLE: "No Disponible",
        PRESTADO: "Prestado"
      };
      return estadosMap[estado] || estado;
    },
    filterData() {
      this.filteredDatos = this.datos.filter(dispositivo => {
        return (
            (this.searchNumber === '' || dispositivo.numeroSerie.toLowerCase().includes(this.searchNumber.toLowerCase())) &&
            (this.searchEstado === '' || dispositivo.estadoDispositivo.toLowerCase().includes(this.searchEstado.toLowerCase())) &&
            (this.searchStock === '' || dispositivo.stock.toString().includes(this.searchStock))
        );
      });
    },
  },
};
</script>

<style>
.filters {
  display: flex;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  margin-left: -20%;
  margin-top: -25%;
}

.filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter input {
  width: 200px;
  padding: 0.75rem;
  border: 1px solid #d6621e;
  border-radius: 25px;
}
</style>