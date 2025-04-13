<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
    Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="filterData" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 143%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
        <Column field="guid">
          <template #header>
            <b>GUID</b>
          </template>
        </Column>
        <Column field="userGuid">
          <template #header>
            <b>Usuario</b>
          </template>
        </Column>
        <Column field="dispositivoGuid">
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

export default {
  name: 'PrestamoDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Prestamo[],
      filteredDatos: [] as Prestamo[]
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    formatEstado(estadoPrestamo: 'VENCIDO' | 'EN_CURSO' | 'CANCELADO'): string {
      return estadoPrestamo.replace(/_/g, ' ');
    },
    async obtenerDatos() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const response = await axios.get(`http://localhost:8080/prestamos`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        console.log("Datos recibidos:", response.data);
        let prestamos = response.data.content || response.data;

        if (!Array.isArray(prestamos)) {
          prestamos = [prestamos];
        }

        this.datos = prestamos.map((prestamo: any) => {
          return {
            ...prestamo,
            user: prestamo.user ? { guid: prestamo.user.guid } : null,
            userGuid: prestamo.user ? prestamo.user.guid : null,
            dispositivo: prestamo.dispositivo ? { guid: prestamo.dispositivo.guid } : null,
            dispositivoGuid: prestamo.dispositivo ? prestamo.dispositivo.guid : null,
          };
        });

        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(prestamo => {
        const searchText = this.search.toLowerCase();
        const guid = (prestamo.guid || "").toLowerCase();
        const dispositivoGuid = (prestamo.dispositivoGuid || "").toLowerCase();
        const estado = (prestamo.estado || '').toLowerCase();
        const userGuid = (prestamo.userGuid || "").toLowerCase();

        return (
            guid.includes(searchText) ||
            dispositivoGuid.includes(searchText) ||
            estado.includes(searchText) ||
            userGuid.includes(searchText)
        );
      });
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
</style>