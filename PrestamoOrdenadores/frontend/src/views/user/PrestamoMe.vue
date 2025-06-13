<template>
  <div class="page-container prestamos-me-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Mis Préstamos</h2>
      </div>
      <div class="header-right">
        <button class="action-button primary-button" @click="openRealizarPrestamoDialog">
          <i class="pi pi-plus"></i> Realizar Préstamo
        </button>
      </div>
    </div>

    <div class="table-card">
      <DataTable
          v-if="prestamos.length > 0"
          :value="prestamos"
          paginator
         :rows="5"
         :rowsPerPageOptions="[5, 10, 20, 50]"
          sortMode="multiple"
          tableStyle="min-width: 50rem; font-family: 'Montserrat', sans-serif"
          :loading="loading"
      >
        <Column field="dispositivo.numeroSerie" header="Número de Serie">
          <template #body="slotProps">
            {{ slotProps.data.dispositivo?.numeroSerie ?? 'N/A' }}
          </template>
        </Column>
        <Column field="fechaPrestamo" header="Fecha Préstamo">
          <template #body="slotProps">
            {{ slotProps.data.fechaPrestamo }}
          </template>
        </Column>
        <Column field="fechaDevolucion" header="Fecha Devolución">
          <template #body="slotProps">
            {{ slotProps.data.fechaDevolucion ? slotProps.data.fechaDevolucion : 'Pendiente' }}
          </template>
        </Column>
        <Column field="estadoPrestamo" header="Estado">
          <template #body="slotProps">
            <span :class="['status-badge', getEstadoPrestamoClass(slotProps.data.estadoPrestamo)]">
              {{ slotProps.data.estadoPrestamo }}
            </span>
          </template>
        </Column>
        <Column header="" :exportable="false" style="min-width:10rem">
          <template #body="slotProps">
            <Button
                icon="pi pi-file-pdf"
                label="Ver PDF"
                class="action-button-table pdf-button"
                @click="descargarPdf(slotProps.data.guid)"
            />
          </template>
        </Column>
      </DataTable>
      <p v-if="!loading && prestamos.length === 0 " class="no-data-message">
        Aún no tienes préstamos registrados.
      </p>
    </div>
  </div>

  <Dialog v-model:visible="displayConfirmDialog" modal :header="'Confirmar Préstamo'" :style="{ width: '500px', fontFamily: 'Montserrat, sans-serif' }" :breakpoints="{ '960px': '75vw', '641px': '100vw' }" class="custom-dialog">
    <div class="dialog-content">
      <p class="dialog-message">Estás a punto de realizar un préstamo. ¿Deseas continuar?</p>
    </div>
    <template #footer>
      <Button label="Cancelar" @click="cancelarRealizarPrestamo" class="p-button action-button secondary-button" />
      <Button label="Realizar préstamo" @click="confirmRealizarPrestamo" class="p-button action-button primary-button" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Toast from 'primevue/toast';
