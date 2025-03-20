<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -35%;">
    <div class="filter col-3" style="margin-left: 2%;">
      <label for="username">Usuario:</label>
      <input id="username" type="text" v-model="searchUsername" placeholder="Buscar por usuario..." />
    </div>
    <div class="filter col-3" style="margin-left: -4%;">
      <label for="asunto">Asunto:</label>
      <input id="asunto" type="text" v-model="searchAsunto" placeholder="Buscar por asunto..." />
    </div>
    <p class="filter col-3" style="margin-left: -5%; margin-top: 1%">
      Fecha:
      <input type="date" v-model="searchDate" />
    </p>
    <p class="filter col-3" style="margin-left: -5%; margin-top: 1%">
      <label for="state">Estado:</label>
      <select id="state" type="text" v-model="searchState">
        <option value="">Todos</option>
        <option value="Pendiente">Pendiente</option>
        <option value="Resuelto">Resuelto</option>
      </select>
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -25%;">
    <DataTable stripedRows tableStyle="min-width: 50rem">
      <Column field="guid" header="GUID"></Column>
      <Column field="asunto" header="Asunto"></Column>
      <Column field="state" header="Estado"></Column>
      <Column field="user" header="Usuario"></Column>
      <Column field="date" header="Fecha de incidencia"></Column>
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
      searchAsunto: '',
      searchDate: '',
      searchState: '',
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
  align-items: center;
  margin-top: -15%;
}

.filter {
  display: flex;
  align-items: center;
}

.filter input, select {
  width: 200px;
  padding: 0.75rem;
  border: 1px solid #d6621e;
  border-radius: 25px;
}

.table {
  margin-top: 1%;
  width: 100%;
}
</style>