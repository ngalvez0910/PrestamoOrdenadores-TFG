<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -40%;">
    <p class="filter col-5">
      Nombre de usuario:
      <input type="text" v-model="searchName" placeholder="Buscar por usuario..." />
    </p>
    <p class="filter col-4">
      Curso:
      <input type="text" v-model="searchCurso" placeholder="Buscar por curso..." />
    </p>
    <p class="filter col-3">
      Rol:
      <select v-model="searchRol">
        <option value="">Todos</option>
        <option value="Admin">Admin</option>
        <option value="Profesor">Profesor</option>
        <option value="Estudiante">Estudiante</option>
      </select>
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -20%;">
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
      searchName: '',
      searchCurso: '',
      searchRol: '',
    };
  },
};
</script>

<style>
.filter input, select {
  width: 200px;
  border: 1px solid #d6621e;
  border-radius: 25px;
}
</style>