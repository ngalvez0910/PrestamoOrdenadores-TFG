<template>
  <MenuBar />
  <div class="detalle-container"> <div class="detalle-header-actions">
    <button @click="goBack" class="back-button" title="Volver a Usuarios">
      <i class="pi pi-arrow-left"></i>
    </button>
    <div></div>
  </div>

    <div class="usuario-details" v-if="userData">
      <div class="details-header">
        <h2>Detalles del Usuario</h2>
        <i class="pi pi-user header-icon"></i>
      </div>

      <div class="details-grid">

        <div class="form-group">
          <label>Número de Identificación</label>
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
          <div class="readonly-field">{{ userData.curso || 'N/A' }}</div>
        </div>

        <div class="form-group">
          <label>Tutor</label>
          <div class="readonly-field">{{ userData.tutor || 'N/A' }}</div>
        </div>

        <div class="form-group">
          <label>Rol</label>
          <div class="readonly-field">{{ userData.rol }}</div>
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
          <label>Fecha de Creación</label>
          <div class="readonly-field">{{ userData.createdDate }}</div>
        </div>

        <div class="form-group">
          <label>Fecha de Actualización</label>
          <div class="readonly-field">{{ userData.updatedDate }}</div>
        </div>

      </div> </div> <div v-else class="loading-message">
      <p>Cargando detalles del usuario...</p>
    </div>

  </div> </template>

<script lang="ts">
import {defineComponent} from 'vue'
import MenuBar from "@/components/AdminMenuBar.vue";
import {getUserByGuidAdmin} from "@/services/UsuarioService.ts";

export default defineComponent({
  name: "UsuarioDetalle",
  components: {MenuBar},
  data() {
    return {
      userData: null as any,
    };
  },
  async mounted() {
    try {
      const guid = this.$route.params.guid;
      const guidString = Array.isArray(guid) ? guid[0] : guid;
      const user = await getUserByGuidAdmin(guidString);
      this.userData = user;
      console.log(user)
    } catch (error) {
      console.error("Error al obtener los detalles del usuario:", error);
    }
  },
  methods: {
    goBack() {
      this.$router.back();
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
  margin-bottom: 30px;
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
  margin-top: 3%;
  margin-left: 15%;
}

.back-button:hover {
  background-color: var(--color-interactive-darker);
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(var(--color-primary-rgb), 0.2);
}

.usuario-details {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.15);
  margin-left: 25%;
  min-width: 800px;
}

.details-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 30px; padding-bottom: 15px;
  border-bottom: 1px solid var(--color-neutral-medium);
}
.details-header h2 {
  color: var(--color-primary); margin: 0; font-size: 1.6rem; font-weight: 600;
}
.header-icon { font-size: 2.5rem; color: var(--color-primary); opacity: 0.7; }

.details-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px 25px;
}

.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label {
  font-size: 0.85rem; color: var(--color-text-dark); font-weight: 500;
  text-transform: uppercase; opacity: 0.8;
}

.readonly-field {
  padding: 10px 12px; font-size: 1rem; line-height: 1.4; color: var(--color-text-dark);
  background-color: var(--color-background-main); border: 1px solid var(--color-neutral-medium);
  border-radius: 8px; min-height: calc(1.4em + 20px + 2px); word-wrap: break-word;
  white-space: pre-wrap; box-sizing: border-box; width: 100%;
}

.loading-message { text-align: center; padding: 40px; color: var(--color-text-dark); }

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