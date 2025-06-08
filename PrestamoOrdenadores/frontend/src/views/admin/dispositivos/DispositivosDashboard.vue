<template>
  <div class="dispositivos-container">

    <div class="filters">
      <label for="search-input">Buscar:</label>
      <input id="search-input" type="text" v-model="search" placeholder="Buscar por Nº Serie, Componentes, Estado..." @input="handleSearchInput" />
      <button class="action-button primary-button" @click="openAddStockDialog">
        <i class="pi pi-plus"></i> Añadir Stock
      </button>
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
          currentPageReportTemplate="Mostrando {first} a {last} de {totalRecords} dispositivos"
          responsiveLayout="scroll"
          class="p-datatable-custom"
      >
        <Column field="numeroSerie" header="Número de Serie" style="min-width: 150px;"></Column>
        <Column field="componentes" header="Componentes" style="min-width: 200px;"></Column>
        <Column field="estado" header="Estado" style="min-width: 120px;">
          <template #body="slotProps">
                 <span :class="['status-badge', getStatusClass(slotProps.data.estado)]">
                     {{ formatEstado(slotProps.data.estado) }}
                 </span>
          </template>
        </Column>
        <Column field="incidencia.guid" header="Incidencia GUID" style="min-width: 150px;">
          <template #body="slotProps">
            {{ slotProps.data.incidencia?.guid || '-' }}
          </template>
        </Column>

        <Column header="Acciones" style="min-width:100px; width: 100px; text-align: center;">
          <template #body="slotProps">
            <div class="action-buttons">
              <button @click="editDispositivo(slotProps.data)" class="action-button edit-button" title="Editar/Ver Detalles">
                <i class="pi pi-info-circle"></i>
              </button>
              <button
                  @click="deleteDispositivo(slotProps.data)"
                  :class="['action-button', 'delete-button', { 'disabled-button': slotProps.data.estado === 'PRESTADO' }]"
                  :title="slotProps.data.estado === 'PRESTADO' ? 'No se puede eliminar un dispositivo PRESTADO' : 'Eliminar Dispositivo'"
                  :disabled="slotProps.data.estado === 'PRESTADO'"
              >
                <i class="pi pi-trash"></i>
              </button>
            </div>
          </template>
        </Column>

        <template #empty>
          No se encontraron dispositivos.
        </template>
        <template #loading>
          Cargando datos de dispositivos...
        </template>
      </DataTable>
    </div>
  </div>

  <Dialog header="Añadir Nuevo Dispositivo al Stock" v-model:visible="showAddStockDialog" :modal="true" :style="{ width: 'clamp(300px, 50vw, 500px)', fontFamily: 'Montserrat, sans-serif' }" :draggable="false" class="add-stock-dialog" @hide="resetNewDispositivoForm" >
    <form @submit.prevent="handleAddDispositivo" class="modal-form">
      <div class="form-group">
        <label for="dispositivoNumSerie" class="input-label">Número de Serie</label>
        <InputText id="dispositivoNumSerie" v-model="newDispositivoData.numeroSerie" class="input-field full-width" placeholder="Ej: 1AB123WXYZ" />
        <small v-if="!newDispositivoData && submittedAddStock" class="error-message p-error">
          El número de serie es requerido.
        </small>
      </div>
      <div class="form-group">
        <label for="dispositivoComponentes" class="input-label">Componentes del Dispositivo</label>
        <InputText id="dispositivoComponentes" v-model="newDispositivoData.componentes" class="input-field full-width" placeholder="Ej: Ratón, cargador..." />
        <small v-if="!newDispositivoData && submittedAddStock" class="error-message p-error">
          Los componentes son requeridos.
        </small>
      </div>
      <div class="dialog-footer-buttons">
        <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button secondary-button" @click="closeAddStockDialog"/>
        <Button type="submit" label="Añadir Dispositivo" icon="pi pi-send" class="action-button primary-button" :loading="isAddingStock" />
      </div>
    </form>
  </Dialog>

  <Dialog v-model:visible="showDeleteDialog" header="Confirmar Eliminación" modal :draggable="false" :style="{ width: '50vw', fontFamily: 'Montserrat, sans-serif' }">
    <div v-if="dispositivoToDelete">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem;"/><br>
      <span>¿Está seguro de que desea eliminar el dispositivo con numero de serie <strong>{{ dispositivoToDelete.numeroSerie }}</strong>? Esta acción no se puede deshacer.</span>
    </div>
    <template #footer>
      <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button-dialog secondary-button" @click="showDeleteDialog = false" />
      <Button label="Eliminar" class="action-button-dialog primary-button" severity="danger" @click="confirmDelete" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import axios from 'axios';
import {addDispositivoStock, deleteDispositivo} from "@/services/DispositivoService.ts";
import {useToast} from "primevue/usetoast";
import Dialog from "primevue/dialog";
import Toast from "primevue/toast";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import {deleteUser} from "@/services/UsuarioService.ts";

