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
      <DataTable
          v-if="incidencias.length > 0"
          :value="incidencias"
          paginator
          :rows="5"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          sortMode="multiple"
          tableStyle="min-width: 50rem"
          :loading="loading">
        <Column field="asunto" header="Asunto">
          <template #body="slotProps">
            {{ slotProps.data.asunto }}
          </template>
        </Column>
        <Column field="createdDate" header="Fecha Reporte">
          <template #body="slotProps">
            {{ slotProps.data.createdDate }}
          </template>
        </Column>
        <Column field="estadoIncidencia" header="Estado">
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
        <label for="incidenciaDescripcion" class="input-label">Descripción
          <i
            class="pi pi-info-circle info-icon"
            @click="showInfo('descripcion')"
            title="Haz clic para más información"
        ></i></label>
        <Textarea id="incidenciaDescripcion" v-model="nuevaIncidencia.descripcion" rows="5" class="input-field full-width" placeholder="Detalla el problema que has encontrado..." />
        <small v-if="nuevaIncidenciaErrors.descripcion" class="error-message p-error">{{ nuevaIncidenciaErrors.descripcion }}</small>
      </div>
      <div class="dialog-footer-buttons">
        <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button secondary-button" @click="isReportarModalVisible = false" />
        <Button type="submit" label="Reportar Incidencia" icon="pi pi-send" class="action-button primary-button" :loading="isSubmittingIncidencia" />
      </div>
    </form>
  </Dialog>

  <Dialog :header="modalTitle" v-model:visible="isInfoModalVisible" modal
          :style="{ width: '50vw', fontFamily: 'Montserrat' }"
          :breakpoints="{'960px': '75vw', '641px': '100vw'}">
    <div class="dialog-content" v-html="modalBody"></div>
  </Dialog>

</template>

<script lang="ts">
import { defineComponent } from 'vue';
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
  data() {
    return {
      incidencias: [] as Incidencia[],
      loading: true,
      isReportarModalVisible: false,
      isSubmittingIncidencia: false,
      nuevaIncidencia: {
        asunto: '',
        descripcion: ''
      },
      nuevaIncidenciaErrors: {
        asunto: '',
        descripcion: ''
      },
      isInfoModalVisible: false,
      modalTitle: '',
      modalBody: '',
    };
  },
  created() {
    this.fetchIncidencias();
  },
  methods: {
    async fetchIncidencias() {
      this.loading = true;
      try {
        this.incidencias = await getIncidenciasByUserGuid();
        console.log("Datos de incidencias:", this.incidencias);
      } catch (error: any) {
        console.error("Error al obtener las incidencias:", error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.message || 'No se pudieron cargar las incidencias.',
          life: 3000
        });
      } finally {
        this.loading = false;
      }
    },
    openReportarModal() {
      this.resetNuevaIncidenciaForm();
      this.isReportarModalVisible = true;
    },
    resetNuevaIncidenciaForm() {
      this.nuevaIncidencia = { asunto: '', descripcion: '' };
      this.nuevaIncidenciaErrors = { asunto: '', descripcion: '' };
      this.isSubmittingIncidencia = false;
    },
    validarIncidencia() {
      let isValid = true;
      this.nuevaIncidenciaErrors.asunto = '';
      this.nuevaIncidenciaErrors.descripcion = '';

      const asunto = this.nuevaIncidencia.asunto.trim();
      const descripcion = this.nuevaIncidencia.descripcion.trim();

      if (!asunto) {
        this.nuevaIncidenciaErrors.asunto = 'El asunto es obligatorio.';
        isValid = false;
      } else if (asunto.length > 255) {
        this.nuevaIncidenciaErrors.asunto = 'El asunto no puede exceder los 255 caracteres.';
        isValid = false;
      }

      if (!descripcion) {
        this.nuevaIncidenciaErrors.descripcion = 'La descripción es obligatoria.';
        isValid = false;
      } else if (descripcion.length > 1000) {
        this.nuevaIncidenciaErrors.descripcion = 'La descripción no puede exceder los 1000 caracteres.';
        isValid = false;
      }

      return isValid;
    },
    async handleReportarIncidencia() {
      if (!this.validarIncidencia()) return;

      this.isSubmittingIncidencia = true;

      try {
        const response = await createIncidencia(this.nuevaIncidencia);
        if (response) {
          this.isReportarModalVisible = false;
          await this.fetchIncidencias();
        } else {
          throw new Error('No se recibió respuesta al crear la incidencia.');
        }
      } catch (error: any) {
        console.error('Error al reportar la incidencia:', error);
        this.$toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.response?.data?.message || error.message || 'No se pudo reportar la incidencia.',
          life: 3000
        });
      } finally {
        this.isSubmittingIncidencia = false;
      }
    },
    getEstadoIncidenciaClass(estado: string | undefined): string {
      if (!estado) return 'status-unknown';
      switch (estado) {
        case 'PENDIENTE': return 'status-pendiente';
        case 'RESUELTO': return 'status-resuelto';
        default: return 'status-unknown';
      }
    },
    showInfo(infoType: string): void {
      switch (infoType) {
        case 'descripcion':
          this.modalTitle = 'Detalles de la Descripción';
          this.modalBody = `
            <p>Por favor, sé lo más detallado posible al describir tu incidencia. Incluye la siguiente información para que podamos ayudarte de manera más eficiente:</p>
            <ul>
              <li><strong>Número de Serie del Dispositivo:</strong> Este es crucial para identificar el equipo. Puedes encontrarlo en la parte trasera o inferior del dispositivo, o en la tabla que encontrarás en la ventana de "Mis Préstamos". Asegúrate de indicar el número de serie correcto.</li>
              <li><strong>Mensajes de Error:</strong> Si aparece algún mensaje en pantalla, anótalo.</li>
            </ul>
            <p>Cuanta más información proporciones, más rápido podremos resolver tu incidencia.</p>
          `;
          break;
        default:
          this.modalTitle = 'Información';
          this.modalBody = 'No hay información disponible para este campo.';
      }
      this.isInfoModalVisible = true;
    },
  },
  mounted() {
    this.$toast = useToast();
    this.$router = useRouter();
  }
});
</script>

<style scoped>
.page-container.incidencias-me-page {
  max-width: 900px;
  box-sizing: border-box;
  margin: 0 auto;
  padding: 30px 30px 50px;
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

.info-icon {
  color: var(--color-interactive);
  cursor: pointer;
  margin-left: 8px;
  font-size: 1rem;
  vertical-align: middle;
  transition: color 0.2s ease;
}

.info-icon:hover {
  color: var(--color-interactive-darker);
}

@media (max-width: 768px) {
  .page-container.incidencias-me-page {
    max-width: 900px;
    box-sizing: border-box;
    margin: 0 auto;
    padding: 30px 30px 50px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .page-header .header-left {
    justify-content: space-between;
  }

  .page-header h2 {
    font-size: 1.5rem;
    margin-bottom: 0;
  }

  .page-header .header-right {
    display: flex;
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

  :deep(.p-datatable) {
    font-size: 0.9rem;
    min-width: 100% !important;
  }

  :deep(.p-datatable .p-datatable-tbody > tr > td) {
    white-space: normal !important;
    word-wrap: break-word;
  }

  .dialog-footer-buttons {
    flex-direction: column;
    gap: 10px;
  }
  .dialog-footer-buttons > button {
    width: 100%;
  }
}
</style>