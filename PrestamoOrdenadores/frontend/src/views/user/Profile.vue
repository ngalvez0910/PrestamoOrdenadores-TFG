<template>
  <AdminMenuBar/>
  <div class="page-container profile-page profile-page-tabs-internal">

    <div class="profile-card">
      <div class="profile-card-header">
        <h2>Mi Perfil</h2>
        <i class="pi pi-user header-icon"></i>
      </div>

      <div class="profile-tab-content">
        <div class="profile-card-body">
          <div class="avatar-section">
            <img :src="avatar" alt="Avatar" class="avatar-image" />
            <button @click="changeAvatar" class="action-button avatar-button">
              <i class="pi pi-camera"></i> Cambiar Avatar
            </button>
          </div>

          <div class="user-info-section">
            <div class="form-group">
              <label for="nombre">Nombre</label>
              <div id="nombre" class="readonly-field">{{ nombre || '-' }}</div>
            </div>
            <div class="form-group">
              <label for="email">Correo electrónico</label>
              <div id="email" class="readonly-field">{{ email || '-' }}</div>
            </div>
            <div class="form-group">
              <label for="curso">Curso</label>
              <div id="curso" class="readonly-field">{{ curso || '-' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-card-footer">
        <button @click="goToChangePassword" class="action-button change-password-button">
          <i class="pi pi-key"></i> Cambiar Contraseña
        </button>
        <button @click="logout" class="action-button logout-button-tabs">
          <i class="pi pi-sign-out"></i> Cerrar sesión
        </button>
      </div>
    </div>
  </div>
  <Toast />
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';
import {useRouter} from "vue-router";
import {jwtDecode} from "jwt-decode";
import {useToast} from "primevue/usetoast";

interface UserData {
  nombre: string;
  email: string;
  curso: string;
  avatar?: string;
}

export default defineComponent({
  nombre: "Profile",
  components: { AdminMenuBar },
  data() {
    return {
      nombre: '',
      email: '',
      curso: '',
      avatar: 'https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg',
      loading: true,
      activeTab: 'info',
    };
  },
  setup: function () {
    const router = useRouter();
    const toast = useToast();
    return {router, toast};
  },
  mounted() {
    this.obtenerDatos();
  },
  methods: {
    async obtenerDatos() {
      const token = localStorage.getItem("token");
      if (token) {
        try {
          const decodedToken = jwtDecode(token);
          const userEmail = decodedToken.sub;

          let apiUrl = `http://localhost:8080/users/email/${userEmail}`;

          const response = await axios.get<UserData>(apiUrl, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          this.nombre = response.data.nombre;
          this.email = response.data.email;
          this.curso = response.data.curso;
          if (response.data.avatar) {
            this.avatar = response.data.avatar;
          }
        } catch (error) {
          console.error("Error al obtener los datos del usuario:", error);
        }
      }
    },
    async saveProfile() {
      try {
        const updatedUser = {
          nombre: this.nombre,
          email: this.email,
        };

        const response = await axios.put('/students/2f935f49-4088-4d35-8b5f-246e27ccd12e', updatedUser);
        console.log('Perfil actualizado con éxito:', response.data);
      } catch (error) {
        console.error('Error al guardar el perfil:', error);
      }
    },
    logout() {
      console.log("Cerrando sesión...");
      localStorage.removeItem("token");
      this.$router.push("/");
    },
    goToChangePassword() {
      this.$router.push('/cambioContrasena');
    },
    changeAvatar() {
      console.log('Cambiar avatar');
    },
  },
});
</script>

<style scoped>
.page-container.profile-page {
  padding: 80px 30px 40px 30px;
  max-width: 900px;
  margin: 0 auto;
  box-sizing: border-box;
}

.profile-card {
  background-color: white;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 6px 20px rgba(var(--color-primary-rgb), 0.1);
  width: 140%;
  box-sizing: border-box;
  margin-left: 33%;
}

.profile-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--color-neutral-medium);
}

.profile-card-header h2 {
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

.profile-card-body {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 30px;
  align-items: flex-start;
  padding-top: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-image {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  object-fit: cover;
  border: 3px solid var(--color-neutral-medium);
}

.user-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  min-height: 44px;
  height: 44px;
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
  word-wrap: break-word;
}

.profile-card-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-neutral-medium);
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.action-button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease, transform 0.1s ease;
  text-decoration: none;
  line-height: 1.2;
}

.action-button:active {
  transform: scale(0.98);
}

.change-password-button {
  background-color: var(--color-interactive);
  color: white;
  margin-right: 43%;
}

.change-password-button:hover {
  background-color: var(--color-interactive-darker);
}

.logout-button-tabs {
  background-color: var(--color-error);
  color: white;
}

.logout-button-tabs:hover {
  background-color: var(--color-error);
}

.avatar-button {
  background-color: var(--color-interactive);
  color: white;
  width: auto;
}
.avatar-button:hover {
  background-color: var(--color-interactive-darker);
}

@media (max-width: 992px) {
  .profile-card-body {
    grid-template-columns: 1fr;
    text-align: center;
  }
  .avatar-section {
    align-items: center;
  }
  .user-info-section {
    text-align: left;
  }
  .profile-tabs-container.internal-tabs ul {
    flex-wrap: wrap;
  }
  .profile-tabs-container.internal-tabs .tab-link {
    padding: 8px 12px;
    font-size: 0.9rem;
  }
}
@media (max-width: 768px) {
  .page-container.profile-page {
    padding: 70px 20px 30px 20px;
  }
  .profile-card-header h2 {
    font-size: 1.4rem;
  }
  .header-icon {
    font-size: 2rem;
  }
  .profile-card-footer {
    flex-direction: column;
    gap: 10px;
  }
  .action-button {
    width: 100%;
    justify-content: center;
  }
}
</style>