<template>
  <div class="page-container incidencias-me-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Mis Incidencias</h2>
      </div>
      <div class="header-right">
        <button class="action-button primary-button" @click="openReportarModal">
          <i class="pi pi-plus"></i> Reportar Incidencia
        </button>
      </div>
    </div>

    <div class="table-card">
      <DataTable :value="incidencias" stripedRows responsiveLayout="scroll" tableStyle="min-width: 50rem" :loading="loading">
        <Column field="asunto" header="Asunto" sortable>
          <template #body="slotProps">
            {{ slotProps.data.asunto }}
          </template>
        </Column>
        <Column field="createdDate" header="Fecha Reporte" sortable>
          <template #body="slotProps">
            {{ slotProps.data.createdDate }}
          </template>
        </Column>
        <Column field="estadoIncidencia" header="Estado" sortable>
          <template #body="slotProps">
            <span :class="['status-badge', getEstadoIncidenciaClass(slotProps.data.estadoIncidencia)]">
              {{ slotProps.data.estadoIncidencia }}
            </span>
          </template>
        </Column>
      </DataTable>
      <p v-if="!loading && incidencias.length === 0" class="no-data-message">
        No has reportado ninguna incidencia.
      </p>
    </div>
  </div>

  <Dialog header="Reportar Nueva Incidencia" v-model:visible="isReportarModalVisible" modal
          :style="{ width: 'clamp(300px, 50vw, 600px)', fontFamily: 'Montserrat, sans-serif' }" :draggable="false" @hide="resetNuevaIncidenciaForm">
    <form @submit.prevent="handleReportarIncidencia" class="modal-form">
      <div class="form-group">
        <label for="incidenciaAsunto" class="input-label">Asunto</label>
        <InputText id="incidenciaAsunto" v-model="nuevaIncidencia.asunto" class="input-field full-width" placeholder="Ej: El proyector no enciende" />
        <small v-if="nuevaIncidenciaErrors.asunto" class="error-message p-error">{{ nuevaIncidenciaErrors.asunto }}</small>
      </div>
      <div class="form-group">
        <label for="incidenciaDescripcion" class="input-label">Descripción</label>
        <Textarea id="incidenciaDescripcion" v-model="nuevaIncidencia.descripcion" rows="5" class="input-field full-width" placeholder="Detalla el problema que has encontrado..." />
        <small v-if="nuevaIncidenciaErrors.descripcion" class="error-message p-error">{{ nuevaIncidenciaErrors.descripcion }}</small>
      </div>
      <div class="dialog-footer-buttons">
        <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button secondary-button" @click="isReportarModalVisible = false" />
        <Button type="submit" label="Reportar Incidencia" icon="pi pi-send" class="action-button primary-button" :loading="isSubmittingIncidencia" />
      </div>
    </form>
  </Dialog>

  <Toast />
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import Textarea from 'primevue/textarea';

