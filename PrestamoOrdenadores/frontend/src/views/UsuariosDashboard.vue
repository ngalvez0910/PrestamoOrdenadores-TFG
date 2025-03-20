<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -30%;">
    <p class="filter col-5" style="margin-left: 2%;">
      Nombre:
      <input type="text" v-model="searchName" placeholder="Buscar por nombre..." />
    </p>
    <p class="filter col-4" style="margin-left: -8%;">
      Curso:
      <input type="text" v-model="searchCurso" placeholder="Buscar por curso..." />
    </p>
    <p class="filter col-3" style="margin-left: -2%;">
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
      <Column field="guid" header="GUID"></Column>
      <Column field="email" header="Email"></Column>
      <Column field="name" header="Nombre"></Column>
      <Column field="grade" header="Curso"></Column>
      <Column field="tutor" header="Tutor"></Column>
      <Column field="role" header="Rol"></Column>
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