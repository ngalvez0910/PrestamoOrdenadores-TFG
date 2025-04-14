<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
    Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="handleSearchInput" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 150%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable
          :value="datos"
          paginator
          :rows="5"
          :totalRecords="totalRecords"
          :lazy="true"
          @page="onPage"
          paginatorClass="custom-paginator"
      >
        <Column field="email">
          <template #header>
            <b>Email</b>
          </template>
        </Column>
        <Column>
          <template #body="slotProps">
            <span>{{ slotProps.data.nombre }} {{ slotProps.data.apellidos }}</span>
          </template>
          <template #header>
            <b>Nombre</b>
          </template>
        </Column>
        <Column field="curso">
          <template #header>
            <b>Curso</b>
          </template>
        </Column>
        <Column field="tutor">
          <template #header>
            <b>Tutor</b>
          </template>
        </Column>
        <Column field="rol">
          <template #header>
            <b>Rol</b>
          </template>
        </Column>
        <Column field="ver">
          <template #body="slotProps">
            <button @click="verUsuario(slotProps.data)" class="ver-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete">
          <template #body="slotProps">
            <button @click="deleteUsuario(slotProps.data)" class="delete-button">
              <i class="pi pi-ban"></i>
            </button>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

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
  components: { MenuBar, DataTable, Column },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as User[],
      totalRecords: 0,
      loading: false,
      currentPage: 0,
      pageSize: 5,
      filters: {}
    };
  },
  async mounted() {
    console.log("Componente montado. Llamando a obtenerDatos inicial...");
    await this.loadData();
    console.log("Datos iniciales y totalRecords cargados.");
  },
  methods: {
    async loadData(page: number = this.currentPage, size: number = this.pageSize, query: string = '') {
      this.loading = true;
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          this.loading = false;
          return;
        }

        let url = `http://localhost:8080/users?page=${page}&size=${size}`;
        if (query) {
          url += `&search=${query}`; // Assuming your API supports a 'search' parameter
        }

        console.log("Llamando a la API:", url);

        const response = await axios.get<PagedResponse>(url, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("Respuesta de la API:", response.data);

        this.datos = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.loading = false;
        console.log("Datos actualizados en el componente:", this.datos);
        console.log("Total de registros:", this.totalRecords);

      } catch (error) {
        console.error("Error obteniendo datos:", error);
        this.loading = false;
      }
    },
    onPage(event: any) {
      console.log("Evento de paginación:", event);
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.loadData(this.currentPage, this.pageSize, this.search);
    },
    handleSearchInput(event: Event) {
      const target = event.target as HTMLInputElement;
      this.search = target.value;
      this.filterData(this.search);
    },
    filterData(query: string) {
      console.log("Filtrando datos con:", query);
      this.currentPage = 0;
      this.loadData(this.currentPage, this.pageSize, query);
    },
    verUsuario(usuario: User) {
      console.log("Navegando a detalle de usuario con estos datos:", usuario);
      this.$router.push({
        name: 'UsuarioDetalle',
        params: { guid: usuario.guid }
      });
    }
  }
};
</script>

<style>
.filters {
  position: relative;
  width: 100%;
  padding: 1rem;
  display: flex;
  align-items: center;
}

.filters input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #d1d3e2;
  border-radius: 25px;
  margin-left: 2%;
  transition: border 0.3s ease;
  outline: none;
}

.filters input:focus {
  border-color: #d6621e;
}

.ver-button {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
}

.ver-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.ver-button i {
  transform: scale(1.1);
}

.delete-button {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: -2%;
}

.delete-button:hover {
  background-color: #9b1616;
}

.delete-button i {
  margin-top: 2%;
  margin-left: 5%
}

a {
  background-color: inherit !important;
}

.custom-paginator {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

.p-paginator-pages button {
  background-color: #d6621e;
  color: #ffffff;
  padding: 0.5rem 0.5rem;
  margin: 0 5px;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.p-paginator-pages button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.p-paginator-pages .p-highlight {
  background-color: #d6621e;
  color: white;
}

.p-paginator-current {
  font-size: 0.9rem;
  color: #555;
  margin: 0 10px;
}

.p-paginator-first,
.p-paginator-prev,
.p-paginator-next,
.p-paginator-last {
  background-color: #d6621e;
  color: #ffffff;
  border-radius: 40px;
  padding: 0.5rem 0.75rem;
  margin: 0 5px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  max-width: 2%
}

.p-paginator-first:hover,
.p-paginator-prev:hover,
.p-paginator-next:hover,
.p-paginator-last:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>