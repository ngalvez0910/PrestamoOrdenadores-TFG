<template>
  <MenuBar />
  <div class="dashboard">
    <router-link to="/admin/dashboard/prestamos" class="dashboard-box">
      <i class="pi pi-arrow-right-arrow-left icon"></i>
      <p>Ver préstamos</p>
    </router-link>

    <router-link to="/admin/dashboard/dispositivos" class="dashboard-box">
      <i class="pi pi-desktop icon"></i>
      <p>Ver dispositivos</p>
    </router-link>

    <router-link to="/admin/dashboard/usuarios" class="dashboard-box">
      <i class="pi pi-users icon"></i>
      <p>Ver usuarios</p>
    </router-link>

    <router-link to="/admin/dashboard/incidencias" class="dashboard-box">
      <i class="pi pi-flag-fill icon"></i>
      <p>Ver incidencias</p>
    </router-link>

    <router-link to="/admin/dashboard/storage" class="dashboard-box">
      <i class="pi pi-file icon"></i>
      <p>Ver informes</p>
    </router-link>

    <router-link to="/admin/dashboard/sanciones" class="dashboard-box">
      <i class="pi pi-ban icon"></i>
      <p>Ver sanciones</p>
    </router-link>
  </div>
</template>

<script lang="ts">
import AdminMenuBar from "@/components/AdminMenuBar.vue";

export default {
  name: "Dashboard",
  components: { MenuBar: AdminMenuBar },
};
</script>

<style scoped>
.dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 25px;
  max-width: 1000px;
  margin-left: 25%;
  margin-top: 5%;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  text-align: center;
  cursor: pointer;
  text-decoration: none;
  color: var(--color-primary);
  min-height: 160px;
  transition: transform 0.25s ease, box-shadow 0.25s ease, background-color 0.25s ease, color 0.25s ease;
  overflow: hidden;
}

.dashboard-box:hover {
  transform: translateY(-8px);
  background-color: var(--color-background-main);
  color: var(--color-interactive);
  box-shadow: 0 8px 16px rgba(var(--color-primary-rgb), 0.12);
}

.icon {
  font-size: 4rem;
  margin-bottom: 15px;
  transition: color 0.25s ease;
}

.dashboard-box p {
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
  line-height: 1.3;
  transition: color 0.25s ease;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 70px 15px 30px 15px;
  }

  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 15px;
  }

  .dashboard-box {
    padding: 20px 15px;
    min-height: 140px;
  }

  .icon {
    font-size: 3.5rem;
    margin-bottom: 10px;
  }

  .dashboard-box p {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  }
  .dashboard-box {
    min-height: 120px;
  }
  .icon {
    font-size: 3rem;
  }
}
</style>