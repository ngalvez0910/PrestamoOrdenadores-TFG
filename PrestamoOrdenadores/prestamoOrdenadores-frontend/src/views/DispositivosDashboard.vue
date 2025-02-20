<template>
  <MenuBar />
  <div class="filters">
    <div class="filter">
      <label for="name">Nombre:</label>
      <input id="name" type="text" v-model="searchName" placeholder="Buscar por nombre..." />
    </div>
    <div class="filter">
      <label for="state">Estado:</label>
      <input id="state" type="text" v-model="searchState" placeholder="Buscar por estado..." />
    </div>
    <div class="filter">
      <label for="user">Usuario:</label>
      <input id="user" type="text" v-model="searchUser" placeholder="Buscar por usuario..." />
    </div>
  </div>

  <div class="table">
    <DataTable stripedRows tableStyle="min-width: 50rem;">
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
      searchName: '',
      searchState: '',
      searchUser: '',
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
