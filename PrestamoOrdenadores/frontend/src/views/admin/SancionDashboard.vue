<template>
  <MenuBar />
  <div class="filters" style="margin-left: -20%; margin-top: 35%">
    Buscar:
    <input type="text" v-model="search" placeholder="Buscar..." @input="filterData" />
  </div>
  <br>
  <div style="margin-left: -40%; margin-top: 2%; width: 150%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
        <Column field="guid" header="GUID"></Column>
        <Column field="userGuid" header="Usuario"></Column>
        <Column field="tipoSancion" header="Tipo">
          <template #body="slotProps">
            {{ formatTipoSancion(slotProps.data.tipoSancion) }}
          </template>
        </Column>
        <Column field="fechaSancion" header="Fecha de sanción"></Column>
        <Column field="ver">
          <template #body="slotProps">
            <button @click="verSancion(slotProps.data)" class="verSancion-button">
              <i class="pi pi-eye"></i>
            </button>
          </template>
        </Column>
        <Column field="delete">
          <template #body="slotProps">
            <button @click="deleteSancion(slotProps.data)" class="deleteSancion-button">
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

interface Sancion {
  guid: string;
  tipoSancion: string;
  tipo: string;
  user: { guid: string } | null;
  userGuid: string;
  fechaSancion: string;
  createdDate: string;
  updatedDate: string;
}

export default {
  name: 'SancionDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as Sancion[],
      filteredDatos: [] as Sancion[]
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    formatTipoSancion(tipoSancion: 'ADVERTENCIA' | 'BLOQUEO_TEMPORAL' | 'INDEFINIDO'): string {
      return tipoSancion.replace(/_/g, ' ');
    },
    async obtenerDatos() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const response = await axios.get(`http://localhost:8080/sanciones`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        console.log("Datos recibidos:", response.data);
        let sanciones = response.data.content || response.data;

        if (!Array.isArray(sanciones)) {
          sanciones = [sanciones];
        }

        this.datos = sanciones.map((sancion: any) => {
          return {
            ...sancion,
            user: sancion.user ? { guid: sancion.user.guid } : null,
            userGuid: sancion.user ? sancion.user.guid : null,
          };
        });

        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(sancion => {
        const searchText = this.search.toLowerCase();
        const guid = (sancion.guid || "").toLowerCase();
        const tipo = (sancion.tipoSancion || '').toLowerCase();
        const userGuid = (sancion.userGuid || "").toLowerCase();

        return (
            guid.includes(searchText) ||
            tipo.includes(searchText) ||
            userGuid.includes(searchText)
        );
      });
    },
    verSancion(sancion: Sancion) {
      console.log("Navegando a detalle de sancion con estos datos:", sancion);
      this.$router.push({
        name: 'SancionDetalle',
        params: { guid: sancion.guid }
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

.verSancion-button {
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

.verSancion-button:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}

.verSancion-button i {
  transform: scale(1.1);
}

.deleteSancion-button {
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

.deleteSancion-button:hover {
  background-color: #9b1616;
}

.deleteSancion-button i {
  margin-top: 1%;
  margin-left: 3%
}
</style>