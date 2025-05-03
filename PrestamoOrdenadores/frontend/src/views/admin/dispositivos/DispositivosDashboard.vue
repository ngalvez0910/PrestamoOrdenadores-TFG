<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 37%">
      Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="handleSearchInput" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; margin-bottom: 5%; width: 140%; height: 600px;">
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
        <Column field="numeroSerie" style="width: 15%">
          <template #header>
            <b>Número de serie</b>
          </template>
        </Column>
        <Column field="componentes" style="width: 15%">
          <template #header>
            <b>Componentes</b>
          </template>
        </Column>
        <Column style="width: 15%">
          <template #body="slotProps">
            {{ formatEstado(slotProps.data.estado) }}
          </template>
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
        <Column field="incidencia.guid" style="width: 15%">
          <template #body="slotProps">
            {{ slotProps.data.incidencia?.guid }} </template>
          <template #header>
            <b>Incidencias</b>
          </template>
        </Column>
        <Column field="edit" style="width: 7%">
          <template #body="slotProps">
            <button @click="editDispositivo(slotProps.data)" class="editDispositivo-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete" style="width: 5%">
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
  estado: 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO';
  incidencia: { guid: string } | null;
  incidenciaGuid: string | null;
  isActivo: boolean;
  createdDate: string;
  updatedDate: string;
}

interface PagedResponse {
  content: Dispositivo[];
  totalElements: number;
}

export default {
  name: 'DispositivosDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Dispositivo[],
      todosLosDatos: [] as Dispositivo[],
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
    formatEstado(estado: 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO'): string {
      return estado.replace(/_/g, ' ');
    },
    async loadData() {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const urlTotal = `http://localhost:8080/dispositivos?page=0&size=1`;
        const responseTotal = await axios.get<PagedResponse>(urlTotal, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.totalRecords = responseTotal.data.totalElements;

        const urlAll = `http://localhost:8080/dispositivos?page=0&size=${this.totalRecords}`;
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
    filtrarPorTexto(query: string): Dispositivo[] {
      if (!query) {
        return this.todosLosDatos;
      }

      const q = query.toLowerCase();

      return this.todosLosDatos.filter(dispositivo => {
        const numeroSerieMatch = dispositivo.numeroSerie?.toLowerCase().startsWith(q) ?? false;

        const componentesMatch = dispositivo.componentes?.toLowerCase().startsWith(q) ?? false;

        const estadoFormateado = this.formatEstado(dispositivo.estado);
        const estadoMatch = estadoFormateado?.toLowerCase().startsWith(q) ?? false;

        const incidenciaMatch = dispositivo.incidencia?.guid?.toLowerCase().startsWith(q) ?? false;

        return numeroSerieMatch || componentesMatch || estadoMatch || incidenciaMatch;
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
  margin-left: -5%;
}

.editDispositivo-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.editDispositivo-button i {
  transform: scale(1.1);
}

.delete-button {
  padding: 0.5rem 0.8rem;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  margin-top: 20%;
  transition: all 0.3s ease-in-out;
}

.delete-button:hover {
  background-color: #9b1616;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(214, 30, 30);
}

.delete-button i {
  pointer-events: none;
  margin-top: 30%;
}

a{
  background-color: inherit !important;
}

.p-paginator-pages {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 5px;
}

.p-paginator-pages button {
  background-color: #a6a6a6;
  color: #ffffff;
  padding: 0.5rem 0.5rem;
  margin: 0 5px;
  border-radius: 80%;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  display: flex;
  justify-content: center;
  align-items: center;
}

.p-paginator-pages button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.p-paginator-pages .p-highlight {
  background-color: #d6621e !important;
  color: white;
  font-weight: bold;
  transform: scale(1.05);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.p-paginator-first,
.p-paginator-prev,
.p-paginator-next,
.p-paginator-last {
  background-color: #d6621e;
  color: #ffffff;
  border-radius: 40px;
  padding: 0.5rem 0.75rem;
  margin: 0 5px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  max-width: 2%
}

.p-paginator-first:hover,
.p-paginator-prev:hover,
.p-paginator-next:hover,
.p-paginator-last:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>