<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -30%; margin-top: 25%">
    <p class="filter col-5" style="margin-left: 2%;">
      Nombre:
      <input type="text" v-model="searchName" placeholder="Buscar por nombre..." @input="filterData"/>
    </p>
    <p class="filter col-4" style="margin-left: -8%;">
      Curso:
      <input type="text" v-model="searchCurso" placeholder="Buscar por curso..." @input="filterData"/>
    </p>
    <p class="filter col-3" style="margin-left: -2%;">
      Rol:
      <select v-model="searchRol" @input="filterData">
        <option value="">Todos</option>
        <option value="Admin">Admin</option>
        <option value="Profesor">Profesor</option>
        <option value="Estudiante">Estudiante</option>
      </select>
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -20%;">
    <DataTable :value="filteredDatos" stripedRows tableStyle="min-width: 50rem">
      <Column field="guid" header="GUID"></Column>
      <Column field="email" header="Email"></Column>
      <Column field="nombre" header="Nombre"></Column>
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
      searchName: '',
      searchCurso: '',
      searchRol: '',
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
        const response = await axios.get(`http://localhost:8080/users`);
        console.log("Datos recibidos:", response.data);
        this.datos = response.data.content || response.data;
        this.filteredDatos = this.datos;
      } catch (error) {
        console.error("Error obteniendo datos:", error);
      }
    },
    filterData() {
      this.filteredDatos = this.datos.filter(usuario => {
        return (
            (this.searchName === '' || usuario.nombre.toLowerCase().includes(this.searchName.toLowerCase())) &&
            (this.searchCurso === '' || (usuario.curso && usuario.curso.toLowerCase().includes(this.searchCurso.toLowerCase()))) &&
            (this.searchRol === '' || usuario.rol.toString().includes(this.searchRol))
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
.filter input, select {
  width: 200px;
  border: 1px solid #d6621e;
  border-radius: 25px;
}

.ver-button {
  padding: 0.5rem 0.8rem;
  font-size: 0.875rem;
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20%
}

.ver-button:hover {
  background-color: #a14916;
}

.ver-button i {
  pointer-events: none;
  margin-top: 30%;
}

.delete-button {
  padding: 0.5rem 0.8rem;
  font-size: 0.875rem;
  background-color: #d61e1e;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20%
}

.delete-button:hover {
  background-color: #9b1616;
}

.delete-button i {
  pointer-events: none;
  margin-top: 30%;
}
</style>