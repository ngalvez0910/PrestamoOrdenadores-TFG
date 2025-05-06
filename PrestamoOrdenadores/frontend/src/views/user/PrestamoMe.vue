<template>
  <AdminMenuBar/>

  <div class="page-container prestamos-me-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Mis Préstamos</h2>
      </div>
      <div class="header-right">
        <button class="action-button primary-button" @click="realizarPrestamo">
          <i class="pi pi-plus"></i> Realizar Préstamo
        </button>
      </div>
    </div>

    <div class="table-card">
      <DataTable :value="prestamos" stripedRows responsiveLayout="scroll" tableStyle="min-width: 50rem; font-family: 'Montserrat', sans-serif" :loading="loading">
        <Column field="dispositivo.numeroSerie" header="Número de Serie" sortable>
          <template #body="slotProps">
            {{ slotProps.data.dispositivo?.numeroSerie ?? 'N/A' }}
          </template>
        </Column>
        <Column field="fechaPrestamo" header="Fecha Préstamo" sortable>
          <template #body="slotProps">
            {{ slotProps.data.fechaPrestamo }}
          </template>
        </Column>
        <Column field="fechaDevolucion" header="Fecha Devolución" sortable>
          <template #body="slotProps">
            {{ slotProps.data.fechaDevolucion ? slotProps.data.fechaDevolucion : 'Pendiente' }}
          </template>
        </Column>
        <Column field="estadoPrestamo" header="Estado" sortable>
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
      <p v-if="!loading && prestamos.length === 0" class="no-data-message">
        Aún no tienes préstamos registrados.
      </p>
    </div>
  </div>

  <ConfirmDialog />
  <Toast />
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import ConfirmDialog from 'primevue/confirmdialog';
import Toast from 'primevue/toast';
import Tooltip from 'primevue/tooltip';

import { getPrestamosByUserGuid, createPrestamo, descargarPdfPrestamo, type Prestamo } from '@/services/PrestamoService.ts';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import { useConfirm } from 'primevue/useconfirm';

export default defineComponent({
  name: "PrestamoMe",
  components: { AdminMenuBar, DataTable, Column, Button, ConfirmDialog, Toast },
  directives: { Tooltip },
  setup() {
    const prestamos = ref<Prestamo[]>([]);
    const toast = useToast();
    const router = useRouter();
    const confirm = useConfirm();
    const loading = ref(true);

    onMounted(async () => {
      await fetchPrestamos();
    });

    const fetchPrestamos = async () => {
      loading.value = true;
      try {
        const data = await getPrestamosByUserGuid();
        prestamos.value = data.map(p => ({ ...p }));
        console.log("Datos de préstamos:", prestamos.value);
      } catch (error) {
        console.error("Error al obtener los préstamos:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los préstamos.', life: 3000 });
      } finally {
        loading.value = false;
      }
    };

    const realizarPrestamo = async () => {
      console.log('Iniciando proceso de préstamo');
      confirm.require({
        message: 'Estás a punto de realizar un préstamo. ¿Deseas continuar?',
        header: 'Confirmar Préstamo',
        icon: 'pi pi-question-circle',
        acceptLabel: 'Sí, realizar préstamo',
        rejectLabel: 'Cancelar',
        acceptClass: 'p-button-success',
        rejectClass: 'p-button-danger',
        accept: async () => {
          console.log('Confirmación aceptada, llamando a createPrestamo');
          try {
            const prestamoResponse = await createPrestamo();
            if (prestamoResponse) {
              toast.add({ severity: 'success', summary: 'Éxito', detail: `Préstamo realizado.`, life: 3000 });
              await fetchPrestamos();
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
              toast.add({
                severity: 'warn',
                summary: 'Sin Disponibilidad',
                detail: 'No hay dispositivos disponibles para realizar el préstamo en este momento.',
                life: 5000,
              });
            } else if (error.response?.status === 409 && (errorMessage.includes('límite de préstamos') || errorMessage.includes('préstamo activo'))) {
              toast.add({
                severity: 'warn',
                summary: 'Límite Alcanzado',
                detail: 'Ya tienes un préstamo activo o has alcanzado el límite. No puedes realizar otro hasta devolver el actual.',
                life: 6000,
              });
            } else {
              toast.add({ severity: 'error', summary: 'Error de Préstamo', detail: errorMessage, life: 5000 });
            }
          }
        },
        reject: () => {
          console.log('Confirmación rechazada');
          toast.add({
            severity: 'info',
            summary: 'Operación Cancelada',
            detail: 'El préstamo no se ha realizado.',
            life: 3000,
          });
        },
      });
    };

    const descargarPdf = async (prestamoGuid: string) => {
      if (!prestamoGuid) {
        toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'GUID de préstamo no disponible.', life: 3000 });
        return;
      }
      try {
        await descargarPdfPrestamo(prestamoGuid);
        toast.add({ severity: 'info', summary: 'PDF', detail: 'La descarga del PDF ha comenzado.', life: 3000 });
      } catch (error: any) {
        console.error("Error al descargar el PDF:", error);
        toast.add({ severity: 'error', summary: 'Error de PDF', detail: 'No se pudo descargar el PDF.', life: 3000 });
      }
    };

    const goBack = () => {
      router.back();
    };

    const getEstadoPrestamoClass = (estado: string | undefined): string => {
      if (!estado) return 'status-unknown';
      switch (estado.toUpperCase()) {
        case 'EN_CURSO': return 'status-en-curso';
        case 'VENCIDO': return 'status-vencido';
        case 'CANCELADO': return 'status-cancelado';
        default: return 'status-unknown';
      }
    };

    return {
      prestamos,
      loading,
      realizarPrestamo,
      descargarPdf,
      goBack,
      getEstadoPrestamoClass,
    };
  },
});
</script>