import Tooltip from 'primevue/tooltip';
import { getPrestamosByUserGuid, createPrestamo, descargarPdfPrestamo, type Prestamo } from '@/services/PrestamoService.ts';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: "PrestamoMe",
  components: { DataTable, Column, Button, Dialog, Toast },
  directives: { Tooltip },
  data() {
    return {
      prestamos: [] as Prestamo[],
      loading: true,
      displayConfirmDialog: false,
    };
  },
  setup() {
    const toast = useToast();
    const router = useRouter();
    return { toast, router };
  },
  methods: {
    openRealizarPrestamoDialog(): void {
      this.displayConfirmDialog = true;
    },
    async fetchPrestamos(): Promise<void> {
      this.loading = true;
      try {
        const data = await getPrestamosByUserGuid();
        this.prestamos = data.map(p => ({ ...p }));
        console.log("Datos de préstamos:", this.prestamos);
      } catch (error) {
        console.error("Error al obtener los préstamos:", error);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los préstamos.', life: 3000 });
      } finally {
        this.loading = false;
      }
    },
    async confirmRealizarPrestamo(): Promise<void> {
      this.displayConfirmDialog = false;
      console.log('Confirmación aceptada, llamando a createPrestamo');
      try {
        const prestamoResponse = await createPrestamo();
        if (prestamoResponse) {
          await this.fetchPrestamos();
          console.log('Llamando a descargarPdfPrestamo con GUID:', prestamoResponse);
          await descargarPdfPrestamo(prestamoResponse);
          console.log('Descarga de PDF iniciada');
        } else {
          throw new Error('La creación del préstamo no devolvió un GUID válido.');
        }
      } catch (error: any) {
        console.error('Error al realizar el préstamo:', error.response?.data || error.message);
        const errorMessage = error.response?.data?.message || error.response?.data || error.message || 'Error desconocido al realizar el préstamo.';

        if (error.response?.status === 404 && (errorMessage.includes('No hay dispositivos disponibles') || errorMessage.includes('dispositivos disponibles'))) {
          this.toast.add({
            severity: 'warn',
            summary: 'Sin Disponibilidad',
            detail: 'No hay dispositivos disponibles para realizar el préstamo en este momento.',
            life: 5000,
          });
        } else if (error.response?.status === 409 && (errorMessage.includes('límite de préstamos') || errorMessage.includes('préstamo activo'))) {
          this.toast.add({
            severity: 'warn',
            summary: 'Límite Alcanzado',
            detail: 'Ya tienes un préstamo activo o has alcanzado el límite. No puedes realizar otro hasta devolver el actual.',
            life: 6000,
          });
        } else {
          this.toast.add({ severity: 'error', summary: 'Error de Préstamo', detail: errorMessage, life: 5000 });
        }
      }
    },
    cancelarRealizarPrestamo(): void {
      this.displayConfirmDialog = false;
      console.log('Confirmación rechazada');
    },
    async descargarPdf(prestamoGuid: string): Promise<void> {
      if (!prestamoGuid) {
        this.toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'GUID de préstamo no disponible.', life: 3000 });
        return;
      }
      try {
        await descargarPdfPrestamo(prestamoGuid);
        this.toast.add({ severity: 'info', summary: 'PDF', detail: 'La descarga del PDF ha comenzado.', life: 3000 });
      } catch (error: any) {
        console.error("Error al descargar el PDF:", error);
        this.toast.add({ severity: 'error', summary: 'Error de PDF', detail: 'No se pudo descargar el PDF.', life: 3000 });
      }
    },
    goBack(): void {
      this.router.back();
    },
    getEstadoPrestamoClass(estado: string | undefined): string {
      if (!estado) return 'status-unknown';
      switch (estado.toUpperCase()) {
        case 'EN_CURSO': return 'status-en-curso';
        case 'VENCIDO': return 'status-vencido';
        case 'CANCELADO': return 'status-cancelado';
        default: return 'status-unknown';
      }
    },
  },
  mounted() {
    this.fetchPrestamos();
  },
});
</script>

<style scoped>
.page-container.prestamos-me-page {
  padding: 30px 30px 50px;
  max-width: 1000px;
  box-sizing: border-box;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  position: sticky;
  top: 0;
  padding-top: 10px;
  padding-bottom: 10px;
  z-index: 5;
}

.page-header .header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-header h2 {
  color: var(--color-primary);
  font-size: 1.8rem;
  font-weight: 600;
  margin: 0;
}

.action-button {
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
}

.action-button:active { transform: scale(0.98); }

.action-button.primary-button {
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
}

.action-button.primary-button:hover {
  background-color: var(--color-interactive-darker);
  box-shadow: 0 2px 8px rgba(var(--color-interactive-rgb), 0.3);
}

.action-button.primary-button i {
  font-size: 1.1rem;
}

