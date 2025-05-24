<template>
  <div class="sanciones-container">
    <div class="filters-container">
      <div class="unified-search">
        <label for="search-input">Buscar:</label>
        <div class="search-input-container">
          <input
              id="search-input"
              type="text"
              v-model="search"
              placeholder="GUID, Usuario, Tipo..."
              @input="handleFilterChange"
          />
          <div class="calendar-icon-container" @click="toggleCalendar">
            <i class="pi pi-calendar"></i>
            <span v-if="selectedDate" class="selected-date-indicator"></span>
          </div>
          <div v-if="showCalendar" class="calendar-dropdown" ref="calendarDropdown">
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
          currentPageReportTemplate="Mostrando {first} a {last} de {totalRecords} sanciones"
          responsiveLayout="scroll"
          class="p-datatable-custom"
      >
        <Column field="guid" header="GUID" style="min-width: 150px;"></Column>
        <Column field="user.numeroIdentificacion" header="Usuario" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.user?.numeroIdentificacion || 'N/A' }}
          </template>
        </Column>
        <Column field="prestamo.guid" header="Préstamo" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.prestamo?.guid || 'N/A' }}
          </template>
        </Column>
        <Column field="tipoSancion" header="Tipo" style="min-width: 150px;">
          <template #body="slotProps">
                 <span :class="['status-badge', getStatusClass(slotProps.data.tipoSancion)]">
                     {{ formatTipoSancion(slotProps.data.tipoSancion) }}
                 </span>
          </template>
        </Column>
        <Column field="fechaSancion" header="Fecha Sanción" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.fechaSancion }} </template>
        </Column>
        <Column field="fechaFin" header="Fecha Fin" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.fechaFin }} </template>
        </Column>


        <Column header="Acciones" style="min-width:100px; width: 100px; text-align: center;">
          <template #body="slotProps">
            <div class="action-buttons">
              <button @click="verSancion(slotProps.data)" class="action-button view-button" title="Ver Detalles Sanción">
                <i class="pi pi-info-circle"></i>
              </button>
              <button @click="deleteSancion(slotProps.data)" class="action-button delete-button" title="Eliminar Sanción">
                <i class="pi pi-trash"></i> </button>
            </div>
          </template>
        </Column>

        <template #empty>
          No se encontraron sanciones.
        </template>
        <template #loading>
          Cargando datos de sanciones...
        </template>
      </DataTable>
    </div>
  </div>

  <Dialog v-model:visible="showDeleteDialog" header="Confirmar Eliminación" modal :draggable="false" :style="{ width: '50vw', fontFamily: 'Montserrat, sans-serif' }">
    <div v-if="sancionToDelete">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem;"/><br>
      <span>¿Está seguro de que desea eliminar la sanción con guid <strong>{{ sancionToDelete.guid }}</strong> del usuario <strong>{{ sancionToDelete.user?.numeroIdentificacion || 'N/A' }}</strong>? Esta acción no se puede deshacer.</span>
    </div>
    <template #footer>
      <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button-dialog secondary-button" @click="showDeleteDialog = false" />
      <Button label="Eliminar" class="action-button-dialog primary-button" severity="danger" @click="confirmDelete" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import axios from 'axios';
import Calendar from 'primevue/calendar';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import Dialog from "primevue/dialog";
import {deleteSancion} from "@/services/SancionService.ts";

type SanctionType = 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'INDEFINIDO';

interface Sancion {
  guid: string;
  tipoSancion: string;
  tipo: string;
  user: { guid: string, numeroIdentificacion: string } | null;
  userGuid: string;
  prestamo: { guid: string } | null;
  prestamoGuid: string;
  fechaSancion: string;
  fechaFin: string;
  createdDate: string;
  updatedDate: string;
  isDeleted: boolean;
}

interface PagedResponse {
  content: Sancion[];
  totalElements: number;
}

