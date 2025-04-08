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
        <Column field="email" header="Email"></Column>
        <Column header="Nombre">
          <template #body="slotProps">
            <span>{{ slotProps.data.nombre }} {{ slotProps.data.apellidos }}</span>
          </template>
        </Column>
        <Column field="curso" header="Curso"></Column>
        <Column field="tutor" header="Tutor"></Column>
        <Column field="rol" header="Rol"></Column>
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

export default {
  name: 'UsuariosDashboard',
  components: { MenuBar },
  emits: ['input-change'],
  data() {
    return {
      search: '',
      datos: [] as User[],
      filteredDatos: [] as User[]
    };
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    async obtenerDatos() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error("No se encontró el token de autenticación.");
          return;
        }

        const response = await axios.get(`http://localhost:8080/users`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        let users = response.data.content || response.data;

        if (!Array.isArray(users)) {
          users = [users];
        }

        this.datos = users.map((user: any) => {
          return {
            ...user
          };
        });

        console.log("Datos recibidos:", response.data);
        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(user => {
        const searchText = this.search.toLowerCase();
        const email = (user.email || "").toLowerCase();
        const nombre = (user.nombre || "").toLowerCase();
        const curso = (user.curso || "").toLowerCase();
        const tutor = (user.tutor || "").toLowerCase();
        const rol = (user.rol || "").toLowerCase();

        return (
            email.includes(searchText) ||
            nombre.includes(searchText) ||
            curso.includes(searchText) ||
            tutor.includes(searchText) ||
            rol.includes(searchText)
        );
      });
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
</style>