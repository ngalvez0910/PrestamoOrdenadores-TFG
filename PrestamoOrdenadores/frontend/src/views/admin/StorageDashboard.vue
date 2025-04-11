<template>
  <MenuBar />
  <div class="dashboard">
    <div class="dashboard-box">
      <i class="pi pi-desktop icon"></i>
      <p>Dispositivos</p>
      <button class="botonCsv" @click="descargarCsvDispositivos">CSV</button>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-users icon"></i>
      <p>Usuarios</p>
      <button class="botonCsv" @click="descargarCsvUsers">CSV</button>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-exclamation-triangle icon"></i>
      <p>Incidencias</p>
      <button class="botonCsv">CSV</button>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-file icon"></i>
      <p>Préstamos</p>
      <button class="botonCsv">CSV</button>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-ban icon"></i>
      <p>Sanciones</p>
      <button class="botonCsv">CSV</button>
    </div>
  </div>
</template>

<script lang="ts">
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import { descargarCsvDispositivos } from "@/services/DispositivoService";
import {descargarCsvUsers} from "@/services/UsuarioService.ts";

export default {
  name: "StorageDashboard",
  components: { MenuBar: AdminMenuBar },
  methods: {
    async descargarCsvDispositivos() {
      try {
        await descargarCsvDispositivos();
      } catch (error) {
        console.error('Error al descargar el CSV en el componente', error);
      }
    },
    async descargarCsvUsers() {
      try {
        await descargarCsvUsers();
      } catch (error) {
        console.error('Error al descargar el CSV en el componente', error);
      }
    },
  },
};
</script>

<style>
.dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  justify-content: center;
  align-items: center;
  margin-top: 10%;
  max-width: 900px;
  padding: 20px;
  margin-left: -25%;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
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
  cursor: default;
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

.botonCsv{
  margin-top: 7%;
  margin-left: 3%;
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