export default {
  name: 'SancionDashboard',
  components: { Calendar, Button, Dialog, Toast },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      selectedDate: null as Date | null,
      showCalendar: false,
      datos: [] as Sancion[],
      todosLosDatos: [] as Sancion[],
      totalRecords: 0,
      loading: false,
      currentPage: 0,
      pageSize: 5,
      showDeleteDialog: false,
      sancionToDelete: null as Sancion | null
    };
  },
  async mounted() {
    console.log("Componente montado. Llamando a obtenerDatos inicial...");
    await this.loadData();
    console.log("Datos iniciales y totalRecords cargados.");
    document.addEventListener('mousedown', this.handleDocumentClick);
  },
  beforeUnmount() {
    document.removeEventListener('mousedown', this.handleDocumentClick);
  },
  methods: {
    formatTipoSancion(tipoSancion: 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'INDEFINIDO'): string {
      return tipoSancion.replace(/_/g, ' ');
    },
    getStatusClass(tipo: SanctionType | undefined): string {
      if (!tipo) return 'status-unknown';
      switch (tipo) {
        case 'ADVERTENCIA': return 'status-advertencia';
        case 'BLOQUEO_TEMPORAL': return 'status-bloqueo';
        case 'INDEFINIDO': return 'status-indefinido';
        default: return 'status-unknown';
      }
    },
    async loadData() {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const urlTotal = `http://localhost:8080/sanciones?page=0&size=1`;
        const responseTotal = await axios.get<PagedResponse>(urlTotal, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.totalRecords = responseTotal.data.totalElements;

        const urlAll = `http://localhost:8080/sanciones?page=0&size=${this.totalRecords}`;
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
    filtrarYPaginar() {
      this.loading = true;
      let resultadosFiltrados = this.todosLosDatos;

      if (this.search) {
        const q = this.search.toLowerCase();
        resultadosFiltrados = resultadosFiltrados.filter(sancion => {
          const guidMatch = sancion.guid?.toLowerCase().startsWith(q) ?? false;
          const userMatch = sancion.userGuid?.toLowerCase().startsWith(q) ?? false;
          const prestamoMatch = sancion.prestamo?.guid?.toLowerCase().startsWith(q) ?? false;
          const tipoMatch = sancion.tipo?.toLowerCase().startsWith(q) ?? false;
          const fechaSancionMatch = sancion.fechaSancion?.includes(q) ?? false;
          const fechaFinMatch = sancion.fechaFin?.includes(q) ?? false;

          return guidMatch || userMatch || prestamoMatch || tipoMatch || fechaSancionMatch || fechaFinMatch;
        });
      }

      if (this.selectedDate) {
        const day = this.selectedDate.getDate().toString().padStart(2, '0');
        const month = (this.selectedDate.getMonth() + 1).toString().padStart(2, '0');
        const year = this.selectedDate.getFullYear();
        const selectedDateStringDdMmYyyy = `${day}-${month}-${year}`;

        resultadosFiltrados = resultadosFiltrados.filter(sancion => {
          const fechaSancionMatch = sancion.fechaSancion === selectedDateStringDdMmYyyy;
          const fechaFinMatch = sancion.fechaFin === selectedDateStringDdMmYyyy;
          return fechaSancionMatch || fechaFinMatch;
        });
      }

      this.totalRecords = resultadosFiltrados.length;
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = resultadosFiltrados.slice(inicio, fin);
      this.loading = false;
    },
    verSancion(sancion: Sancion) {
      console.log("Navegando a detalle de sancion con estos datos:", sancion);
      this.$router.push({
        name: 'SancionDetalle',
        params: { guid: sancion.guid }
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
    onPage(event: any) {
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.filtrarYPaginar();
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
    handleDocumentClick(event: MouseEvent) {
      if (!this.showCalendar) return;

      const calendarDropdown = this.$refs.calendarDropdown as HTMLElement;

      if (calendarDropdown && !calendarDropdown.contains(event.target as Node)) {
        const isCalendarIconClick = (event.target as HTMLElement).closest('.calendar-icon-container') !== null;

        if (!isCalendarIconClick) {
          this.$nextTick(() => {
            this.showCalendar = false;
          });
        }
      }
    },
    deleteSancion(sancion: Sancion) {
      this.sancionToDelete = sancion;
      this.showDeleteDialog = true;
      console.log(`Preparando para eliminar sancion con GUID: ${sancion.guid}. Mostrando diálogo.`);
    },
    async confirmDelete() {
      if (!this.sancionToDelete) {
        console.error("No sancion selected for deletion.");
        this.$toast.add({ severity: 'error', summary: 'Error', detail: 'No sancion selected for deletion.', life: 3000 });
        this.showDeleteDialog = false;
        return;
      }

      console.log(`Confirmado eliminar sancion con GUID: ${this.sancionToDelete.guid}`);
      try {
        await deleteSancion(this.sancionToDelete.guid)

        this.filtrarYPaginar();

      } catch (error: any) {
        console.error("Error eliminando sancion:", error);
        let errorMessage = 'Error al eliminar el sancion.';
        if (axios.isAxiosError(error) && error.response && error.response.data) {
          if (typeof error.response.data === 'string') {
            errorMessage = error.response.data;
          } else if (error.response.data && typeof error.response.data.message === 'string') {
            errorMessage = error.response.data.message;
          } else {
            errorMessage = 'Error desconocido al eliminar sancion.';
          }
        } else if (error instanceof Error) {
          errorMessage = error.message;
        }
        this.$toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      } finally {
        this.showDeleteDialog = false;
        this.sancionToDelete = null;
      }
    }
  },
};
</script>

<style scoped>
.sanciones-container {
  padding: 80px 30px 40px 30px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
}

.filters-container {
  display: flex;
  gap: 10px;
  max-width: 300px;
  justify-content: center;
  align-items: center;
  margin: -25px auto 20px 250px;
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
  width: 100%;
  overflow-x: auto;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  background-color: white;
  margin-left: -15px;
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

.status-advertencia {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-bloqueo {
  background-color: rgba(var(--color-error-rgb), 0.1);
  color: var(--color-error);
}

.status-indefinido {
  background-color: var(--color-error);
  color: white;
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

.action-button-dialog {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.action-button-dialog:active {
  transform: scale(0.98);
}

.action-button-dialog.secondary-button {
  background-color: transparent;
  color: var(--color-interactive);
  border: 1px solid var(--color-interactive);
}

.action-button-dialog.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05);
}

.action-button-dialog.primary-button {
  background-color: var(--color-error);
  color: var(--color-text-on-dark-hover);
}

.action-button-dialog.primary-button:hover {
  background-color: #B91C1C;
  box-shadow: 0 2px 8px rgba(var(--color-interactive-rgb), 0.3);
}

.action-button-dialog.primary-button i {
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .sanciones-container {
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