<template>
  <MenuBar />
  <div class="filters">
    <p class="filter">
      Nombre de usuario:
      <input type="text" v-model="search" placeholder="Buscar por usuario..." />
    </p>
    <p class="filter">
      Curso:
      <input type="text" v-model="search" placeholder="Buscar por curso..." />
    </p>
    <p class="filter">
      Rol:
      <select>
        <option value="">Todos</option>
        <option value="Admin">Admin</option>
        <option value="Profesor">Profesor</option>
        <option value="Estudiante">Estudiante</option>
      </select>
    </p>
  </div>
  <br>
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
      search: '',
    };
  },
};
</script>

<style>
.filters {
  display: flex;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  margin-left: -25%;
  margin-top: -10%;
}

.filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter input, select {
  width: 200px;
  padding: 0.75rem;
  border: 1px solid #d6621e;
  border-radius: 25px;
}

.table {
  margin-left: -37%;
  margin-top: 1%;
}
</style>