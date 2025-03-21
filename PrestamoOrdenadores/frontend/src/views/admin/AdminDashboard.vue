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
  margin-top: 20px;
  max-width: 900px;
  padding: 20px;
  margin-left: -25%;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 6px 12px rgba(20, 18, 79, 0.15);
  text-align: center;
  cursor: pointer;
  width: 100%;
  max-width: 220px;
  text-decoration: none;
  color: #14124f;
  transition: transform 0.2s ease, color 0.3s ease;
}

.dashboard-box:hover {
  transform: translateY(-10px);
  background-color: #f8f9fa;
  box-shadow: 0 4px 6px rgba(239, 139, 85, 0.19);
}

.dashboard-box:hover .icon, .dashboard-box:hover p {
  color: #a14916;
}

.dashboard-box .icon, .dashboard-box p {
  transition: color 0.3s ease;
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

.dashboard .dashboard-box:nth-last-child(2),
.dashboard .dashboard-box:nth-last-child(1) {
  margin-left: 55%;
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