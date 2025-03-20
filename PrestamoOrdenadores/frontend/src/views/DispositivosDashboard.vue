<template>
  <MenuBar />
  <div class="filters row-12" style="margin-left: -33%;">
    <p class="filter col-4">
      Nº Serie:
      <input type="text" v-model="searchNumber" placeholder="Buscar por nº de serie..." />
    </p>
    <p class="filter col-4">
      Estado:
      <input type="text" v-model="searchEstado" placeholder="Buscar por estado..." />
    </p>
    <p class="filter col-4">
      Stock:
      <input type="text" v-model="searchStock" placeholder="Buscar por stock..." />
    </p>
  </div>
  <br>
  <div class="table row-12" style="margin-left: -20%;">
    <DataTable stripedRows tableStyle="min-width: 50rem">
      <Column field="guid" header="GUID"></Column>
      <Column field="serialNumber" header="Número de serie"></Column>
      <Column field="componentes" header="Componentes"></Column>
      <Column field="state" header="Estado"></Column>
      <Column field="incident" header="Incidencias"></Column>
      <Column field="stock" header="Stock"></Column>
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
      searchNumber: '',
      searchEstado: '',
      searchStock: '',
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
  margin-top: 1%;
}
</style>