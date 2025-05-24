<template>
  <div class="detalle-container">
    <div class="detalle-header-actions">
      <button @click="goBack" class="back-button" title="Volver a Incidencias">
        <i class="pi pi-arrow-left"></i>
      </button>
      <button
          v-if="incidenciaData && incidenciaData.estadoIncidencia !== 'RESUELTO'"
          class="action-button edit-button"
          @click="toggleEdit"
          title="Editar Incidencia"
      >
        <i :class="editable ? 'pi pi-times' : 'pi pi-pencil'"></i> {{ editable ? 'Cancelar' : 'Editar' }}
      </button>
    </div>

    <div class="incidencia-details" v-if="incidenciaData">
      <div class="details-header">
        <h2>Detalles de la Incidencia</h2>
        <i class="pi pi-exclamation-triangle header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group span-3"> <label for="asunto">Asunto</label>
          <input v-if="editable" type="text" id="asunto" class="input-field" v-model="incidenciaData.asunto"/>
          <div v-else class="readonly-field asunto-display">{{ incidenciaData.asunto }}</div>
        </div>

        <div class="form-group span-3"> <label for="descripcion">Descripción</label>
          <textarea :readonly="!editable" id="descripcion" class="input-field textarea-field" v-model="incidenciaData.descripcion" rows="4"></textarea>
        </div>

        <div class="form-group">
          <label for="estado">Estado</label>
          <select v-if="editable" id="estado" class="input-field" v-model="incidenciaData.estadoIncidencia">
            <option value="PENDIENTE">Pendiente</option>
            <option value="RESUELTO">Resuelto</option>
          </select>
          <div v-else>
                <span :class="['status-badge', getStatusClass(incidenciaData.estadoIncidencia)]">
                    {{ incidenciaData.estadoIncidencia }}
                </span>
          </div>
        </div>

        <div class="form-group">
          <label for="userNumIdentificacion">Usuario</label>
          <div class="readonly-field">{{ incidenciaData.user?.numeroIdentificacion || incidenciaData.userGuid || 'N/A' }}</div>
        </div>

        <div class="form-group">
          <label for="fechaCreacion">Fecha Creación</label>
          <div class="readonly-field">{{ incidenciaData.createdDate }}</div>
        </div>

        <div class="form-group">
          <label for="isDeleted">Marcado Borrado</label>
          <div class="readonly-field">{{ incidenciaData.isDeleted ? 'SI' : 'NO' }}</div>
        </div>
      </div>

      <div v-if="editable" class="update-button-wrapper">
        <button class="action-button update-button" @click="actualizarIncidencia">
          Actualizar Incidencia
        </button>
      </div>
    </div>

    <div v-else class="loading-message">
      <p>Cargando detalles de la incidencia...</p>
    </div>
  </div>

</template>

<script lang="ts">
import {defineComponent} from 'vue'
import {getIncidenciaByGuidAdmin, actualizarIncidencia} from "@/services/IncidenciaService.ts";
import {useToast} from "primevue/usetoast";

type IncidenceState = 'PENDIENTE' | 'RESUELTO';

export default defineComponent({
  name: "IncidenciaDetalle",
  inheritAttrs: false,
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      incidenciaData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const incidencia = await getIncidenciaByGuidAdmin(guidString);
      this.incidenciaData = incidencia;
      this.originalData = JSON.parse(JSON.stringify(incidencia));
      console.log(incidencia)
    } catch (error) {
      console.error("Error al obtener los detalles de la incidencia:", error);
      this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar la incidencia.', life: 3000 });
    }
  },
  methods: {
    toggleEdit() {
      this.editable = !this.editable;
      if (!this.editable && this.originalData) {
        this.incidenciaData = JSON.parse(JSON.stringify(this.originalData));
      }
    },
    goBack() {
      this.$router.back();
    },
    getStatusClass(estado: IncidenceState | undefined): string {
      if (!estado) return 'status-unknown';
      switch (estado) {
        case 'PENDIENTE': return 'status-pendiente';
        case 'RESUELTO': return 'status-resuelto';
        default: return 'status-unknown';
      }
    },
    async actualizarIncidencia() {
      if (!this.incidenciaData || !this.originalData) {
        console.warn("Datos de la incidencia no disponibles para actualizar.");
        this.toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'Datos no disponibles.', life: 3000 });
        return;
      }

      const hasChanged = this.incidenciaData.estadoIncidencia !== this.originalData.estadoIncidencia;

      if (hasChanged) {
        try {
          const updatePayload = {
            estadoIncidencia: this.incidenciaData.estadoIncidencia,
          };

          console.log("Actualizando incidencia con payload:", updatePayload);

          const incidenciaActualizada = await actualizarIncidencia(this.incidenciaData.guid, updatePayload);

          if(incidenciaActualizada) {
            this.originalData = JSON.parse(JSON.stringify(incidenciaActualizada));
            this.incidenciaData = JSON.parse(JSON.stringify(incidenciaActualizada));

            this.editable = false;
          } else {
            throw new Error("La actualización no devolvió datos válidos.");
          }

        } catch (error: any) {
          console.error('Error al actualizar la incidencia:', error);
          const errorMessage = error.response?.data?.message || error.message || 'No se pudo actualizar la incidencia.';
          this.toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });

          if (this.originalData) {
            this.incidenciaData = JSON.parse(JSON.stringify(this.originalData));
          }
        }
      } else {
        console.log("No se detectaron cambios.");
        this.toast.add({ severity: 'info', summary: 'Info', detail: 'No se realizaron cambios.', life: 3000 });

        this.editable = false;
      }
    }
  }
})
</script>

