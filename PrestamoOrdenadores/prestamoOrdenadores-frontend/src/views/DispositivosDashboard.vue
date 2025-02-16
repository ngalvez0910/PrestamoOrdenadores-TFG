<template>
  <MenuBar />
  <div class="table">
    <DataTable stripedRows tableStyle="min-width: 50rem">
      <Column field="id" header="ID"></Column>
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
  components: {MenuBar},
};
</script>

<style>
.table {
  display: flex;
}
</style>