<template>
  <div class="page-container sanciones-me-page">
    <div class="page-header">
      <div class="header-left">
        <h2>Mis Sanciones</h2>
      </div>
    </div>

    <div class="table-card">
      <DataTable
          v-if="sanciones.length > 0"
          :value="sanciones"
          paginator
          :rows="5"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          tableStyle="min-width: 50rem"
          sortMode="multiple"
          :loading="loading"
          class="p-datatable-gridlines"
      >
        <Column field="fechaInicio" header="Fecha Inicio" sortable>
          <template #body="slotProps">
            {{ formatDate(slotProps.data.fechaSancion) }}
          </template>
        </Column>
        <Column field="fechaFin" header="Fecha Fin" sortable>
          <template #body="slotProps">
            {{ formatDate(slotProps.data.fechaFin) }}
          </template>
        </Column>
        <Column field="tipoSancion" header="Tipo" sortable>
          <template #body="slotProps">
            <span :class="['status-badge', getSancionTipoClass(slotProps.data.tipoSancion)]">
              {{ slotProps.data.tipoSancion }}
            </span>
          </template>
        </Column>
      </DataTable>

      <p v-if="!loading && sanciones.length === 0 && !error" class="no-data-message">
        No has recibido ninguna sanción.
      </p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Message from 'primevue/message';
import { getSancionesByUserGuid, type Sancion } from "@/services/SancionService.ts";

export default defineComponent({
  name: "SancionesMe",
  components: {
    DataTable,
    Column,
    Message
  },
  data() {
    return {
      sanciones: [] as Sancion[],
      loading: true,
      error: null as string | null,
    };
  },
  methods: {
    async loadSanciones(): Promise<void> {
      this.loading = true;
      this.error = null;
      try {
        this.sanciones = await getSancionesByUserGuid();
        console.log("Sanciones cargadas:", this.sanciones);
      } catch (err: any) {
        console.error("Error al cargar las sanciones:", err);
        this.error = err.message || "No se pudieron cargar tus sanciones.";
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateString: string): string {
      if (!dateString) return '';
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return dateString;
      const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short', day: 'numeric' };
      return date.toLocaleDateString('es-ES', options);
    },
    getSancionTipoClass(tipoSancion: string): string {
      switch (tipoSancion) {
        case 'ADVERTENCIA':
          return 'status-sancion-advertencia';
        case 'BLOQUEO_TEMPORAL':
          return 'status-sancion-temporal';
        case 'INDEFINIDO':
          return 'status-sancion-indefinido';
        default:
          return 'status-unknown';
      }
    },
  },
  mounted() {
    this.loadSanciones();
  },
});
</script>

<style scoped>
.page-container.sanciones-me-page {
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

:deep(.p-datatable .p-datatable-tbody > tr:nth-child(even)) {
  background-color: var(--surface-a);
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

.status-sancion-advertencia {
  background-color: rgba(var(--color-warning-rgb), 0.15);
  color: #B45309;
}

.status-sancion-temporal {
  background-color: rgba(var(--color-error-rgb), 0.15);
  color: var(--color-error);
}

.status-sancion-indefinido {
  background-color: rgba(var(--color-error-rgb), 0.15);
  color: var(--color-error);
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

@media (max-width: 768px) {
  .page-container.sanciones-me-page {
    padding: 70px 15px 30px 15px;
  }
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    background-color: var(--surface-b);
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
  .table-card {
    padding: 10px;
  }
}
</style>