<template>
  <MenuBar />
  <div class="detalle-container">
    <div class="detalle-header-actions">
      <button @click="goBack" class="back-button" title="Volver a Dispositivos">
        <i class="pi pi-arrow-left"></i>
      </button>
      <button class="action-button edit-button" @click="toggleEdit" title="Editar Dispositivo">
        <i :class="editable ? 'pi pi-times' : 'pi pi-pencil'"></i> {{ editable ? 'Cancelar' : 'Editar' }}
      </button>
    </div>

    <div class="usuario-details" v-if="userData">
      <div class="details-header">
        <h2>Detalles del Usuario</h2>
        <i class="pi pi-user header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label>Número Identificación</label>
          <div class="readonly-field">{{ userData.numeroIdentificacion || 'No especificado' }}</div>
        </div>

        <div class="form-group">
          <label>Nombre</label>
          <div class="readonly-field">{{ userData.nombre }}</div>
        </div>

        <div class="form-group">
          <label>Apellidos</label>
          <div class="readonly-field">{{ userData.apellidos }}</div>
        </div>

        <div class="form-group">
          <label>Email</label>
          <div class="readonly-field">{{ userData.email }}</div>
        </div>

        <div class="form-group">
          <label>Curso</label>
          <div class="readonly-field">{{ userData.curso || '-' }}</div>
        </div>

        <div class="form-group">
          <label>Tutor</label>
          <div class="readonly-field">{{ userData.tutor || '-' }}</div>
        </div>

        <div class="form-group">
          <label for="rol">Rol</label>
          <select v-if="editable" id="rol" class="input-field" v-model="userData.rol">
            <option value="ALUMNO">ALUMNO</option>
            <option value="PROFESOR">PROFESOR</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <div v-else>
                <span :class="['status-badge', getRolClass(userData.rol)]">
                    {{ userData.rol }}
                </span>
          </div>
        </div>

        <div class="form-group">
          <label>Último Login</label>
          <div class="readonly-field">{{ userData.lastLoginDate }}</div>
        </div>

        <div class="form-group">
          <label>Último Cambio Contraseña</label>
          <div class="readonly-field">{{ userData.lastPasswordResetDate }}</div>
        </div>

        <div class="form-group">
          <label>Fecha Creación</label>
          <div class="readonly-field">{{ userData.createdDate }}</div>
        </div>

        <div class="form-group">
          <label>Fecha Actualización</label>
          <div class="readonly-field">{{ userData.updatedDate }}</div>
        </div>
      </div>

      <div v-if="editable" class="update-button-wrapper">
        <button class="action-button update-button" @click="actualizarUsuario">
          Actualizar Usuario
        </button>
      </div>
    </div>

    <div v-else class="loading-message">
      <p>Cargando detalles del usuario...</p>
    </div>

  </div>

  <Toast />
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {actualizarUsuario, getUserByGuidAdmin} from "@/services/UsuarioService.ts";
import {useToast} from "primevue/usetoast";

type UserRole = 'ADMIN' | 'USER' | 'PROFESOR';

export default defineComponent({
  name: "UsuarioDetalle",
  components: {MenuBar},
  inheritAttrs: false,
  setup() {
    const toast = useToast();
    return { toast };
  },
  data() {
    return {
      userData: null as any,
      originalData: null as any,
      editable: false,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const user = await getUserByGuidAdmin(guidString);
      this.userData = user;
      this.originalData = JSON.parse(JSON.stringify(user));
      console.log(user)
    } catch (error) {
      console.error("Error al obtener los detalles del usuario:", error);
      this.toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar el usuario.', life: 3000 });
    }
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    toggleEdit() {
      this.editable = !this.editable;
      if (!this.editable && this.originalData) {
        this.userData = JSON.parse(JSON.stringify(this.originalData));
      }
    },
    getRolClass(rol: UserRole | undefined): string {
      if (!rol) return 'status-unknown';
      switch (rol) {
        case 'ADMIN': return 'status-admin';
        case 'PROFESOR': return 'status-profesor';
        case 'USER': return 'status-user';
        default: return 'status-unknown';
      }
    },
    async actualizarUsuario() {
      if (!this.userData || !this.originalData) {
        console.warn("Datos del usuario no disponibles para actualizar.");
        this.toast.add({ severity: 'warn', summary: 'Advertencia', detail: 'Datos no disponibles.', life: 3000 });
        return;
      }

      const hasChanged = this.userData.rol !== this.originalData.rol;

      if (hasChanged) {
        try {
          const updatePayload = {
            rol: this.userData.rol,
          };

          console.log("Actualizando usuario con payload:", updatePayload);

          const usuarioActualizado = await actualizarUsuario(this.userData.guid, updatePayload);

          if (usuarioActualizado) {
            this.originalData = JSON.parse(JSON.stringify(usuarioActualizado));
            this.userData = JSON.parse(JSON.stringify(usuarioActualizado));

            this.toast.add({ severity: 'success', summary: 'Éxito', detail: 'Usuario actualizado.', life: 3000 });
            this.editable = false;
          } else {
            throw new Error("La actualización no devolvió datos válidos.");
          }

        } catch (error: any) {
          console.error('Error al actualizar el usuario:', error);
          const errorMessage = error.response?.data?.message || error.message || 'No se pudo actualizar el usuario.';
          this.toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });
          if (this.originalData) {
            this.userData = JSON.parse(JSON.stringify(this.originalData));
          }
        }
      } else {
        console.log("No se detectaron cambios.");
        this.toast.add({ severity: 'info', summary: 'Info', detail: 'No se realizaron cambios.', life: 3000 });
        this.editable = false;
      }
    },
  }
})
</script>

