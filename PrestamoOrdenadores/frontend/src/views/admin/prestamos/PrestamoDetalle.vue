<template>
  <MenuBar />
  <div class="detalle-container">

    <div class="detalle-header-actions">
      <button @click="goBack" class="back-button" title="Volver a Prestamos">
        <i class="pi pi-arrow-left"></i>
      </button>
      <button class="action-button edit-button" @click="toggleEdit" title="Editar Préstamo">
        <i :class="editable ? 'pi pi-times' : 'pi pi-pencil'"></i> {{ editable ? 'Cancelar' : 'Editar' }}
      </button>
    </div>

    <div class="prestamo-details" v-if="prestamoData">
      <div class="details-header">
        <h2>Detalles del Préstamo</h2>
        <i class="pi pi-arrow-right-arrow-left header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label for="user">Usuario</label>
          <input readonly type="text" id="userNumIdentificacion" class="input-field" v-model="prestamoData.user.numeroIdentificacion"/>
        </div>

        <div class="form-group">
          <label for="dispositivo">Dispositivo</label>
          <input readonly type="text" id="dispositivo" class="input-field" v-model="prestamoData.dispositivo.numeroSerie"/>
        </div>

        <div class="form-group">
          <label for="estado">Estado</label>
          <select v-if="editable" id="estado" class="input-field" v-model="prestamoData.estadoPrestamo">
            <option value="VENCIDO">VENCIDO</option>
            <option value="EN_CURSO">EN CURSO</option>
            <option value="CANCELADO">CANCELADO</option>
            <option value="DEVUELTO">DEVUELTO</option>
          </select>
          <div v-else>
                <span :class="['status-badge', getStatusClass(prestamoData.estadoPrestamo)]">
                    {{ formatEstado(prestamoData.estadoPrestamo) }}
                </span>
          </div>
        </div>

        <div class="form-group">
          <label for="fechaPrestamo">Fecha de Préstamo</label>
          <input readonly type="text" id="fechaPrestamo" class="input-field" v-model="prestamoData.fechaPrestamo"/>
        </div>

        <div class="form-group">
          <label for="fechaDevolucion">Fecha de Devolución</label>
          <input readonly type="text" id="fechaDevolucion" class="input-field" :value="prestamoData.fechaDevolucion || 'Pendiente'"/>
        </div>

      </div>

      <div v-if="editable" class="update-button-wrapper">
        <button class="action-button update-button" @click="actualizarPrestamo">
          Actualizar Préstamo
        </button>
      </div>
    </div>
    <div v-else>
      <p>Cargando detalles del préstamo...</p>
    </div>
  </div>

  <Toast />
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {actualizarPrestamo, cancelarPrestamo, getPrestamoByGuid, type Prestamo} from "@/services/PrestamoService.ts";
import {useToast} from "primevue/usetoast";

export default defineComponent({
  name: "PrestamoDetalle",
  components: {MenuBar},
  inheritAttrs: false,
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      prestamoData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const prestamo = await getPrestamoByGuid(guidString);
      if (prestamo) {
        this.prestamoData = JSON.parse(JSON.stringify(prestamo));
        this.originalData = JSON.parse(JSON.stringify(prestamo));
        console.log("Datos del préstamo cargados:", this.prestamoData);
      } else {
        console.error(`No se encontró el préstamo con GUID: ${guidString}`);
        this.toast.add({ severity: 'error', summary: 'Error', detail: 'Préstamo no encontrado.', life: 3000 });
      }
    } catch (error) {
      console.error("Error al obtener los detalles de la prestamo:", error);
    }
  },
  methods: {
    formatEstado(estadoPrestamo: 'VENCIDO' | 'EN_CURSO' | 'CANCELADO' | 'DEVUELTO' | undefined): string {
      if (!estadoPrestamo) {
        return '';
      }
      return estadoPrestamo.replace(/_/g, ' ');
    },
    toggleEdit() {
      this.editable = !this.editable;
    },
    goBack() {
      this.$router.back();
    },
    getStatusClass(estado: string): string {
      if (!estado) return 'status-unknown';
      switch (estado.toUpperCase()) {
        case 'EN_CURSO': return 'status-en-curso';
        case 'VENCIDO': return 'status-vencido';
        case 'CANCELADO': return 'status-cancelado';
        case 'DEVUELTO': return 'status-devuelto';
        default: return 'status-unknown';
      }
    },
    async actualizarPrestamo() {
      if (!this.prestamoData || !this.originalData) {
        console.warn("Datos del préstamo no disponibles para guardar cambios.");
        this.toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'Datos no disponibles.', life: 3000 });
        return;
      }

      const nuevoEstado = this.prestamoData.estadoPrestamo;
      const estadoOriginal = this.originalData.estadoPrestamo;

      if (nuevoEstado === estadoOriginal) {
        console.log("No se detectaron cambios en el estado del préstamo.");
        this.toast.add({ severity: 'info', summary: 'Info', detail: 'No se realizaron cambios en el estado.', life: 3000 });
        this.editable = false;
        return;
      }

      try {
        let prestamoResultado: Prestamo | null = null;

        console.log(`Intentando cambiar estado de préstamo ${this.prestamoData.guid} de ${estadoOriginal} a ${nuevoEstado}`);

        if (nuevoEstado === "CANCELADO") {
          this.toast.add({ severity: 'info', summary: 'Procesando', detail: 'Cancelando préstamo...', life: 2000 });
          prestamoResultado = await cancelarPrestamo(this.prestamoData.guid);
        } else {
          const updatePayload = {
            estadoPrestamo: nuevoEstado,
          };
          this.toast.add({ severity: 'info', summary: 'Procesando', detail: 'Actualizando préstamo...', life: 2000 });
          prestamoResultado = await actualizarPrestamo(this.prestamoData.guid, updatePayload);
        }

        if (prestamoResultado && prestamoResultado.guid) {
          this.originalData = JSON.parse(JSON.stringify(prestamoResultado));
          this.prestamoData = JSON.parse(JSON.stringify(prestamoResultado));
          this.editable = false;
        } else {
          throw new Error("La operación no devolvió datos válidos o fue abortada (token ausente).");
        }

      } catch (error: any) {
        console.error('Error al guardar cambios del préstamo (vista):', error);
        const backendError = error.response?.data;
        let detailMessage = 'No se pudo guardar los cambios del préstamo.';

        if (backendError) {
          if (typeof backendError.message === 'string') {
            detailMessage = backendError.message;
          } else if (backendError.error && typeof backendError.error === 'string') {
            detailMessage = backendError.error;
          } else if (Array.isArray(backendError.errors) && backendError.errors.length > 0) {
            detailMessage = backendError.errors.map((e: any) => e.defaultMessage || e.message).join(', ');
          }
        } else if (error.message) {
          detailMessage = error.message;
        }

        this.toast.add({ severity: 'error', summary: 'Error', detail: detailMessage, life: 5000 });

        if (this.originalData) {
          this.prestamoData = JSON.parse(JSON.stringify(this.originalData));
        }
      }
    },
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
  margin-right: -400px;
}

.edit-button:hover {
  background-color: var(--color-interactive-darker);
}

.prestamo-details {
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

.action-buttons-footer button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.action-buttons-footer button:active {
  transform: scale(0.98);
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
}

.status-en-curso {
  background-color: rgba(var(--color-interactive-rgb), 0.15);
  color: var(--color-interactive-darker);
}

.status-cancelado {
  background-color: rgba(var(--color-error-rgb), 0.1);
  color: var(--color-error);
}

.status-vencido {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-warning);
}

.status-devuelto {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-unknown {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
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
</style>