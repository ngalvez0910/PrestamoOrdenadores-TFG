<template>
  <MenuBar />
  <div class="dashboard">
    <router-link to="/admin/dashboard/dispositivos" class="dashboard-box">
      <i class="pi pi-desktop" style="font-size: 5rem"></i>
      <p>Ver dispositivos</p>
    </router-link>

    <div class="dashboard-box">
      <i class="pi pi-users" style="font-size: 5rem"></i>
      <p>Ver usuarios</p>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-exclamation-triangle" style="font-size: 5rem"></i>
      <p>Ver incidencias</p>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-file" style="font-size: 5rem"></i>
      <p>Ver informes</p>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-box" style="font-size: 5rem"></i>
      <p>Ver stock</p>
    </div>
  </div>
</template>

<script lang="ts">
import AdminMenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: 'Dashboard',
  components: {MenuBar: AdminMenuBar},
};
</script>

<style>
.dashboard {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  justify-content: center;
  align-items: center;
  margin-left: -15%;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(20, 18, 79, 0.3);
  width: 200px;
  box-sizing: border-box;
  color: inherit;
  text-decoration: none;
}

.dashboard-box:nth-child(4),
.dashboard-box:nth-child(5) {
  grid-column: span 1;
  justify-self: center;
  margin-left: 100%
}

.dashboard-box:hover {
  color: inherit !important;
  background-color: inherit !important;
}

</style>