type DeviceState = 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO';

interface Dispositivo {
  guid: string;
  numeroSerie: string;
  componentes: string;
  estadoDispositivo: string;
  estado: DeviceState;
  incidencia: { guid: string } | null;
  incidenciaGuid: string | null;
  isActivo: boolean;
  createdDate: string;
  updatedDate: string;
  isDeleted: boolean;
}

interface PagedResponse {
  content: Dispositivo[];
  totalElements: number;
}

export default {
  name: 'DispositivosDashboard',
  emits: ['input-change'],
  components: { Dialog, Toast, Button, InputText },
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      search: '',
      datos: [] as Dispositivo[],
      todosLosDatos: [] as Dispositivo[],
      totalRecords: 0,
      loading: false,
      currentPage: 0,
      pageSize: 5,
      showAddStockDialog: false,
      newDispositivoData: {numeroSerie: '', componentes: ''},
      isAddingStock: false,
      submittedAddStock: false,
      showDeleteDialog: false,
      dispositivoToDelete: null as Dispositivo | null,
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
    getStatusClass(estado: DeviceState | undefined): string {
      if (!estado) return 'status-unknown';
      switch (estado) {
        case 'DISPONIBLE': return 'status-disponible';
        case 'PRESTADO': return 'status-prestado';
        case 'NO_DISPONIBLE': return 'status-no-disponible';
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
      const resultadosFiltrados = this.filtrarPorTexto(this.search);
      this.totalRecords = resultadosFiltrados.length;
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = resultadosFiltrados.slice(inicio, fin);
    },
    onPage(event: any) {
      this.loading = true;
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.paginar();
      setTimeout(() => { this.loading = false; }, 100);
    },
    handleSearchInput(event: Event) {
      this.currentPage = 0;
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
        params: { guid: dispositivo.guid }
      });
    },
    openAddStockDialog() {
      this.resetNewDispositivoForm();
      this.showAddStockDialog = true;
    },
    closeAddStockDialog() {
      this.showAddStockDialog = false;
    },
    resetNewDispositivoForm() {
      this.newDispositivoData =  { numeroSerie: '', componentes: '' };
      this.submittedAddStock = false;
      this.isAddingStock = false;
    },
    async handleAddDispositivo() {
      this.submittedAddStock = true;

      let isValid = true;
      if (!this.newDispositivoData.numeroSerie){
        isValid = false;
      }

      if (!this.newDispositivoData.componentes) {
        isValid = false;
      }

      const numeroSerieRegex = /^\d[A-Z]{2}\d{3}[A-Z]{4}$/;
      if (this.newDispositivoData.numeroSerie && !numeroSerieRegex.test(this.newDispositivoData.numeroSerie)) {
        this.toast.add({ severity: 'error', summary: 'Error de Validación', detail: 'El formato del Número de Serie es incorrecto (Ej: 1AB123CDEF).', life: 4000 });
        isValid = false;
      }

      if (!isValid) {
        this.toast.add({ severity: 'warn', summary: 'Error de Validación', detail: 'Por favor, complete todos los campos requeridos correctamente.', life: 3000 });
        return;
      }

      if (this.newDispositivoData.componentes.length > 1000) {
        this.toast.add({ severity: 'error', summary: 'Error de Validación', detail: 'Los componentes no pueden exceder los 1000 caracteres.', life: 3000 });
        return;
      }
      if (this.newDispositivoData.numeroSerie.length > 255) {
        this.toast.add({ severity: 'error', summary: 'Error de Validación', detail: 'El Número de Serie no puede exceder los 255 caracteres.', life: 3000 });
        return;
      }

      this.isAddingStock = true;
      try {
        const payload = {
          numeroSerie: this.newDispositivoData.numeroSerie,
          componentes: this.newDispositivoData.componentes
        };

        await addDispositivoStock(payload);

        await this.loadData();

        this.closeAddStockDialog();
      } catch (error: any) {
        console.error("Error al añadir el nuevo dispositivo:", error);
        const errorMessage = error.response?.data?.message || error.response?.data?.error || (typeof error.response?.data === 'string' ? error.response.data : null) || 'No se pudo añadir el dispositivo.';
        this.toast.add({ severity: 'error', summary: 'Error al Añadir', detail: String(errorMessage), life: 5000 });
      } finally {
        this.isAddingStock = false;
      }
    },
    deleteDispositivo(dispositivo: Dispositivo) {
      this.dispositivoToDelete = dispositivo;
      this.showDeleteDialog = true;
      console.log(`Preparando para eliminar dispositivo con numero de serie: ${dispositivo.numeroSerie}. Mostrando diálogo.`);
    },
    async confirmDelete() {
      if (!this.dispositivoToDelete) {
        console.error("No dispositivo selected for deletion.");
        this.$toast.add({ severity: 'error', summary: 'Error', detail: 'No user selected for deletion.', life: 3000 });
        this.showDeleteDialog = false;
        return;
      }

      console.log(`Confirmado eliminar dispositivo con numero de serie: ${this.dispositivoToDelete.numeroSerie}`);
      try {
        await deleteDispositivo(this.dispositivoToDelete.guid)

        this.paginar();

      } catch (error: any) {
        console.error("Error eliminando dispositivo:", error);
        let errorMessage = 'Error al eliminar el dispositivo.';
        if (axios.isAxiosError(error) && error.response && error.response.data) {
          if (typeof error.response.data === 'string') {
            errorMessage = error.response.data;
          } else if (error.response.data && typeof error.response.data.message === 'string') {
            errorMessage = error.response.data.message;
          } else {
            errorMessage = 'Error desconocido al eliminar dispositivo.';
          }
        } else if (error instanceof Error) {
          errorMessage = error.message;
        }
        this.$toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      } finally {
        this.showDeleteDialog = false;
        this.dispositivoToDelete = null;
      }
    }
  },
};
</script>