.table-card {
  font-family: 'Montserrat', sans-serif !important;
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.1);
  overflow-x: auto;
  margin-bottom: 30px;
  width: 100%;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.status-badge {
  padding: 5px 12px;
  border-radius: 16px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.status-en-curso {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success, #15803d);
}

.status-cancelado {
  background-color: rgba(var(--color-neutral-medium), 0.15);
  color: var(--color-neutral-medium, #575757);
}

.status-vencido {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.no-data-message {
  text-align: center;
  padding: 20px;
  color: var(--color-text-dark);
  font-style: italic;
}

.action-button-table.pdf-button {
  padding: 8px 12px;
  border: 1px solid var(--color-interactive);
  background-color: white;
  color: var(--color-interactive);
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.1s ease;
  line-height: 1.2;
  margin: 0;
  width: auto;
  height: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.action-button-table.pdf-button .p-button-icon {
  margin-right: 6px;
}

.action-button-table.pdf-button:hover {
  background-color: var(--color-interactive);
  color: white;
  border-color: var(--color-interactive);
}

.action-button-table.pdf-button:active {
  transform: scale(0.97);
}

:deep(.p-toast .p-toast-message.custom-toast-warning) {
  background-color: var(--color-warning, #fffbeb) !important;
  border: 1px solid var(--color-warning, #f59e0b) !important;
  color: var(--color-text-dark, #78350f) !important;
}

:deep(.p-toast .p-toast-message.custom-toast-warning .p-toast-message-icon),
:deep(.p-toast .p-toast-message.custom-toast-warning .p-toast-summary) {
  color: var(--color-text-dark, #78350f) !important;
}

:deep(.p-toast .p-toast-message.custom-toast-info) {
  background-color: var(--color-interactive, #eff6ff) !important;
  border: 1px solid var(--color-interactive, #3b82f6) !important;
  color: var(--color-interactive-darker, #1e3a8a) !important;
}

:deep(.p-toast .p-toast-message.custom-toast-info .p-toast-message-icon),
:deep(.p-toast .p-toast-message.custom-toast-info .p-toast-summary) {
  color: var(--color-interactive-darker, #1e3a8a) !important;
}

:deep(.custom-dialog .p-dialog-content) {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

:deep(.custom-dialog .dialog-message) {
  font-size: 1.1rem;
  color: var(--color-text-dark);
  margin-bottom: 1.5rem;
}

:deep(.custom-dialog .p-dialog-header .p-dialog-title) {
  font-family: 'Montserrat', sans-serif;
  font-weight: 600;
  color: var(--color-primary);
}

:deep(.custom-dialog .p-dialog-header .p-dialog-header-icon) {
  width: auto !important;
  margin-top: 0 !important;
  margin-right: 0 !important;
  padding: 0.5rem !important;
  color: var(--color-text-dark) !important;
  background-color: transparent !important;
}

:deep(.custom-dialog .action-button.primary-button) {
  background-color: var(--color-interactive);
  color: var(--color-text-on-dark-hover);
}

:deep(.custom-dialog .action-button.primary-button:hover) {
  background-color: var(--color-interactive-darker) !important;
  box-shadow: 0 2px 8px rgba(var(--color-interactive-rgb), 0.3) !important;
}

:deep(.custom-dialog .action-button.primary-button:active) {
  transform: scale(0.98) !important;
}

.action-button.secondary-button {
  background-color: transparent !important;
  color: var(--color-interactive) !important;
  border: 1px solid var(--color-interactive) !important;
  padding: 0.75rem 1.5rem !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease, transform 0.1s ease !important;
}

.action-button.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05) !important;
  color: var(--color-interactive-darker) !important;
  border-color: var(--color-interactive-darker) !important;
}

.action-button.secondary-button:active {
  transform: scale(0.98) !important;
}

:deep(.custom-dialog .p-dialog-footer .p-button) {
  width: auto;
  min-width: 120px;
  margin: 0 5px;
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

@media screen and (max-width: 768px) {
  .page-container.prestamos-me-page {
    padding: 20px 15px;
    top: 60px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .page-header h2 {
    font-size: 1.5rem;
  }

  .action-button.primary-button {
    width: 100%;
    justify-content: center;
    font-size: 0.9rem;
  }

  .table-card {
    padding: 15px;
    box-shadow: none;
    border-radius: 0;
  }

  .status-badge {
    font-size: 0.75rem;
    padding: 4px 10px;
  }

  .action-button-table.pdf-button {
    font-size: 0.75rem;
    padding: 6px 10px;
    width: 100%;
    justify-content: center;
  }
}

@media screen and (max-width: 480px) {
  .page-header h2 {
    font-size: 1.3rem;
  }

  .action-button.primary-button,
  .action-button-table.pdf-button {
    font-size: 0.8rem;
    padding: 8px;
  }

  .table-card {
    padding: 10px;
  }
}
</style>