<style scoped>
.detalle-container {
  padding: 80px 30px 40px 30px;
  max-width: 900px;
  margin: 0 auto;
  box-sizing: border-box;
}

.detalle-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  font-size: 1rem;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
  width: 40px;
  height: 40px;
  line-height: 1;
}

.back-button:hover {
  background-color: var(--color-interactive-darker);
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(var(--color-primary-rgb), 0.2);
}

.action-button {
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

.action-button:active {
  transform: scale(0.98);
}

.action-button i {
  font-size: 1rem;
}

.edit-button {
  background-color: var(--color-interactive);
  color: white;
}

.edit-button:hover {
  background-color: var(--color-interactive-darker);
}

.incidencia-details {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--color-neutral-medium);
}

.details-header h2 {
  color: var(--color-primary);
  margin: 0;
  font-size: 1.6rem;
  font-weight: 600;
}

.header-icon {
  font-size: 2.5rem;
  color: var(--color-primary);
  opacity: 0.7;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px 25px;
}

.form-group.span-3 {
  grid-column: span 3;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.85rem;
  color: var(--color-text-dark);
  font-weight: 500;
  text-transform: uppercase;
  opacity: 0.8;
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

.readonly-field {
  padding: 10px 12px;
  font-size: 1rem;
  line-height: 1.4;
  color: var(--color-text-dark);
  background-color: var(--color-background-main);
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  min-height: calc(1.4em + 20px + 2px);
  word-wrap: break-word;
  white-space: pre-wrap;
  box-sizing: border-box;
  width: 100%;
}

.readonly-field.asunto-display {
  font-weight: 600;
  font-size: 1.1rem;
  background-color: transparent;
  border: none;
  padding: 0;
  min-height: auto;
}

input.input-field,
select.input-field,
textarea.input-field {
  border-radius: 8px;
  padding: 10px 12px;
  border: 1px solid var(--color-neutral-medium);
  background-color: white;
  color: var(--color-text-dark);
  width: 100%;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
  font-size: 1rem;
  line-height: 1.4;
}

select.input-field {
  cursor: pointer;
  appearance: none;
}

textarea.input-field {
  resize: vertical;
  min-height: 80px;
}

input.input-field[readonly],
textarea.input-field[readonly] {
  background-color: var(--color-background-main);
  cursor: default;
  opacity: 0.9;
}

input.input-field[readonly]:focus,
textarea.input-field[readonly]:focus {
  border-color: var(--color-neutral-medium);
  box-shadow: none;
}

input.input-field:not([readonly]):focus,
select.input-field:focus,
textarea.input-field:not([readonly]):focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
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

.update-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
}

.update-button {
  background-color: var(--color-interactive);
  color: white;
}

.update-button:hover {
  background-color: var(--color-interactive-darker);
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: var(--color-text-dark);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease-in-out;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 992px) {
  .details-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .form-group.span-3 {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .detalle-container {
    padding: 70px 20px 30px 20px;
    max-width: 100%;
  }

  .details-grid {
    grid-template-columns: 1fr;
    gap: 25px;
  }

  .form-group.span-3 {
    grid-column: span 1;
  }

  .detalle-header-actions {
    margin-bottom: 20px;
  }

  .details-header h2 {
    font-size: 1.4rem;
  }

  .header-icon {
    font-size: 2rem;
  }

  .back-button {
    width: 36px;
    height: 36px;
  }

  .action-button {
    padding: 6px 12px;
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .incidencia-details {
    padding: 20px;
  }

  .form-group label {
    font-size: 0.8rem;
  }

  input.input-field,
  select.input-field,
  textarea.input-field,
  .readonly-field {
    font-size: 0.95rem;
    padding: 8px 10px;
  }

  .readonly-field.asunto-display {
    font-size: 1rem;
  }

  .details-header h2 {
    font-size: 1.2rem;
  }

  .action-button {
    font-size: 0.8rem;
    padding: 5px 10px;
  }
}
</style>