<style scoped>
.dispositivos-container {
  padding: 80px 30px 40px 30px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
}

.filters {
  display: flex;
  align-items: center;
  gap: 15px;
  margin: -25px auto 40px 150px;
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
  max-width: 675px;
}

.filters input:focus {
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
}

.status-disponible {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-prestado {
  background-color: rgba(var(--color-interactive-rgb), 0.15);
  color: var(--color-interactive-darker);
}

.status-no-disponible{
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
}

.action-button.edit-button, .action-button.delete-button {
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

.edit-button {
  background-color: var(--color-interactive);
  color: white;
}

.edit-button:hover {
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

.action-button.primary-button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  text-decoration: none;
  line-height: 1.2;
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
  white-space: nowrap;
}
.action-button.primary-button:active { transform: scale(0.98); }

.action-button.primary-button:hover {
  background-color: var(--color-interactive-darker);
  box-shadow: 0 2px 8px rgba(var(--color-interactive-rgb), 0.3);
}
.action-button.primary-button i {
  font-size: 1.1rem;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  font-size: 0.9rem;
  color: var(--color-text-dark);
  font-weight: 500;
}

.modal-form :deep(.p-inputtext.input-field),
.modal-form :deep(.p-textarea.input-field) {
  border-radius: 8px;
  border: 1px solid var(--color-neutral-medium);
  padding: 10px 12px;
  font-family: 'Montserrat', sans-serif;
  font-size: 1rem;
  width: 100% !important;
  box-sizing: border-box;
}
.modal-form :deep(.p-inputtext.input-field:focus),
.modal-form :deep(.p-textarea.input-field:focus) {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 2px rgba(var(--color-interactive-rgb), 0.2);
}

.error-message.p-error {
  color: var(--color-error);
  font-size: 0.85rem;
}

.dialog-footer-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
}

.action-button.secondary-button {
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  text-decoration: none;
  line-height: 1.2;
  background-color: transparent;
  color: var(--color-interactive);
  border: 1px solid var(--color-interactive);
}
.action-button.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05);
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

.disabled-button {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    margin: 0 auto 30px auto;
    align-items: stretch;
  }

  .filters label {
    margin-bottom: -10px;
  }

  .filters input {
    width: 100%;
    max-width: 100%;
  }

  .filters .action-button.primary-button {
    width: 100%;
    justify-content: center;
  }

  .table-wrapper {
    margin-left: 0;
    border: 1px solid var(--color-neutral-medium);
    border-radius: 8px;
    box-shadow: none;
  }

  :deep(.p-datatable-custom) {
    font-size: 0.9rem;
  }

  .action-buttons {
    flex-direction: column;
    gap: 8px;
  }

  :deep(.add-stock-dialog),
  :deep(.p-dialog) {
    width: 95vw !important;
    max-width: 400px;
    font-size: 0.9rem;
  }

  .dialog-footer-buttons {
    flex-direction: column;
    gap: 8px;
    padding-left: 10px;
    padding-right: 10px;
  }

  .dialog-footer-buttons .action-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  :deep(.add-stock-dialog),
  :deep(.p-dialog) {
    width: 95vw !important;
    max-width: 400px;
    font-size: 0.9rem;
  }

  .dialog-footer-buttons {
    flex-direction: column;
    gap: 8px;
    padding-left: 10px;
    padding-right: 10px;
  }

  .dialog-footer-buttons .action-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .action-buttons {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
  }

  .action-button.edit-button,
  .action-button.delete-button {
    width: 28px;
    height: 28px;
  }

  .action-button.primary-button,
  .action-button.secondary-button {
    width: 100%;
    justify-content: center;
  }
}
</style>