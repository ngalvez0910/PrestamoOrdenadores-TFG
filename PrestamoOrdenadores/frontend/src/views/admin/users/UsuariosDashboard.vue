<template>
  <div class="usuarios-container"> <div class="filters">
    <label for="search-input">Buscar:</label>
    <input id="search-input" type="text" v-model="search" placeholder="Buscar por Email, Nombre, Rol..." @input="handleSearchInput" />
  </div>

    <div class="table-wrapper">
      <DataTable
          :value="datos"
          paginator
          :rows="5"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          :totalRecords="totalRecords"
          :loading="loading"
          lazy @page="onPage"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Mostrando {first} a {last} de {totalRecords} usuarios"
          responsiveLayout="scroll"
          class="p-datatable-custom"
      >
        <Column field="numeroIdentificacion" header="Núm. Identificación" style="min-width: 200px;"></Column>
        <Column field="email" header="Email" style="min-width: 200px;"></Column>
        <Column header="Nombre" style="min-width: 200px;">
          <template #body="slotProps">
            <span>{{ slotProps.data.nombre }} {{ slotProps.data.apellidos }}</span>
          </template>
        </Column>
        <Column field="curso" header="Curso" style="min-width: 100px;"></Column>
        <Column field="tutor" header="Tutor" style="min-width: 150px;"></Column>
        <Column field="rol" header="Rol" style="min-width: 100px;">
          <template #body="slotProps">
                 <span :class="['status-badge', getRolClass(slotProps.data.rol)]">
                     {{ slotProps.data.rol }}
                 </span>
          </template>
        </Column>

        <Column header="Acciones" style="min-width:100px; width: 100px; text-align: center;">
          <template #body="slotProps">
            <div class="action-buttons">
              <button @click="verUsuario(slotProps.data)" class="action-button view-button" title="Ver Detalles Usuario">
                <i class="pi pi-info-circle"></i>
              </button>
              <button
                  @click="deleteUsuario(slotProps.data)"
                  :class="['action-button', 'delete-button', { 'disabled-button': !slotProps.data.isActivo }]"
                  :title="slotProps.data.isActivo ? 'Eliminar/Desactivar Usuario' : 'Este usuario está inactivo'"
                  :disabled="!slotProps.data.isActivo"
              >
                <i class="pi pi-trash"></i>
              </button>
            </div>
          </template>
        </Column>

        <template #empty>
          No se encontraron usuarios.
        </template>
        <template #loading>
          Cargando datos de usuarios...
        </template>
      </DataTable>
    </div>
  </div>

  <Dialog v-model:visible="showDeleteDialog" header="Confirmar Eliminación" modal :draggable="false" :style="{ width: '50vw', fontFamily: 'Montserrat, sans-serif' }">
    <div v-if="userToDelete">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem;"/><br>
      <span>¿Está seguro de que desea eliminar el usuario con email <strong>{{ userToDelete.email }}</strong>? Esta acción no se puede deshacer.</span>
    </div>
    <template #footer>
      <Button label="Cancelar" icon="pi pi-times" class="p-button-text action-button-dialog secondary-button" @click="showDeleteDialog = false" />
      <Button label="Eliminar" class="action-button-dialog primary-button" severity="danger" @click="confirmDelete" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import axios from 'axios';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Toast from 'primevue/toast';
import Dialog from "primevue/dialog";
import {deleteUser} from "@/services/UsuarioService.ts";

type UserRole = 'ADMIN' | 'USER' | 'PROFESOR';

interface User {
  guid: string;
  email: string;
  password: string;
  rol: string;
  numeroIdentificacion: string;
  nombre: string;
  apellidos: string;
  curso: string | null;
  tutor: string | null;
  fotoCarnet: string;
  avatar: string;
  isActivo: boolean;
  lastLoginDate: string,
  lastPasswordResetDate: string;
  createdDate: string;
  updatedDate: string;
}

interface PagedResponse {
  content: User[];
  totalElements: number;
}

