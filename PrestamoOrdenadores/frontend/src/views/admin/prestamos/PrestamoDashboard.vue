<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
    Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="handleSearchInput" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 143%; height: 600px; overflow-y: auto;">
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
        <Column field="guid">
          <template #header>
            <b>GUID</b>
          </template>
        </Column>
        <Column field="user.guid"> <template #body="slotProps">
          {{ slotProps.data.user?.guid }} </template>
          <template #header>
            <b>Usuario</b>
          </template>
        </Column>
        <Column field="dispositivo.guid"> <template #body="slotProps">
          {{ slotProps.data.dispositivo?.guid }} </template>
          <template #header>
            <b>Dispositivo</b>
          </template>
        </Column>
        <Column field="estadoPrestamo">
          <template #body="slotProps">
            {{ formatEstado(slotProps.data.estadoPrestamo) }}
          </template>
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
        <Column field="fechaPrestamo">
          <template #header>
            <b>Fecha de préstamo</b>
          </template>
        </Column>
        <Column field="fechaDevolucion">
          <template #header>
            <b>Fecha de devolución</b>
          </template>
        </Column>
        <Column field="ver">
          <template #body="slotProps">
            <button @click="verPrestamo(slotProps.data)" class="verPrestamo-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete">
          <template #body="slotProps">
            <button @click="deletePrestamo(slotProps.data)" class="deletePrestamo-button">
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

interface Prestamo {
  guid: string;
  user: { guid: string } | null;
  userGuid: string;
  dispositivo: { guid: string } | null;
  dispositivoGuid: string;
  estadoPrestamo: string;
  estado: string;
  fechaPrestamo: string;
  fechaDevolucion: string;
  createdDate: string;
  updatedDate: string;
}

interface PagedResponse {
  content: Prestamo[];
  totalElements: number;
}

export default {
  name: 'PrestamoDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Prestamo[],
      todosLosDatos: [] as Prestamo[],
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
    formatEstado(estadoPrestamo: 'VENCIDO' | 'EN_CURSO' | 'CANCELADO'): string {
      return estadoPrestamo.replace(/_/g, ' ');
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

        const urlTotal = `http://localhost:8080/prestamos?page=0&size=1`;
        const responseTotal = await axios.get<PagedResponse>(urlTotal, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.totalRecords = responseTotal.data.totalElements;

        const urlAll = `http://localhost:8080/prestamos?page=0&size=${this.totalRecords}`;
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
    filtrarPorTexto(query: string) {
      if (!query) return this.todosLosDatos;

      const q = query.toLowerCase();
      return this.todosLosDatos.filter(prestamo =>
          prestamo.guid?.toLowerCase().includes(q) ||
          prestamo.dispositivoGuid?.toLowerCase().includes(q) ||
          prestamo.estado?.toLowerCase().includes(q) ||
          prestamo.userGuid?.toLowerCase().includes(q)
      );
    },
    handleSearchInput(event: Event) {
      const target = event.target as HTMLInputElement;
      this.search = target.value;
      this.currentPage = 0;

      const resultadosFiltrados = this.filtrarPorTexto(this.search);
      this.totalRecords = resultadosFiltrados.length;

      this.paginar();
    },
    paginar() {
      const inicio = this.currentPage * this.pageSize;
      const fin = inicio + this.pageSize;
      this.datos = this.filtrarPorTexto(this.search).slice(inicio, fin);
    },
    onPage(event: any) {
      this.currentPage = event.page;
      this.pageSize = event.rows;
      this.paginar();
    },
    verPrestamo(prestamo: Prestamo) {
      console.log("Navegando a detalle de prestamo con estos datos:", prestamo);
      this.$router.push({
        name: 'PrestamoDetalle',
        params: { guid: prestamo.guid }
      });
    }
  },
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

.verPrestamo-button {
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
  margin-left: -5%;
}

.verPrestamo-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.verPrestamo-button i {
  transform: scale(1.1);
}

.deletePrestamo-button {
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
  margin-left: -5%;
}

.deletePrestamo-button:hover {
  background-color: #9b1616;
}

.deletePrestamo-button i {
  margin-top: 1%;
  margin-left: 3%
}

.p-paginator-pages {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 5px;
}

.p-paginator-pages button {
  background-color: #a6a6a6;
  color: #ffffff;
  padding: 0.5rem 0.5rem;
  margin: 0 5px;
  border-radius: 80%;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  display: flex;
  justify-content: center;
  align-items: center;
}

.p-paginator-pages button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.p-paginator-pages .p-highlight {
  background-color: #d6621e !important;
  color: white;
  font-weight: bold;
  transform: scale(1.05);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
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