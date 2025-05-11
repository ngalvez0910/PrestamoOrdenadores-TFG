<template>
  <MenuBar />
  <div class="detalle-container">
    <div class="detalle-header-actions">
      <button @click="goBack" class="back-button" title="Volver a Dispositivos">
        <i class="pi pi-arrow-left"></i>
      </button>
      <button class="action-button edit-button" @click="toggleEdit" title="Editar Dispositivo">
        <i :class="editable ? 'pi pi-times' : 'pi pi-pencil'"></i> {{ editable ? 'Cancelar' : 'Editar' }}
      </button>
    </div>

    <div class="dispositivo-details" v-if="dispositivoData">
      <div class="details-header">
        <h2>Detalles del Dispositivo</h2>
        <i class="pi pi-desktop header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label for="numeroSerie">Número de Serie</label>
          <input readonly type="text" id="numeroSerie" class="input-field" v-model="dispositivoData.numeroSerie"/>
        </div>

        <div class="form-group">
          <label for="componentes">Componentes</label>
          <input readonly type="text" id="componentes" class="input-field" v-model="dispositivoData.componentes"/>
        </div>

        <div class="form-group">
          <label for="estado">Estado</label>
          <select v-if="editable" id="estado" class="input-field" v-model="dispositivoData.estado">
            <option value="DISPONIBLE">Disponible</option>
            <option value="NO_DISPONIBLE">No Disponible</option>
            <option value="PRESTADO">Prestado</option>
          </select>
          <div v-else>
                <span :class="['status-badge', getStatusClass(dispositivoData.estado)]">
                    {{ dispositivoData.estado }}
                </span>
          </div>
        </div>

        <div class="form-group">
          <label for="incidencias">Incidencia Asociada (GUID)</label>
          <input :readonly="!editable" type="text" id="incidencias" class="input-field" v-model="dispositivoData.incidenciaGuid"/>
        </div>

      </div>

      <div v-if="editable" class="update-button-wrapper">
        <button class="action-button update-button" @click="actualizarDispositivo">
          Actualizar Dispositivo
        </button>
      </div>

    </div>

    <div v-else class="loading-message">
      <p>Cargando detalles del dispositivo...</p>
    </div>
  </div>

  <Toast />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import MenuBar from '@/components/AdminMenuBar.vue';
import {actualizarDispositivo, getDispositivoByGuid,} from '@/services/DispositivoService.ts';
import {useToast} from "primevue/usetoast";

type DeviceState = 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO';

export default defineComponent({
  name: 'DispositivoDetalle',
  components: { MenuBar },
  inheritAttrs: false,
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      dispositivoData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const dispositivo = await getDispositivoByGuid(guidString);
      this.dispositivoData = { ...dispositivo };
      this.originalData = { ...dispositivo };
      console.log(dispositivo);
    } catch (error) {
      console.error('Error al obtener los detalles del dispositivo:', error);
    }
  },
  methods: {
    formatEstado(estado: 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO' | undefined): string {
      if (!estado) {
        return '';
      }
      return estado.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    goBack() {
      this.$router.back();
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
    async actualizarDispositivo() {
      if (!this.dispositivoData || !this.originalData) {
        console.warn("Datos del dispositivo no disponibles para actualizar.");
        return;
      }

      const hasChanged = this.dispositivoData.componentes !== this.originalData.componentes ||
          this.dispositivoData.estado !== this.originalData.estado ||
          this.dispositivoData.incidenciaGuid !== this.originalData.incidenciaGuid;

      if (hasChanged) {
        try {
          const updatePayload = {
            componentes: this.dispositivoData.componentes,
            estadoDispositivo: this.dispositivoData.estado,
            incidenciaGuid: this.dispositivoData.incidenciaGuid || null,
          };

          console.log("Actualizando dispositivo con payload:", updatePayload);

          const dispositivoActualizado = await actualizarDispositivo(this.dispositivoData.guid, updatePayload);

          if (dispositivoActualizado) {
            this.originalData = {
              guid: dispositivoActualizado.guid,
              numeroSerie: dispositivoActualizado.numeroSerie,
              componentes: dispositivoActualizado.componentes,
              estado: dispositivoActualizado.estado,
              incidenciaGuid: dispositivoActualizado.incidencia?.guid || null
            };
            this.dispositivoData = JSON.parse(JSON.stringify(this.originalData));

            this.toast.add({ severity: 'success', summary: 'Éxito', detail: 'Dispositivo actualizado.', life: 3000 });

            this.editable = false;

          } else {
            throw new Error("La actualización no devolvió datos válidos o falló en el servicio.");
          }

        } catch (error: any) {
          console.error('Error al actualizar el dispositivo (componente):', error);
          const errorMessage = error.response?.data?.message || error.message || 'No se pudo actualizar el dispositivo.';
          this.toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });

          if (this.originalData) {
            this.dispositivoData = JSON.parse(JSON.stringify(this.originalData));
          }
        }
      } else {
        console.log("No se detectaron cambios.");
        this.toast.add({ severity: 'info', summary: 'Info', detail: 'No se realizaron cambios.', life: 3000 });

        this.editable = false;
      }
    },
  },
});
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
  margin-top: 5%;
  margin-left: 40%;
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
  margin-right: -135%;
}

.edit-button:hover {
  background-color: var(--color-interactive-darker);
}

.dispositivo-details {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  margin-left: 55%;
  min-width: 500px;
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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px 25px;
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
  border-radius: 8px;
  min-height: calc(1.4em + 20px + 2px);
  word-wrap: break-word;
}

input.input-field,
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
}

select.input-field {
  cursor: pointer;
  appearance: none;
}

input.input-field[readonly] {
  background-color: var(--color-background-main);
  cursor: default;
  opacity: 0.9;
}

input.input-field[readonly]:focus {
  border-color: var(--color-neutral-medium);
  box-shadow: none;
}

input.input-field:not([readonly]):focus,
select.input-field:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
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

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
  text-align: center;
  display: inline-block;
  text-transform: uppercase;
  line-height: 1.4;
  vertical-align: middle;
}

.status-disponible {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-prestado {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-no-disponible {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
  opacity: 0.9;
}

@media (max-width: 768px) {
  .detalle-container {
    padding: 70px 20px 30px 20px;
  }

  .details-grid {
    grid-template-columns: 1fr;
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
</style>