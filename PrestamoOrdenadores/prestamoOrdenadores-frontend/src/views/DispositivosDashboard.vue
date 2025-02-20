<template>
  <MenuBar />
  <div class="filters">
    <p class="filter">
      Nombre:
      <input type="text" v-model="search" placeholder="Buscar por nombre..." />
    </p>
    <p class="filter">
      Estado:
      <input type="text" v-model="search" placeholder="Buscar por estado..." />
    </p>
    <p class="filter">
      Usuario:
      <input type="text" v-model="search" placeholder="Buscar por usuario..." />
    </p>
  </div>
  <br>
  <div class="table">
    <DataTable stripedRows tableStyle="min-width: 50rem">
      <Column field="number" header="Nº"></Column>
      <Column field="guid" header="GUID"></Column>
      <Column field="serialNumber" header="Número de serie"></Column>
      <Column field="name" header="Nombre"></Column>
      <Column field="state" header="Estado"></Column>
      <Column field="user" header="Usuario"></Column>
      <Column field="incident" header="Incidencias"></Column>
      <Column field="edit"></Column>
    </DataTable>
  </div>
</template>

<script lang="ts">
import MenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: 'DispositivosDashboard',
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
  margin-left: -20%;
  margin-top: -25%;
}

.filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter input {
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