export default {
  name: 'UsuariosDashboard',
  components: {DataTable, Column, Dialog, Toast },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as User[],
      todosLosDatos: [] as User[],
      totalRecords: 0,
      loading: false,
      currentPage: 0,
      pageSize: 5,
      showDeleteDialog: false,
      userToDelete: null as User | null,
    };
  },
  async mounted() {
    console.log("Componente montado. Llamando a obtenerDatos inicial...");
    await this.loadData();
    console.log("Datos iniciales y totalRecords cargados.");
  },
  methods: {
    getRolClass(rol: UserRole | undefined): string {
      if (!rol) return 'status-unknown';
      switch (rol) {
        case 'ADMIN': return 'status-admin';
        case 'PROFESOR': return 'status-profesor';
        case 'USER': return 'status-user';
        default: return 'status-unknown';
      }
    },
    async loadData() {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          this.loading = false;
          return;
        }

        const urlTotal = `https://loantechoficial.onrender.com/users?page=0&size=1`;
        const responseTotal = await axios.get<PagedResponse>(urlTotal, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.totalRecords = responseTotal.data.totalElements;

        const urlAll = `https://loantechoficial.onrender.com/users?page=0&size=${this.totalRecords}`;
        const responseAll = await axios.get<PagedResponse>(urlAll, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.todosLosDatos = responseAll.data.content;
        this.paginar();
        this.loading = false;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
        this.loading = false;
      }
    },
    paginar() {
      const resultadosFiltrados = this.filtrarPorTexto(this.search);
      this.totalRecords = resultadosFiltrados.length;
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = resultadosFiltrados.slice(inicio, fin);
    },
    handleSearchInput() {
      this.currentPage = 0;
      this.paginar();
    },
    onPage(event: any) {
      this.loading = true;
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.paginar();
      setTimeout(() => { this.loading = false; }, 100);
    },
    filtrarPorTexto(query: string): User[] {
      if (!query) {
        return this.todosLosDatos;
      }

      const q = query.toLowerCase();

      return this.todosLosDatos.filter(user => {
        const numeroIdentificacionMatch = user.numeroIdentificacion?.toLowerCase().startsWith(q) ?? false;

        const nombreMatch = user.nombre?.toLowerCase().startsWith(q) ?? false;

        const apellidosMatch = user.apellidos?.toLowerCase().startsWith(q) ?? false;

        const emailMatch = user.email?.toLowerCase().startsWith(q) ?? false;

        const cursoMatch = user.curso?.toLowerCase().startsWith(q) ?? false;

        const tutorMatch = user.tutor?.toLowerCase().startsWith(q) ?? false;

        const rolMatch = user.rol?.toLowerCase().startsWith(q) ?? false;

        return numeroIdentificacionMatch || nombreMatch || apellidosMatch || emailMatch || cursoMatch || tutorMatch || rolMatch;
      });
    },
    verUsuario(usuario: User) {
      console.log("Navegando a detalle de usuario con estos datos:", usuario);
      this.$router.push({
        name: 'UsuarioDetalle',
        params: { guid: usuario.guid }
      });
    },
    deleteUsuario(usuario: User) {
      if (!usuario.isActivo) {
        this.$toast.add({
          severity: 'warn',
          summary: 'Acción No Permitida',
          detail: 'Este usuario ya está inactivo y no puede ser eliminado nuevamente.',
          life: 3000
        });
        return;
      }
      this.userToDelete = usuario;
      this.showDeleteDialog = true;
      console.log(`Preparando para eliminar usuario con GUID: ${usuario.guid}. Mostrando diálogo.`);
    },
    async confirmDelete() {
      if (!this.userToDelete) {
        console.error("No user selected for deletion.");
        this.$toast.add({ severity: 'error', summary: 'Error', detail: 'No user selected for deletion.', life: 3000 });
        this.showDeleteDialog = false;
        return;
      }

      console.log(`Confirmado eliminar usuario con GUID: ${this.userToDelete.guid}`);
      try {
        await deleteUser(this.userToDelete.guid)

        const indexInAllData = this.todosLosDatos.findIndex(
            (u) => u.guid === this.userToDelete!.guid
        );

        if (indexInAllData !== -1) {
          const updatedUser = {
            ...this.todosLosDatos[indexInAllData],
            isActivo: false,
          };
          this.todosLosDatos.splice(indexInAllData, 1, updatedUser);
        }

        this.paginar();

        this.$toast.add({
          severity: 'success',
          summary: 'Eliminación Exitosa',
          detail: `El usuario con email ${this.userToDelete.email} ha sido desactivado.`,
          life: 3000
        });

      } catch (error: any) {
        console.error("Error eliminando usuario:", error);
        let errorMessage = 'Error al eliminar el usuario.';
        if (axios.isAxiosError(error) && error.response && error.response.data) {
          if (typeof error.response.data === 'string') {
            errorMessage = error.response.data;
          } else if (error.response.data && typeof error.response.data.message === 'string') {
            errorMessage = error.response.data.message;
          } else {
            errorMessage = 'Error desconocido al eliminar usuario.';
          }
        } else if (error instanceof Error) {
          errorMessage = error.message;
        }
        this.$toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      } finally {
        this.showDeleteDialog = false;
        this.userToDelete = null;
      }
    }
  }
};
</script>

