<template>
  <MenuBar />
  <div class="incidencias-container">
    <div class="filters-container">
      <div class="unified-search">
        <label for="search-input">Buscar:</label>
        <div class="search-input-container">
          <input
              id="search-input"
              type="text"
              v-model="search"
              placeholder="GUID, Asunto, Usuario..."
              @input="handleFilterChange"
          />
          <div class="calendar-icon-container" @click="toggleCalendar">
            <i class="pi pi-calendar"></i>
            <span v-if="selectedDate" class="selected-date-indicator"></span>
          </div>
          <div v-if="showCalendar" class="calendar-dropdown">
            <Calendar
                id="date-filter-input"
                v-model="selectedDate"
                dateFormat="dd/mm/yy"
                placeholder="Selecciona una fecha"
                :showClear="true"
                @date-select="handleDateSelect"
                @clear="handleClearDate"
                inline
                class="p-calendar-custom"
            />
          </div>
        </div>
        <div v-if="selectedDate" class="active-filter-badge" @click="handleClearDate">
          {{ formatSelectedDate(selectedDate) }} <i class="pi pi-times"></i>
        </div>
      </div>
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
import Calendar from 'primevue/calendar';
import Button from 'primevue/button';

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
  components: { MenuBar, Calendar, Button },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      selectedDate: null as Date | null,
      showCalendar: false,
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
    document.addEventListener('click', this.clickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.clickOutside);
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
        this.filtrarYPaginar();
        this.loading = false;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
        this.loading = false;
      }
    },
    onPage(event: any) {
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.filtrarYPaginar();
    },
    filtrarYPaginar() {
      this.loading = true;
      let resultadosFiltrados = this.todosLosDatos;

      if (this.search) {
        const q = this.search.toLowerCase();
        resultadosFiltrados = resultadosFiltrados.filter(incidencia => {
          const guidMatch = incidencia.guid?.toLowerCase().startsWith(q) ?? false;
          const asuntoMatch = incidencia.asunto?.toLowerCase().startsWith(q) ?? false;
          const estadoMatch = incidencia.estadoIncidencia?.toLowerCase().startsWith(q) ?? false;
          const fechaCreacionMatch = incidencia.createdDate?.includes(q) ?? false;

          return guidMatch || asuntoMatch || estadoMatch ||
              fechaCreacionMatch ;
        });
      }

      if (this.selectedDate) {
        const selectedYear = this.selectedDate.getFullYear();
        const selectedMonth = (this.selectedDate.getMonth() + 1).toString().padStart(2, '0');
        const selectedDay = this.selectedDate.getDate().toString().padStart(2, '0');
        const selectedDateStringForCompare = `${selectedDay}-${selectedMonth}-${selectedYear}`;

        resultadosFiltrados = resultadosFiltrados.filter(incidencia => {
          if (!incidencia.createdDate) return false;

          const incidenciaDatePart = incidencia.createdDate.substring(0, 10);

          return incidenciaDatePart === selectedDateStringForCompare;
        });
      }

      this.totalRecords = resultadosFiltrados.length;
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = resultadosFiltrados.slice(inicio, fin);
      this.loading = false;
    },
    verIncidencia(incidencia: Incidencia) {
      console.log("Navegando a detalle de incidencia con estos datos:", incidencia);
      this.$router.push({
        name: 'IncidenciaDetalle',
        params: { guid: incidencia.guid }
      });
    },
    toggleCalendar() {
      this.showCalendar = !this.showCalendar;
    },
    handleFilterChange() {
      this.currentPage = 0;
      this.filtrarYPaginar();
    },
    handleClearDate() {
      this.selectedDate = null;
      this.handleFilterChange();
    },
    handleDateSelect() {
      this.handleFilterChange();
      this.showCalendar = false;
    },
    formatSelectedDate(date: Date): string {
      if (!date) return '';
      const day = date.getDate().toString().padStart(2, '0');
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const year = date.getFullYear();
      return `${day}/${month}/${year}`;
    },
    clickOutside(event: MouseEvent) {
      const target = event.target as HTMLElement;
      if (!target.closest('.calendar-dropdown') && !target.closest('.calendar-icon-container')) {
        this.showCalendar = false;
      }
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

.filters-container {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  max-width: 300px;
  justify-content: flex-start;
  margin-top: -73%;
}

.unified-search {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10px;
  margin-bottom: 20px;
  max-width: 500px;
}

.unified-search label {
  font-weight: 500;
  color: var(--color-text-dark);
  white-space: nowrap;
  margin-right: 8px;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input-container input {
  padding: 10px 35px 10px 15px;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  min-width: 235%;
  box-sizing: border-box;
}

.search-input-container input:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom.p-datepicker-inline) {
  font-family: 'Montserrat', sans-serif !important;
  font-size: 0.8rem;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-header) {
  padding: 0.4rem 0.2rem;
  font-family: 'Montserrat', sans-serif !important;
}
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-title select),
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-title button) {
  font-size: 0.8rem;
  font-family: 'Montserrat', sans-serif !important;
  padding: 0.2rem 0.3rem;
}
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-prev),
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-next) {
  width: 1.8rem;
  height: 1.8rem;
}
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-prev .p-icon),
.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-next .p-icon) {
  font-size: 0.7rem;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-calendar th) {
  padding: 0.3rem 0.2rem;
  font-size: 0.75rem;
  font-family: 'Montserrat', sans-serif !important;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-calendar td) {
  padding: 0.1rem;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-calendar td > span) {
  width: 2rem;
  height: 2rem;
  line-height: 2rem;
  font-size: 0.75rem;
  font-family: 'Montserrat', sans-serif !important;
  border-radius: 4px;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-calendar td > span.p-highlight) {
  background-color: var(--color-interactive) !important;
  color: white !important;
}

.calendar-dropdown :deep(.p-calendar.p-calendar-custom .p-datepicker-calendar td > span.p-datepicker-today) {
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
}

.calendar-icon-container {
  position: absolute;
  right: -325px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  cursor: pointer;
  color: var(--color-text-dark);
  opacity: 0.7;
  transition: opacity 0.2s ease, color 0.2s ease;
  z-index: 2;
}

.calendar-icon-container:hover {
  opacity: 1;
  color: var(--color-interactive);
}

.selected-date-indicator {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  background-color: var(--color-interactive);
  border-radius: 50%;
}

.calendar-dropdown {
  position: absolute;
  top: calc(100% + 5px);
  right: -400px;
  z-index: 1000;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  padding: 10px;
  border: 1px solid var(--color-neutral-medium);
}

.active-filter-badge {
  display: inline-flex;
  align-items: center;
  background-color: var(--color-accent-soft);
  border: 1px solid var(--color-interactive);
  color: var(--color-interactive-darker);
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 0.85rem;
  font-family: 'Montserrat', sans-serif;
  cursor: pointer;
  transition: background-color 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
  margin-left: 350px;
  margin-top: 5px;
  z-index: 5;
}

.active-filter-badge:hover {
  background-color: var(--color-text-on-dark);
}

.active-filter-badge i {
  margin-left: 6px;
  font-size: 0.8rem;
}

.filters :deep(.p-calendar.p-calendar-w-btn .p-inputtext:focus) {
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