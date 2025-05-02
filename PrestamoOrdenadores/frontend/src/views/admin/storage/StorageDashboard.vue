<template>
  <MenuBar />
  <div class="dashboard">
    <div class="dashboard-box-storage">
      <i class="pi pi-desktop icon"></i>
      <p>Dispositivos</p>
      <button class="botonCsv" @click="descargarCsvDispositivos">CSV</button>
    </div>

    <div class="dashboard-box-storage">
      <i class="pi pi-users icon"></i>
      <p>Usuarios</p>
      <button class="botonCsv" @click="descargarCsvUsers">CSV</button>
    </div>

    <div class="dashboard-box-storage">
      <i class="pi pi-exclamation-triangle icon"></i>
      <p>Incidencias</p>
      <button class="botonCsv" @click="descargarCsvIncidencias">CSV</button>
    </div>

    <div class="dashboard-box-storage">
      <i class="pi pi-file icon"></i>
      <p>Préstamos</p>
      <button class="botonCsv" @click="descargarCsvPrestamos">CSV</button>
    </div>

    <div class="dashboard-box-storage">
      <i class="pi pi-ban icon"></i>
      <p>Sanciones</p>
      <button class="botonCsv" @click="descargarCsvSanciones">CSV</button>
    </div>

    <div class="dashboard-box-storage">
      <i class="pi pi-database icon"></i>
      <p>Copia de Seguridad</p>
      <button class="botonZipExportar" @click="crearCopiaSeguridad">Exportar</button>
      <button class="botonZipImportar" @click="listarYMostrarBackups">Importar</button>
    </div>

    <div v-if="mostrarModalRestauracion" class="modal-restauracion">
      <button class="modal-close-button" @click="mostrarModalRestauracion = false">&times;</button>

      <h2>Selecciona un backup para restaurar:</h2>
      <ul v-if="backupsDisponibles && backupsDisponibles.length > 0" class="backup-list">
        <li v-for="backup in backupsDisponibles" :key="backup.fileName" class="backup-item">
        <span class="backup-details">
          <span class="backup-name">{{ backup.fileName }}</span>
          <span class="backup-date">{{ backup.date ? formatDate(backup.date) : 'Fecha no disponible' }}</span>
        </span>
          <button class="boton-restaurar-item" @click="restoreBackup(backup.fileName)" title="Restaurar este backup">
            &#x2713; </button>
        </li>
      </ul>
      <p v-else class="no-backups-message">No hay backups disponibles.</p>
    </div>
  </div>
</template>

<script lang="ts">
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import { descargarCsvDispositivos } from "@/services/DispositivoService.ts";
import {descargarCsvUsers} from "@/services/UsuarioService.ts";
import {descargarCsvIncidencias} from "@/services/IncidenciaService.ts";
import {descargarCsvPrestamos} from "@/services/PrestamoService.ts";
import {descargarCsvSanciones} from "@/services/SancionService.ts";
import { createBackup, listBackups, restoreBackup, type BackupInfo } from "@/services/StorageService.ts";

