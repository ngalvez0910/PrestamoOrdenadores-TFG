<template>
  <MenuBar />
  <div class="filters">
    <div class="filter">
      <label for="username">Usuario:</label>
      <input id="username" type="text" v-model="searchUsername" placeholder="Buscar por usuario..." />
    </div>
    <div class="filter">
      <label for="grade">Curso:</label>
      <input id="grade" type="text" v-model="searchGrade" placeholder="Buscar por curso..." />
    </div>
    <div class="filter">
      <label for="role">Rol:</label>
      <select id="role" type="text" v-model="searchRole">
        <option value="">Todos</option>
        <option value="Admin">Administrador</option>
        <option value="Profesor">Profesor</option>
        <option value="Alumno">Alumno</option>
      </select>
    </div>
  </div>

  <div class="table">
    <DataTable stripedRows tableStyle="min-width: 50rem">
      <Column field="number" header="Nº"></Column>
      <Column field="guid" header="GUID"></Column>
      <Column field="username" header="Nombre de usuario"></Column>
      <Column field="grade" header="Curso"></Column>
      <Column field="role" header="Rol"></Column>
      <Column field="date" header="Fecha de alta"></Column>
      <Column field="incident" header="Incidencias"></Column>
      <Column field="edit"></Column>
    </DataTable>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: 'UsuariosDashboard',
  components: { MenuBar },
  data() {
    return {
      searchUsername: '',
      searchGrade: '',
      searchRole: '',
    };
  },
};
</script>

<style scoped>
.filters {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
  justify-content: center;
  padding: 1.5rem 1rem;
}

.filter {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 250px;
  width: 100%;
}

.filter label {
  font-weight: 600;
  color: #14124f;
  font-size: 0.95rem;
}

.filter input {
  padding: 0.75rem;
  border: 1px solid #d6621e;
  border-radius: 25px;
  font-size: 1rem;
  width: 100%;
  box-sizing: border-box;
}

.filter input::placeholder {
  color: #aaa;
}

.table {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.p-datatable {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.p-datatable th, .p-datatable td {
  text-align: center;
  padding: 1rem 1.5rem;
}

.p-datatable th {
  background-color: #14124f;
  color: white;
  font-weight: bold;
}

.p-datatable td {
  background-color: #f8f9fa;
}

.p-datatable td:hover {
  background-color: #e8e8e8;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .filter input {
    width: 100%;
  }

  .table {
    margin-top: 1rem;
    margin-left: 0;
  }
}
</style>
