<template>
  <div class="detalle-container">
    <div class="detalle-header-actions">
      <button @click="goBack" class="back-button" title="Volver a Sanciones">
        <i class="pi pi-arrow-left"></i>
      </button>
      <button class="action-button edit-button" @click="toggleEdit" title="Editar Sanción">
        <i :class="editable ? 'pi pi-times' : 'pi pi-pencil'"></i> {{ editable ? 'Cancelar' : 'Editar' }}
      </button>
    </div>

    <div class="sancion-details" v-if="sancionData">
      <div class="details-header">
        <h2>Detalles de la Sanción</h2>
        <i class="pi pi-ban header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label for="guid">GUID</label>
          <div class="readonly-field">{{ sancionData.guid || '-' }}</div>
        </div>

        <div class="form-group">
          <label for="tipo">Tipo</label>
          <select v-if="editable" id="tipo" class="input-field" v-model="sancionData.tipoSancion">
            <option value="ADVERTENCIA">Advertencia</option>
            <option value="BLOQUEO_TEMPORAL">Bloqueo Temporal</option>
            <option value="INDEFINIDO">Indefinido</option>
          </select>
          <div v-else>
             <span :class="['status-badge', getTipoSancionClass(sancionData.tipoSancion)]">
               {{ formatTipoSancion(sancionData.tipoSancion) }}
             </span>
          </div>
        </div>

        <div class="form-group">
          <label for="user">Usuario</label>
          <div class="readonly-field">{{ sancionData.user?.numeroIdentificacion || sancionData.userGuid || '-' }}</div>
        </div>

        <div class="form-group">
          <label>Fecha de Sanción</label>
          <div class="readonly-field">{{ sancionData.fechaSancion }}</div>
        </div>

      </div>

      <div v-if="editable" class="update-button-wrapper">
        <button class="action-button update-button" @click="actualizarSancion">
          Actualizar Sanción
        </button>
      </div>

    </div>

    <div v-else class="loading-message">
      <p>Cargando detalles de la sanción...</p>
    </div>

  </div>

  <Toast />
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import {getSancionByGuid, actualizarSancion} from "@/services/SancionService.ts";
import { useToast } from "primevue/usetoast";

type TipoSancion = 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'BLOQUEO_INDEFINIDO';

export default defineComponent({
  name: "SancionDetalle",
  inheritAttrs: false,
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      sancionData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;

      const sancion = await getSancionByGuid(guidString);

      this.sancionData = sancion;
      this.originalData = JSON.parse(JSON.stringify(sancion));
      console.log(sancion);

    } catch (error: any) {
      console.error("Error al obtener los detalles de la sanción:", error);
      this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar la sanción.', life: 3000 });
    }
  },
  methods: {
    formatTipoSancion(tipoSancion: 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'INDEFINIDO' | undefined): string {
      if (!tipoSancion) {
        return '';
      }
      return tipoSancion.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    getTipoSancionClass(tipoSancion: TipoSancion | undefined): string {
      if (!tipoSancion) return 'status-unknown';
      switch (tipoSancion) {
        case 'ADVERTENCIA': return 'status-advertencia';
        case 'BLOQUEO_TEMPORAL': return 'status-bloqueo-temporal';
        case 'BLOQUEO_INDEFINIDO': return 'status-indefinido';
        default: return 'status-unknown';
      }
    },
    goBack() {
      this.$router.back();
    },
    async actualizarSancion() {
      if (!this.sancionData || !this.originalData) {
        console.warn("Datos de la sanción no disponibles para actualizar.");
        this.toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'Datos no disponibles.', life: 3000 });
        return;
      }

      const hasChanged = this.sancionData.tipoSancion !== this.originalData.tipoSancion;

      if (hasChanged) {
        try {
          const updatePayload = {
            tipoSancion: this.sancionData.tipoSancion,
          };

          console.log("Actualizando sanción con payload:", updatePayload);

          const sancionActualizada = await actualizarSancion(this.sancionData.guid, updatePayload);

          if (sancionActualizada) {
            this.originalData = JSON.parse(JSON.stringify(sancionActualizada));
            this.sancionData = JSON.parse(JSON.stringify(sancionActualizada));

            this.toast.add({ severity: 'success', summary: 'Éxito', detail: 'Sanción actualizada.', life: 3000 });
            this.editable = false;
          } else {
            throw new Error("La actualización no devolvió datos válidos.");
          }

        } catch (error: any) {
          console.error('Error al actualizar la sanción:', error);
          const errorMessage = error.response?.data?.message || error.message || 'No se pudo actualizar la sanción.';
          this.toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });
          if (this.originalData) {
            this.sancionData = JSON.parse(JSON.stringify(this.originalData));
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

.sancion-details {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  max-width: 500px;
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
  grid-template-columns: 1fr;
  gap: 25px;
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

.readonly-field {
  padding: 10px 12px;
  font-size: 1rem;
  line-height: 1.4;
  color: var(--color-text-dark);
  background-color: var(--color-background-main);
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  height: 44px;
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
}

select.input-field {
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
  cursor: pointer;
  appearance: none;
  height: 44px;
}

select.input-field:focus {
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
  line-height: 1.4;
  vertical-align: middle;
}

.status-advertencia {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-bloqueo-temporal {
  background-color: rgba(var(--color-error-rgb), 0.1);
  color: var(--color-error);
}

.status-indefinido {
  background-color: rgba(114, 28, 36, 0.15);
  color: #721c24;
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: var(--color-text-dark);
}

@media (max-width: 768px) {
  .detalle-container {
    padding: 70px 20px 30px 20px;
    max-width: 100%;
  }
  .details-grid {
    gap: 20px;
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
  .sancion-details {
    padding: 20px;
  }
  .form-group label {
    font-size: 0.8rem;
  }
  select.input-field, .readonly-field, .form-group > div:has(.status-badge) {
    font-size: 0.95rem;
    padding: 8px 10px;
    height: 40px;
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