<style scoped>
.detalle-container {
  padding: 80px 30px 40px 30px;
  max-width: 900px;
  margin: 0 auto;
  box-sizing: border-box;
}

.detalle-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  font-size: 1rem;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
  width: 40px;
  height: 40px;
  line-height: 1;
}

.back-button:hover {
  background-color: var(--color-interactive-darker);
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(var(--color-primary-rgb), 0.2);
}

.action-button {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.action-button:active {
  transform: scale(0.98);
}

.action-button i {
  font-size: 1rem;
}

.edit-button {
  background-color: var(--color-interactive);
  color: white;
}

.edit-button:hover {
  background-color: var(--color-interactive-darker);
}

.usuario-details {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  max-width: 850px;
  margin-left: auto;
  margin-right: auto;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--color-neutral-medium);
}
.details-header h2 {
  color: var(--color-primary);
  margin: 0;
  font-size: 1.6rem;
  font-weight: 600;
}
.header-icon {
  font-size: 2.5rem;
  color: var(--color-primary);
  opacity: 0.7;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px 25px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;

}
.form-group label {
  font-size: 0.85rem;
  color: var(--color-text-dark);
  font-weight: 500;
  text-transform: uppercase;
  opacity: 0.8;
}

.readonly-field {
  padding: 10px 12px;
  font-size: 1rem;
  line-height: 1.4;
  color: var(--color-text-dark);
  background-color: var(--color-background-main);
  border: 1px solid var(--color-neutral-medium);
  border-radius: 8px;
  min-height: calc(1.4em + 20px + 2px);
  word-wrap: break-word;
  white-space: pre-wrap;
  box-sizing: border-box;
  width: 100%;
}

input.input-field,
select.input-field {
  border-radius: 8px;
  padding: 10px 12px;
  border: 1px solid var(--color-neutral-medium);
  background-color: white;
  color: var(--color-text-dark);
  width: 100%;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  box-sizing: border-box;
  font-size: 1rem;
  line-height: 1.4;
}

select.input-field {
  cursor: pointer;
  appearance: none;
}

input.input-field[readonly] {
  background-color: var(--color-background-main);
  cursor: default;
  opacity: 0.9;
}

input.input-field[readonly]:focus {
  border-color: var(--color-neutral-medium);
  box-shadow: none;
}

input.input-field:not([readonly]):focus,
select.input-field:focus {
  border-color: var(--color-interactive);
  box-shadow: 0 0 0 3px rgba(var(--color-interactive-rgb), 0.2);
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: var(--color-text-dark);
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
  text-align: center;
  display: inline-block;
  text-transform: uppercase;
}

.status-admin {
  background-color: rgba(var(--color-error-rgb), 0.1);
  color: var(--color-error);
}

.status-profesor {
  background-color: rgba(var(--color-interactive-rgb), 0.15);
  color: var(--color-interactive-darker);
}

.status-user {
  background-color: rgba(var(--color-success-rgb), 0.15);
  color: var(--color-success);
}

.status-unknown {
  background-color: var(--color-neutral-medium);
  color: var(--color-text-dark);
}

.update-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
}

.update-button {
  background-color: var(--color-interactive);
  color: white;
}

.update-button:hover {
  background-color: var(--color-interactive-darker);
}

@media (max-width: 992px) {
  .details-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .detalle-container { padding: 70px 20px 30px 20px; max-width: 100%; }
  .details-grid {
    grid-template-columns: 1fr;
    gap: 25px;
  }
  .detalle-header-actions { margin-bottom: 20px; }
  .details-header h2 { font-size: 1.4rem; }
  .header-icon { font-size: 2rem; }
  .back-button { width: 36px; height: 36px; }
}

@media (max-width: 480px) {
  .usuario-details { padding: 20px; }
  .form-group label { font-size: 0.8rem; }
  .readonly-field { font-size: 0.95rem; padding: 8px 10px; }
  .details-header h2 { font-size: 1.2rem; }
}
</style>