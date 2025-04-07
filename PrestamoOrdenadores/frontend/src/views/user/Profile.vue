<template>
  <AdminMenuBar/>
  <div class="profile-container">
    <div class="avatar">
      <img :src="avatar" alt="Avatar" />
      <button @click="changeAvatar">Cambiar Avatar</button>
    </div>
    <div class="user-details">
      <div>
        <label for="nombre">Nombre</label>
        <input readonly type="text" id="nombre" v-model="nombre"/>
      </div>
      <div>
        <label for="email">Correo electrónico</label>
        <input readonly type="email" id="email" v-model="email"/>
      </div>
      <div>
        <label for="curso">Curso</label>
        <input readonly type="text" id="curso" v-model="curso"/>
      </div>
    </div>
  </div>
  <div class="side-menu">
    <ul>
      <li class="side-menu-item">
        <a href="/prestamo/me" class="side-menu-link">
          <i class="pi pi-briefcase mr-2"></i> Mis préstamos
        </a>
      </li>
      <li class="side-menu-item">
        <a href="#" class="side-menu-link">
          <i class="pi pi-exclamation-triangle mr-2"></i> Mis incidencias
        </a>
      </li>
      <li class="side-menu-item">
        <a href="#" class="side-menu-link">
          <i class="pi pi-bell mr-2"></i> Notificaciones
        </a>
      </li>
      <li class="side-menu-item">
        <a href="/cambioContrasena" class="side-menu-link">
          <i class="pi pi-key mr-2"></i> Cambiar contraseña
        </a>
      </li>
      <li class="side-menu-item">
        <button @click="logout" class="logout-button">Cerrar sesión</button>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';
import {useRouter} from "vue-router";
import {jwtDecode} from "jwt-decode";

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
      datos: null
    };
  },
  setup() {
    const router = useRouter();

    const logout = () => {
      console.log("Cerrando sesión...");
      localStorage.removeItem("token");
      router.push("/");
    };

    return { logout };
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

    changeAvatar() {
      console.log('Cambiar avatar');
    },
  },
});
</script>

<style scoped>
.profile-container {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 20px;
  padding: 20px;
  margin-top: 30%;
  width: 100%;
}

.avatar {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  margin-right: 20px;
  margin-left: -50%;
  margin-top: -50%;
  width: 200px;
}

.avatar img {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-bottom: 40px;
}

.avatar button {
  background-color: #d6621e;
  color: white;
  border: none;
  padding: 5px;
  cursor: pointer;
  width: 100%;
}

.avatar button:hover {
  background-color: #a14916;
}

.user-details {
  padding-left: 20px;
  margin-left: 40%;
  margin-top: -50%;
}

.user-details label {
  display: block;
  margin-bottom: 5px;
}

.user-details input {
  border-radius: 30px;
  padding: 8px;
  border: 1px solid #d1d3e2;
  width: 100%;
  margin-bottom: 18px;
  transition: border 0.3s ease;
  outline: none;
}

.user-details input:focus {
  border-color: #d6621e;
}

.side-menu {
  width: 250px;
  position: fixed;
  right: 5%;
  top: 20%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  z-index: 10;
}

.side-menu-item {
  text-decoration: none;
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
  margin-left: -15%
}

.side-menu-item a {
  text-decoration: none;
  color: #14124f;
}

.side-menu-item a:hover {
  color: #d6621e;
  padding-left: 22px;
}

.side-menu-item i,
.logout-button i {
  margin-right: 0.5rem;
}

.logout-button {
  background-color: #d61e1e;
  color: white;
  border: none;
  padding: 5px;
  border-radius: 30px;
  width: 100%;
  text-align: center;
  transition: all 0.3s ease-in-out;
  margin-left: 1%
}

.logout-button:hover {
  background-color: #9b1616;
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgb(214, 30, 30);
}
</style>