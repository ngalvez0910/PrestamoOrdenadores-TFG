<template>
  <MenuBar />
  <div class="storage-container"> <div class="dashboard"> <div class="dashboard-box"> <i class="pi pi-desktop icon"></i>
    <p>Dispositivos</p>
    <div class="box-action-wrapper">
      <button class="box-action-button" @click="descargarCsvDispositivos">Descargar CSV</button>
    </div>
  </div>

    <div class="dashboard-box">
      <i class="pi pi-users icon"></i>
      <p>Usuarios</p>
      <div class="box-action-wrapper">
        <button class="box-action-button" @click="descargarCsvUsers">Descargar CSV</button>
      </div>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-flag-fill icon"></i>
      <p>Incidencias</p>
      <div class="box-action-wrapper">
        <button class="box-action-button" @click="descargarCsvIncidencias">Descargar CSV</button>
      </div>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-arrow-right-arrow-left icon"></i>
      <p>Préstamos</p>
      <div class="box-action-wrapper">
        <button class="box-action-button" @click="descargarCsvPrestamos">Descargar CSV</button>
      </div>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-ban icon"></i>
      <p>Sanciones</p>
      <div class="box-action-wrapper">
        <button class="box-action-button" @click="descargarCsvSanciones">Descargar CSV</button>
      </div>
    </div>

    <div class="dashboard-box">
      <i class="pi pi-database icon"></i>
      <p>Copia de Seguridad</p>
      <div class="box-action-wrapper multiple-buttons">
        <button class="box-action-button" @click="crearCopiaSeguridad">Exportar</button>
        <button class="box-action-button" @click="listarYMostrarBackups">Importar</button>
      </div>
    </div>

  </div> <div v-if="mostrarModalRestauracion" class="modal-overlay">
    <div class="modal-restauracion">
      <button class="modal-close-button" @click="mostrarModalRestauracion = false" title="Cerrar">&times;</button>
      <h2>Selecciona un backup para restaurar</h2>
      <div class="backup-list-wrapper">
        <ul v-if="backupsDisponibles && backupsDisponibles.length > 0" class="backup-list">
          <li v-for="backup in backupsDisponibles" :key="backup.fileName" class="backup-item">
                  <span class="backup-details">
                    <span class="backup-name" :title="backup.fileName">{{ backup.fileName }}</span>
                    <span class="backup-date">{{ backup.date ? formatDate(backup.date) : 'Fecha no disponible' }}</span>
                  </span>
            <button class="boton-restaurar-item" @click="restoreBackup(backup.fileName)" title="Restaurar este backup">
              <i class="pi pi-history"></i>
            </button>
          </li>
        </ul>
        <p v-else class="no-backups-message">No hay backups disponibles.</p>
      </div>
    </div>
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

<style scoped>
.storage-container {
  padding: 80px 30px 40px 30px;
  max-width: 1000px;
  margin: 0 auto;
  box-sizing: border-box;
}

.dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 230px));
  gap: 25px;
  max-width: 1000px;
  margin-left: 100px;
  margin-right: auto;
}

.dashboard-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 30px 20px 20px 20px;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  text-align: center;
  text-decoration: none;
  color: var(--color-primary);
  min-height: 200px;
  transition: transform 0.25s ease, box-shadow 0.25s ease, background-color 0.25s ease, color 0.25s ease;
  overflow: hidden;
  max-width: 230px;
  width: 100%;
  box-sizing: border-box;
}

.dashboard-box:hover {
  transform: translateY(-8px);
  background-color: var(--color-background-main);
  box-shadow: 0 8px 16px rgba(var(--color-primary-rgb), 0.12);
}

.icon {
  font-size: 4rem;
  margin-bottom: 15px;
  transition: color 0.25s ease;
  color: var(--color-primary);
}

.dashboard-box p {
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
  line-height: 1.3;
  transition: color 0.25s ease;
  color: var(--color-primary);
}

.box-action-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: auto;
}

.box-action-wrapper.multiple-buttons {
  gap: 10px;
}

.box-action-button {
  padding: 8px 16px;
  border: 1px solid var(--color-interactive);
  background-color: white;
  color: var(--color-interactive);
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.1s ease;
  line-height: 1.2;
}

.box-action-button:hover {
  background-color: var(--color-interactive);
  color: white;
  border-color: var(--color-interactive);
}

.box-action-button:active {
  transform: scale(0.97);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-restauracion {
  position: relative;
  background-color: white;
  padding: 25px 30px 30px 30px;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(var(--color-primary-rgb), 0.2);
  z-index: 1000;
  width: 90%;
  min-width: 300px;
  max-width: 600px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--color-neutral-medium);
}

.modal-close-button {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  font-size: 1.8rem;
  color: var(--color-neutral-medium);
  cursor: pointer;
  line-height: 1;
  padding: 5px;
  transition: color 0.2s ease;
}

.modal-close-button:hover {
  color: var(--color-text-dark);
}

.modal-restauracion h2 {
  font-family: 'Montserrat', sans-serif;
  margin-top: 0;
  margin-bottom: 20px;
  padding-right: 30px;
  color: var(--color-primary);
  text-align: center;
  font-size: 1.4rem;
  font-weight: 600;
}

.backup-list-wrapper {
  flex-grow: 1;
  overflow-y: auto;
  border-top: 1px solid var(--color-neutral-medium);
  border-bottom: 1px solid var(--color-neutral-medium);
  margin-bottom: 15px;
}

.backup-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.backup-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 10px;
}

.backup-item + .backup-item {
  border-top: 1px solid var(--color-background-main);
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
  color: var(--color-text-dark);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.backup-date {
  font-size: 0.8rem;
  color: #6c757d;
  white-space: nowrap;
}

.boton-restaurar-item {
  flex-shrink: 0;
  background-color: var(--color-success);
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
  background-color: #146C43;
  transform: scale(1.1);
}

.boton-restaurar-item:active {
  transform: scale(1);
}

.no-backups-message {
  text-align: center;
  color: var(--color-text-dark);
  opacity: 0.7;
  padding: 40px 20px;
  flex-grow: 1;
}

@media (max-width: 768px) {
  .storage-container {
    padding: 70px 15px 30px 15px;
  }

  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 20px;
  }

  .dashboard-box {
    min-height: 180px;
    padding: 20px 15px 15px 15px;
  }

  .icon {
    font-size: 3.5rem;
    margin-bottom: 10px;
  }

  .dashboard-box p {
    font-size: 0.9rem;
    margin-bottom: 10px;
  }

  .box-action-button {
    font-size: 0.8rem;
    padding: 6px 12px;
  }

  .box-action-wrapper.multiple-buttons {
    gap: 8px;
  }

  .modal-restauracion {
    width: 95%;
  }

  .modal-restauracion h2 {
    font-size: 1.2rem;
  }
}

@media (max-width: 480px) {
  .dashboard {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }

  .dashboard-box {
    min-height: 170px;
  }

  .icon {
    font-size: 3rem;
  }
}
</style>