<template>
  <MenuBar />
  <div class="incidencias-container"> <div class="filters">
    <label for="search-input">Buscar:</label>
    <input id="search-input" type="text" v-model="search" placeholder="Buscar por GUID, Asunto, Estado, Usuario..." @input="handleSearchInput" />
  </div>

    <div class="table-wrapper">
      <DataTable
          :value="datos"
          paginator
          :rows="5"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          :totalRecords="totalRecords"
          :loading="loading"
          lazy @page="onPage"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Mostrando {first} a {last} de {totalRecords} incidencias"
          responsiveLayout="scroll"
          class="p-datatable-custom"
      >
        <Column field="guid" header="GUID" style="min-width: 150px;"></Column>
        <Column field="asunto" header="Asunto" style="min-width: 200px;"></Column>
        <Column field="estadoIncidencia" header="Estado" style="min-width: 120px;">
          <template #body="slotProps">
                 <span :class="['status-badge', getStatusClass(slotProps.data.estadoIncidencia)]">
                     {{ formatEstado(slotProps.data.estadoIncidencia) }}
                 </span>
          </template>
        </Column>
        <Column field="user.numeroIdentificacion" header="Usuario" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.user?.numeroIdentificacion || 'N/A' }}
          </template>
        </Column>
        <Column field="createdDate" header="Fecha Creación" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.createdDate }} </template>
        </Column>


        <Column header="Acciones" style="min-width:100px; width: 100px; text-align: center;">
          <template #body="slotProps">
            <div class="action-buttons">
              <button @click="verIncidencia(slotProps.data)" class="action-button view-button" title="Ver Detalles Incidencia">
                <i class="pi pi-info-circle"></i>
              </button>
              <button @click="deleteIncidencia(slotProps.data)" class="action-button delete-button" title="Eliminar Incidencia">
                <i class="pi pi-trash"></i> </button>
            </div>
          </template>
        </Column>

        <template #empty>
          No se encontraron incidencias.
        </template>
        <template #loading>
          Cargando datos de incidencias...
        </template>
      </DataTable>
    </div>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';

type IncidenceState = 'PENDIENTE' | 'RESUELTO';

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
    };
  },
  async mounted() {
    console.log("Componente montado. Llamando a obtenerDatos inicial...");
    await this.loadData();
    console.log("Datos iniciales y totalRecords cargados.");
  },
  methods: {
    formatEstado(estado: IncidenceState | undefined): string {
      if (!estado) return 'Desconocido';
      const formatted = estado.replace(/_/g, ' ');
      return formatted.charAt(0).toUpperCase() + formatted.slice(1).toLowerCase();
    },
    getStatusClass(estado: IncidenceState | undefined): string {
      if (!estado) return 'status-unknown';
      switch (estado) {
        case 'PENDIENTE': return 'status-pendiente';
        case 'RESUELTO': return 'status-resuelto';
        default: return 'status-unknown';
      }
    },
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
      const resultadosFiltrados = this.filtrarPorTexto(this.search);
      this.totalRecords = resultadosFiltrados.length;
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = resultadosFiltrados.slice(inicio, fin);
    },
    handleSearchInput() {
      this.currentPage = 0;
      this.paginar();
    },
    onPage(event: any) {
      this.loading = true;
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.paginar();
      setTimeout(() => { this.loading = false; }, 100);
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

<style scoped>
.incidencias-container {
  padding: 80px 30px 40px 30px;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
  max-width: 400px;
  margin-top: -80%;
}

.filters label {
  font-weight: 500;
  color: var(--color-text-dark);
  white-space: nowrap;
}

.filters input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  min-width: 250%;
}

.filters input:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.table-wrapper {
  position: fixed;
  width: 90%;
  overflow-x: auto;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  background-color: white;
  margin-left: -21%;
  margin-top: 1%;
}

:deep(.p-datatable-custom .p-datatable-thead > tr > th) {
  font-family: 'Montserrat', sans-serif;
  background-color: var(--color-background-main);
  color: var(--color-primary);
  font-weight: 600;
  padding: 1rem 1rem;
  border-bottom: 2px solid var(--color-neutral-medium);
  text-align: left;
  white-space: nowrap;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr) {
  font-family: 'Montserrat', sans-serif;
  color: var(--color-text-dark);
  transition: background-color 0.2s ease;
  border-bottom: 1px solid var(--color-background-main);
}

:deep(.p-datatable-custom .p-datatable-tbody > tr:last-child) {
  border-bottom: none;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr:hover) {
  background-color: var(--color-accent-soft) !important;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr > td) {
  padding: 0.9rem 1rem;
  vertical-align: middle;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
  text-align: center;
  display: inline-block;
  text-transform: uppercase;
}

.status-pendiente {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-resuelto {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
}
.action-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  padding: 6px;
  transition: all 0.3s ease-in-out;
  line-height: 1;
  width: 32px;
  height: 32px;
  box-sizing: border-box;
}

.action-button i {
  font-size: 1.1rem;
  display: block;
}

.view-button {
  background-color: var(--color-interactive);
  color: white;
}

.view-button:hover {
  background-color: var(--color-interactive-darker);
  transform: scale(1.1);
}

.delete-button {
  background-color: var(--color-error);
  color: white;
}

.delete-button:hover {
  background-color: #B91C1C;
  transform: scale(1.1);
}

:deep(.p-paginator) {
  font-family: 'Montserrat', sans-serif;
  background-color: var(--color-background-main) !important;
  border-top: 1px solid var(--color-neutral-medium) !important;
  padding: 0.75rem 1rem !important;
  border-radius: 0 0 8px 8px !important;
}

:deep(.p-paginator .p-paginator-element) {
  color: var(--color-text-dark) !important;
  background-color: transparent !important;
  border: 1px solid var(--color-neutral-medium) !important;
  border-radius: 6px !important;
  margin: 0 3px !important;
  min-width: 32px;
  height: 32px;
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

:deep(.p-paginator .p-paginator-element:not(.p-disabled):hover) {
  background-color: var(--color-accent-soft) !important;
  border-color: var(--color-interactive) !important;
  color: var(--color-interactive-darker) !important;
}

:deep(.p-paginator .p-paginator-element.p-highlight) {
  background-color: var(--color-interactive) !important;
  border-color: var(--color-interactive) !important;
  color: white !important;
  font-weight: 600;
}

:deep(.p-dropdown) {
  border: 1px solid var(--color-neutral-medium) !important;
  border-radius: 6px !important;
}

:deep(.p-dropdown:not(.p-disabled).p-focus) {
  border-color: var(--color-interactive) !important;
  box-shadow: 0 0 0 1px rgba(var(--color-interactive-rgb), 0.2) !important;
}

@media (max-width: 768px) {
  .incidencias-container {
    padding: 70px 15px 30px 15px;
  }
  .filters {
    max-width: none;
    flex-direction: column;
    align-items: stretch;
  }
  .filters label {
    margin-bottom: 5px;
  }
  :deep(.p-datatable-custom .p-datatable-thead > tr > th),
  :deep(.p-datatable-custom .p-datatable-tbody > tr > td) {
    padding: 0.75rem 0.5rem;
    white-space: normal;
    font-size: 0.9rem;
  }
  .action-buttons {
    gap: 8px;
  }
}
</style>