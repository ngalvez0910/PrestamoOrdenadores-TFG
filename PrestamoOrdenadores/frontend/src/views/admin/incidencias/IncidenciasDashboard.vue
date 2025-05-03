<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
    Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="handleSearchInput" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 140%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable
          :value="datos"
          paginator
          :rows="5"
          :totalRecords="totalRecords"
          :lazy="true"
          @page="onPage"
          paginatorClass="custom-paginator"
      >
        <Column field="guid" style="width: 5%">
          <template #header>
            <b>GUID</b>
          </template>
        </Column>
        <Column field="asunto" style="width: 5%">
          <template #header>
            <b>Asunto</b>
          </template>
        </Column>
        <Column field="estadoIncidencia" style="width: 5%">
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
        <Column field="user.guid" style="width: 5%">
          <template #body="slotProps">
          {{ slotProps.data.user?.guid }} </template>
          <template #header>
            <b>Usuario</b>
          </template>
        </Column>
        <Column field="createdDate" style="width: 5%">
          <template #header>
            <b>Fecha de incidencia</b>
          </template>
        </Column>
        <Column field="ver" style="width: 3%">
          <template #body="slotProps">
            <button @click="verIncidencia(slotProps.data)" class="verIncidencia-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete" style="width: 3%">
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

interface PagedResponse {
  content: Incidencia[];
  totalElements: number;
}

export default {
  name: 'IncidenciasDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Incidencia[],
      todosLosDatos: [] as Incidencia[],
      totalRecords: 0,
      loading: false,
      currentPage: 0,
      pageSize: 5,
      filters: {}
    };
  },
  async mounted() {
    console.log("Componente montado. Llamando a obtenerDatos inicial...");
    await this.loadData();
    console.log("Datos iniciales y totalRecords cargados.");
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          this.loading = false;
          return;
        }

        const urlTotal = `http://localhost:8080/incidencias?page=0&size=1`;
        const responseTotal = await axios.get<PagedResponse>(urlTotal, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.totalRecords = responseTotal.data.totalElements;

        const urlAll = `http://localhost:8080/incidencias?page=0&size=${this.totalRecords}`;
        const responseAll = await axios.get<PagedResponse>(urlAll, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.todosLosDatos = responseAll.data.content;
        this.paginar();
        this.loading = false;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
        this.loading = false;
      }
    },
    paginar() {
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = this.filtrarPorTexto(this.search).slice(inicio, fin);
    },
    onPage(event: any) {
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.paginar();
    },
    handleSearchInput(event: Event) {
      const target = event.target as HTMLInputElement;
      this.search = target.value;
      this.currentPage = 0;

      const resultadosFiltrados = this.filtrarPorTexto(this.search);
      this.totalRecords = resultadosFiltrados.length;

      this.paginar();
    },
    filtrarPorTexto(query: string) {
      if (!query) return this.todosLosDatos;

      const q = query.toLowerCase();
      return this.todosLosDatos.filter(incidencia =>
          incidencia.guid?.toLowerCase().includes(q) ||
          incidencia.asunto?.toLowerCase().includes(q) ||
          incidencia.estado?.toLowerCase().includes(q) ||
          incidencia.userGuid?.toLowerCase().includes(q)
      );
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