<style scoped>
.usuarios-container {
  padding: 80px 30px 40px 30px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
  max-width: 300px;
  margin: -25px auto 40px 150px;
}

.filters label {
  font-weight: 500;
  color: var(--color-text-dark);
  white-space: nowrap;
}

.filters input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  min-width: 250%;
}

.filters input:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.table-wrapper {
  width: 110%;
  overflow-x: auto;
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  background-color: white;
  margin-left: -45px;
}

:deep(.p-datatable-custom .p-datatable-thead > tr > th) {
  font-family: 'Montserrat', sans-serif;
  background-color: var(--color-background-main);
  color: var(--color-primary);
  font-weight: 600;
  border-bottom: 2px solid var(--color-neutral-medium);
  text-align: left;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr) {
  font-family: 'Montserrat', sans-serif;
  color: var(--color-text-dark);
  transition: background-color 0.2s ease;
  border-bottom: 1px solid var(--color-background-main);
}

:deep(.p-datatable-custom .p-datatable-tbody > tr:last-child) {
  border-bottom: none;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr:hover) {
  background-color: var(--color-accent-soft) !important;
}

:deep(.p-datatable-custom .p-datatable-tbody > tr > td) {
  padding: 0.9rem 1rem;
  vertical-align: middle;
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

.status-admin {
  background-color: rgba(var(--color-error-rgb), 0.1);
  color: var(--color-error);
}

.status-profesor {
  background-color: rgba(var(--color-interactive-rgb), 0.15);
  color: var(--color-interactive-darker);
}

.status-user {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-unknown {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
}

.action-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  padding: 6px;
  transition: all 0.3s ease-in-out;
  line-height: 1;
  width: 32px;
  height: 32px;
  box-sizing: border-box;
}

.action-button i {
  font-size: 1.1rem;
  display: block;
}

.view-button {
  background-color: var(--color-interactive);
  color: white;
}

.view-button:hover {
  background-color: var(--color-interactive-darker);
  transform: scale(1.1);
}

.delete-button {
  background-color: var(--color-error);
  color: white;
}

.delete-button:hover {
  background-color: #B91C1C;
  transform: scale(1.1);
}

.action-button:active {
  transform: scale(0.95);
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

.action-button-dialog {
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

.action-button-dialog:active {
  transform: scale(0.98);
}

.action-button-dialog.secondary-button {
  background-color: transparent;
  color: var(--color-interactive);
  border: 1px solid var(--color-interactive);
}

.action-button-dialog.secondary-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.05);
}

.action-button-dialog.primary-button {
  background-color: var(--color-error);
  color: var(--color-text-on-dark-hover);
}

.action-button-dialog.primary-button:hover {
  background-color: #B91C1C;
  box-shadow: 0 2px 8px rgba(var(--color-interactive-rgb), 0.3);
}

.action-button-dialog.primary-button i {
  font-size: 1.1rem;
}

.disabled-button {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .usuarios-container {
    padding: 60px 15px 30px 15px;
  }

  .filters {
    flex-direction: column;
    align-items: stretch;
    margin: 0 auto 30px auto;
    max-width: 100%;
    gap: 5px;
  }

  .filters label {
    text-align: left;
    margin-bottom: 5px;
  }

  .filters input {
    width: 100%;
    min-width: auto;
  }

  .table-wrapper {
    width: 100%;
    margin-left: 0;
    border: 1px solid var(--color-neutral-medium);
    border-radius: 8px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 6px;
  }

  .action-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .action-button i {
    font-size: 0.95rem;
  }

  .status-badge {
    font-size: 0.75rem;
    padding: 2px 8px;
  }

  :deep(.p-paginator) {
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }

  :deep(.p-paginator .p-paginator-element) {
    font-size: 0.8rem;
    min-width: 28px;
    height: 28px;
  }
}
</style>