export default {
  name: "StorageDashboard",
  components: { MenuBar: AdminMenuBar },
  data() {
    return {
      backupsDisponibles: [] as BackupInfo[],
      mostrarModalRestauracion: false,
    };
  },
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
    async descargarCsvIncidencias() {
      try {
        await descargarCsvIncidencias();
      } catch (error) {
        console.error('Error al descargar el CSV en el componente', error);
      }
    },
    async descargarCsvPrestamos() {
      try {
        await descargarCsvPrestamos();
      } catch (error) {
        console.error('Error al descargar el CSV en el componente', error);
      }
    },
    async descargarCsvSanciones() {
      try {
        await descargarCsvSanciones();
      } catch (error) {
        console.error('Error al descargar el CSV en el componente', error);
      }
    },
    async crearCopiaSeguridad() {
      try {
        const successMessage = await createBackup();
        alert(successMessage);
      } catch (error) {
        console.error('COMPONENT: Error en handleCreateBackup:', error);
        alert(error instanceof Error ? error.message : 'Ocurrió un error desconocido al crear el backup.');
      }
    },
    async listarYMostrarBackups() {
      this.backupsDisponibles = [];
      try {
        const backups = await listBackups();
        this.backupsDisponibles = backups;
        if (this.backupsDisponibles.length > 0) {
          this.mostrarModalRestauracion = true;
        } else {
          alert('No hay copias de seguridad disponibles para restaurar.');
        }
      } catch (error) {
        console.error('COMPONENT: Error en handleListAndShowBackups:', error);
        alert(error instanceof Error ? error.message : 'Ocurrió un error desconocido al listar los backups.');
        this.mostrarModalRestauracion = false;
      }
    },
    async restoreBackup(fileName: string) {
      try {
        const successMessage = await restoreBackup(fileName);
        alert(successMessage);
        this.mostrarModalRestauracion = false;
      } catch (error) {
        console.error('COMPONENT: Error en handleRestoreBackup:', error);
        alert(error instanceof Error ? error.message : `Ocurrió un error desconocido al restaurar ${fileName}.`);
      }
    },
    formatDate(dateInput: string | number | Date): string {
      if (!dateInput) return '';

      let date: Date | null = null;

      if (typeof dateInput === 'number') {
        date = new Date(dateInput);
      } else if (dateInput instanceof Date) {
        date = dateInput;
      } else if (typeof dateInput === 'string') {
        const standardParsedDate = new Date(dateInput);
        if (!isNaN(standardParsedDate.getTime())) {
          date = standardParsedDate;
        } else {
          const monthMap: { [key: string]: number } = {
            'Jan': 0, 'Feb': 1, 'Mar': 2, 'Apr': 3, 'May': 4, 'Jun': 5,
            'Jul': 6, 'Aug': 7, 'Sep': 8, 'Oct': 9, 'Nov': 10, 'Dec': 11
          };
          const regex = /^(\w{3})\s(\w{3})\s(\d{1,2})\s(\d{2}):(\d{2}):(\d{2})\s\w+\s(\d{4})$/;
          const match = dateInput.match(regex);

          if (match) {
            try {
              const monthAbbr = match[2];
              const day = parseInt(match[3], 10);
              const hour = parseInt(match[4], 10);
              const minute = parseInt(match[5], 10);
              const second = parseInt(match[6], 10);
              const year = parseInt(match[7], 10);
              const monthIndex = monthMap[monthAbbr]; // Obtiene 0 para Jan, 1 para Feb, etc.

              if (monthIndex !== undefined && !isNaN(day) && !isNaN(hour) && !isNaN(minute) && !isNaN(second) && !isNaN(year)) {
                date = new Date(year, monthIndex, day, hour, minute, second);
              }
            } catch (parseError) {
              console.error('Error en parseo manual de fecha:', parseError);
            }
          }
        }
      }

      if (!date || isNaN(date.getTime())) {
        console.warn('Fecha inválida o imposible de parsear:', dateInput);
        return 'Fecha inválida';
      }

      try {
        const options: Intl.DateTimeFormatOptions = {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        };
        return date.toLocaleString('es-ES', options);
      } catch (formatError) {
        console.error('Error formateando fecha:', date, formatError);
        return 'Error de formato';
      }
    }
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

.dashboard-box-storage {
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
  max-width: 270px;
  height: 100%;
  text-decoration: none;
  color: #14124f;
  transition: transform 0.2s ease, color 0.3s ease;
}

.dashboard-box-storage:hover {
  transform: translateY(-10px);
  background-color: #f8f9fa;
  box-shadow: 0 4px 6px rgba(239, 139, 85, 0.19);
  cursor: default;
}

.dashboard-box-storage:hover .icon, .dashboard-box-storage:hover p {
  color: #a14916;
}

.dashboard-box-storage .icon, .dashboard-box-storage p {
  transition: color 0.3s ease;
}

.icon {
  font-size: 4.5rem;
  margin-bottom: 10px;
  color: #14124f;
}

.dashboard-box-storage p {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0;
}

.botonCsv{
  margin-top: 7%;
  margin-left: 3%;
}

.botonZipImportar, .botonZipExportar{
  margin-left: 3%;
}

.modal-restauracion {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 25px 30px 30px 30px;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  min-width: 450px;
  max-width: 700px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.modal-close-button {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  font-size: 2rem;
  color: #14124f;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 5%;
  transition: color 0.2s ease;
}

.modal-close-button:hover {
  background-color: inherit;
  color: #14124f;
}

.modal-restauracion h2 {
  margin-top: 0;
  margin-bottom: 25px;
  padding-right: 20px;
  color: #14124f;
  text-align: center;
  font-size: 1.4rem;
}

.backup-list {
  list-style: none;
  padding: 0;
  margin: 0 0 10px 0;
  overflow-y: auto;
  flex-grow: 1;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.backup-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 5px;
}
.backup-item + .backup-item {
  border-top: 1px solid #f0f0f0;
}

.backup-details {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  margin-right: 15px;
  overflow: hidden;
}

.backup-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 3px;
}

.backup-date {
  font-size: 0.8rem;
  color: #666;
  white-space: nowrap;
}

.boton-restaurar-item {
  flex-shrink: 0;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  font-size: 1.1rem;
  line-height: 1;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.boton-restaurar-item:hover {
  background-color: #218838;
  transform: scale(1.1);
}
.boton-restaurar-item:active {
  transform: scale(1);
}

.no-backups-message {
  text-align: center;
  color: #666;
  margin: 30px 0;
  flex-grow: 1;
}

@media (max-width: 768px) {
  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 15px;
  }

  .dashboard-box-storage {
    max-width: 180px;
  }

  .icon {
    font-size: 4rem;
  }

  .dashboard-box-storage p {
    font-size: 1rem;
  }
}
</style>