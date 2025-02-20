<template>
  <MenuBar />
  <div class="dashboard">
    <router-link to="/admin/dashboard/dispositivos" class="dashboard-box">
      <i class="pi pi-desktop icon"></i>
      <p>Ver dispositivos</p>
    </router-link>

    <router-link to="/admin/dashboard/usuarios" class="dashboard-box">
      <i class="pi pi-users icon"></i>
      <p>Ver usuarios</p>
    </router-link>

    <router-link to="/admin/dashboard/incidencias" class="dashboard-box">
      <i class="pi pi-exclamation-triangle icon"></i>
      <p>Ver incidencias</p>
    </router-link>

    <div class="dashboard-box">
      <i class="pi pi-file icon"></i>
      <p>Ver informes</p>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-box icon"></i>
      <p>Ver stock</p>
    </div>
  </div>
</template>

<script lang="ts">
import AdminMenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: "Dashboard",
  components: { MenuBar: AdminMenuBar },
};
</script>

<style>
.dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  justify-content: center;
  align-items: center;
  margin: 20px auto;
  max-width: 900px;
  padding: 20px;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 6px 12px rgba(20, 18, 79, 0.15);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-align: center;
  cursor: pointer;
  width: 100%;
  max-width: 220px;
  text-decoration: none;
  color: #14124f;
}

.dashboard-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(20, 18, 79, 0.25);
  background-color: #f8f9fa;
}

.icon {
  font-size: 4.5rem;
  margin-bottom: 10px;
  color: #14124f;
}

.dashboard-box p {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0;
}

@media (max-width: 768px) {
  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 15px;
  }

  .dashboard-box {
    max-width: 180px;
  }

  .icon {
    font-size: 4rem;
  }

  .dashboard-box p {
    font-size: 1rem;
  }
}
</style>