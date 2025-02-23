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
    <p class="filter">
      Fecha:
      <input type="date" v-model="searchDate" />
    </p>
    <p class="filter">
      <label for="type">Tipo:</label>
      <select id="type" type="text" v-model="searchType">
        <option value="">Todos</option>
        <option value="Advertencia">Advertencia</option>
        <option value="Temporal">Bloqueo Temporal</option>
        <option value="Indefinido">Bloqueo Indefinido</option>
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
      <Column field="type" header="Tipo"></Column>
      <Column field="date" header="Fecha de incidencia"></Column>
      <Column field="state" header="Estado"></Column>
      <Column field="edit"></Column>
    </DataTable>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: 'IncidenciasDashboard',
  components: { MenuBar },
  data() {
    return {
      searchUsername: '',
      searchGrade: '',
      searchDate: '',
      searchType: '',
    };
  },
};
</script>

<style>
body{
  overflow-x: hidden;
}

.filters {
  display: flex;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  margin-left: -25%;
  margin-top: -15%;
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
  margin-left: -25%;
  margin-top: 1%;
  width: 100%;
}
</style>