import { getIncidenciasByUserGuid, createIncidencia, type Incidencia } from '@/services/IncidenciaService.ts';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: "IncidenciasMe",
  components: { DataTable, Column, Button, Toast, Dialog, InputText, Textarea },
  setup() {
    const incidencias = ref<Incidencia[]>([]);
    const loading = ref(true);
    const toast = useToast();
    const router = useRouter();

    const isReportarModalVisible = ref(false);
    const nuevaIncidencia = ref<CreateIncidenciaDto>({
      asunto: '',
      descripcion: '',
    });
    const nuevaIncidenciaErrors = ref({
      asunto: '',
      descripcion: '',
    });
    const isSubmittingIncidencia = ref(false);

    const resetNuevaIncidenciaForm = () => {
      nuevaIncidencia.value.asunto = '';
      nuevaIncidencia.value.descripcion = '';
      nuevaIncidenciaErrors.value.asunto = '';
      nuevaIncidenciaErrors.value.descripcion = '';
      isSubmittingIncidencia.value = false;
    };

    const openReportarModal = () => {
      resetNuevaIncidenciaForm();
      isReportarModalVisible.value = true;
    };

    const handleReportarIncidencia = async () => {
      let isValid = true;
      nuevaIncidenciaErrors.value.asunto = '';
      nuevaIncidenciaErrors.value.descripcion = '';

      if (!nuevaIncidencia.value.asunto.trim()) {
        nuevaIncidenciaErrors.value.asunto = 'El asunto es obligatorio.';
        isValid = false;
      }
      if (nuevaIncidencia.value.asunto.trim().length > 255) {
        nuevaIncidenciaErrors.value.asunto = 'El asunto no puede exceder los 255 caracteres.';
        isValid = false;
      }
      if (!nuevaIncidencia.value.descripcion.trim()) {
        nuevaIncidenciaErrors.value.descripcion = 'La descripción es obligatoria.';
        isValid = false;
      }
      if (nuevaIncidencia.value.descripcion.trim().length > 1000) {
        nuevaIncidenciaErrors.value.descripcion = 'La descripción no puede exceder los 1000 caracteres.';
        isValid = false;
      }

      if (!isValid) {
        return;
      }

      isSubmittingIncidencia.value = true;
      try {
        const response = await createIncidencia(nuevaIncidencia.value);
        if (response) {
          isReportarModalVisible.value = false;
          await fetchIncidencias();
        } else {
          throw new Error('No se recibió respuesta al crear la incidencia.');
        }
      } catch (error: any) {
        console.error('Error al reportar la incidencia:', error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data?.message || error.message || 'No se pudo reportar la incidencia.', life: 3000 });
      } finally {
        isSubmittingIncidencia.value = false;
      }
    };

    const fetchIncidencias = async () => {
      loading.value = true;
      try {
        incidencias.value = await getIncidenciasByUserGuid();
        console.log("Datos de incidencias:", incidencias.value);
      } catch (error: any) {
        console.error("Error al obtener las incidencias:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.message || 'No se pudieron cargar las incidencias.', life: 3000 });
      } finally {
        loading.value = false;
      }
    };

    onMounted(fetchIncidencias);

    const goToCrearIncidencia = () => {
      console.log("Navegando a crear incidencia");
      router.push({ name: 'ReportarIncidencia' });
    };

    const goBack = () => {
      router.back();
    };

    const getEstadoIncidenciaClass = (estado: string | undefined): string => {
      if (!estado) return 'status-unknown';
      switch (estado) {
        case 'PENDIENTE': return 'status-pendiente';
        case 'RESUELTO': return 'status-resuelto';
        default: return 'status-unknown';
      }
    };

    // const verDetalleIncidencia = (incidenciaId: string) => {
    //   router.push({ name: 'IncidenciaDetalleUsuario', params: { id: incidenciaId } }); // Ejemplo
    // };

    return {
      incidencias,
      loading,
      isReportarModalVisible,
      nuevaIncidencia,
      nuevaIncidenciaErrors,
      isSubmittingIncidencia,
      openReportarModal,
      handleReportarIncidencia,
      resetNuevaIncidenciaForm,
      goBack,
      getEstadoIncidenciaClass,
    };
  },
});
</script>

<style scoped>
.page-container.incidencias-me-page {
  padding: 30px;
  max-width: 900px;
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
  max-width: 840px;
  margin-left: auto;
  margin-right: auto;
}

:deep(.p-datatable) {
  font-family: 'Montserrat', sans-serif !important;
}

:deep(.p-datatable .p-datatable-thead th),
:deep(.p-datatable .p-datatable-tbody > tr > td) {
  font-family: 'Montserrat', sans-serif !important;
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

.status-pendiente {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-resuelto {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-unknown {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
}

.no-data-message {
  text-align: center;
  padding: 20px;
  color: var(--color-text-dark);
  font-style: italic;
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
  background-color: transparent;
  color: var(--color-interactive);
  border: 1px solid var(--color-interactive);
}
.action-button.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05);
}


/* Estilos para botones de acción en tabla (si los añades) */
/*
.action-button-table {
    margin: 0 2px;
    width: 2.5rem;
    height: 2.5rem;
    padding: 0;
    display: inline-flex;
    justify-content: center;
    align-items: center;
}
.action-button-table.p-button-info {
    background-color: var(--color-info) !important;
    border-color: var(--color-info) !important;
}
.action-button-table.p-button-info:hover {
    background-color: var(--color-info-darker) !important;
    border-color: var(--color-info-darker) !important;
}
*/

@media (max-width: 768px) {
  .page-container.incidencias-me-page {
    padding: 70px 15px 30px 15px;
  }
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
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