<style scoped>
.page-container.prestamos-me-page {
  padding: 30px;
  max-width: 1000px;
  box-sizing: border-box;
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  margin-left: auto;
  margin-right: auto;
  z-index: 0;
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
  color: var(--color-warning, #B45309);
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
  border-width: 0 0 0 6px !important;
  color: var(--color-text-dark, #78350f) !important;
}
:deep(.p-toast .p-toast-message.custom-toast-warning .p-toast-message-icon),
:deep(.p-toast .p-toast-message.custom-toast-warning .p-toast-summary) {
  color: var(--color-text-dark, #78350f) !important;
}

:deep(.p-toast .p-toast-message.custom-toast-info) {
  background-color: var(--color-interactive, #eff6ff) !important;
  border: 1px solid var(--color-interactive, #3b82f6) !important;
  border-width: 0 0 0 6px !important;
  color: var(--color-interactive-darker, #1e3a8a) !important;
}
:deep(.p-toast .p-toast-message.custom-toast-info .p-toast-message-icon),
:deep(.p-toast .p-toast-message.custom-toast-info .p-toast-summary) {
  color: var(--color-interactive-darker, #1e3a8a) !important;
}

:deep(.p-confirm-dialog .p-confirm-dialog-accept.p-button-success) {
  background-color: var(--color-success) !important;
  border-color: var(--color-success) !important;
  color: white !important;
}
:deep(.p-confirm-dialog .p-confirm-dialog-accept.p-button-success:hover) {
  background-color: var(--color-success) !important;
  border-color: var(--color-success) !important;
}

:deep(.p-confirm-dialog .p-confirm-dialog-reject.p-button-danger) {
  background-color: var(--color-error) !important;
  border-color: var(--color-error) !important;
  color: white !important;
}
:deep(.p-confirm-dialog .p-confirm-dialog-reject.p-button-danger:hover) {
  background-color: var(--color-error) !important;
  border-color: var(--color-error) !important;
}

:deep(.p-confirm-dialog .p-dialog-footer button) {
  width: auto;
  min-width: 120px;
  padding: 0.75rem 1.5rem;
  border-radius: 8px !important;
  font-weight: 500;
  margin: 0 5px;
}
:deep(.p-confirm-dialog .p-dialog-header .p-dialog-header-icon) {
  width: auto !important;
  margin-top: 0 !important;
  margin-right: 0 !important;
  padding: 0.5rem !important;
  color: var(--color-text-dark) !important;
  background-color: transparent !important;
}
:deep(.p-confirm-dialog .p-dialog-header .p-dialog-title) {
  font-family: 'Montserrat', sans-serif;
  font-weight: 600;
  color: var(--color-primary);
}
:deep(.p-confirm-dialog .p-confirm-dialog-message) {
  font-family: 'Montserrat', sans-serif;
  margin-left: 0;
}
:deep(.p-confirm-dialog .p-confirm-dialog-icon) {
  font-size: 1.5rem;
  color: var(--color-interactive);
}

@media (max-width: 768px) {
  .page-container.prestamos-me-page {
    padding: 20px 15px;
    max-width: 100%;
    position: relative;
    top: 60px;
    margin-left: auto;
    margin-right: auto;
    overflow-y: visible;
  }
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    position: relative;
  }
  .page-header .header-left {
    justify-content: space-between;
  }
  .page-header .header-left h2 {
    font-size: 1.5rem;
  }
  .page-header .header-right {
    display: flex;
  }
  .action-button.primary-button {
    width: 100%;
    justify-content: center;
  }
  .table-card {
    padding: 10px;
  }
}
</style>