<template>
  <MenuBar />
  <div class="detalle-container">

    <div class="boton-atras-wrapper">
      <button @click="goBack" class="back-button" title="Volver a Préstamos">
        <i class="pi pi-arrow-left"></i>
      </button>
    </div>

    <div class="prestamo-details" v-if="prestamoData">
      <div class="details-header">
        <h2>Detalles del Préstamo</h2>
        <i class="pi pi-list-check header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label for="userGuid">GUID Usuario</label>
          <input readonly type="text" id="userGuid" class="input-field" v-model="prestamoData.userGuid"/>
        </div>

        <div class="form-group">
          <label for="dispositivoGuid">GUID Dispositivo</label>
          <input readonly type="text" id="dispositivoGuid" class="input-field" v-model="prestamoData.dispositivoGuid"/>
        </div>

        <div class="form-group">
          <label for="estado">Estado</label>
          <select v-if="editable" id="estado" class="input-field" v-model="prestamoData.estadoPrestamo">
            <option value="VENCIDO">VENCIDO</option>
            <option value="EN_CURSO">EN CURSO</option>
            <option value="CANCELADO">CANCELADO</option>
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
import {getPrestamoByGuid} from "@/services/PrestamoService.ts";
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
      this.prestamoData = prestamo;
      console.log(prestamo)
    } catch (error) {
      console.error("Error al obtener los detalles de la prestamo:", error);
    }
  },
  methods: {
    formatEstado(estadoPrestamo: 'VENCIDO' | 'EN_CURSO' | 'CANCELADO' | undefined): string {
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
        default: return 'status-unknown';
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


.boton-atras-wrapper {
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

.action-buttons-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
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
  color: var(--color-success);
}

.status-unknown {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
}
</style>