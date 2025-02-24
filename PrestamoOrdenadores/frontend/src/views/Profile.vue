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
      <li><a href="#">Mis préstamos</a></li>
      <li><a href="#">Mis incidencias</a></li>
      <li><a href="#">Notificaciones</a></li>
      <li><a href="#">Cambiar contraseña</a></li>
      <li><a @click="logout">Cerrar sesión</a></li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import axios from 'axios';
import router from "@/router";

export default defineComponent({
  nombre: "Profile",
  components: { AdminMenuBar },
  data() {
    return {
      nombre: '',
      email: '',
      curso: '',
      avatar: 'https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg',
    };
  },
  mounted() {
    this.fetchUserData();
  },
  methods: {
    async fetchUserData() {
      try {
        const response = await axios.get('/students/2f935f49-4088-4d35-8b5f-246e27ccd12e');
        this.nombre = response.data.nombre;
        this.email = response.data.email;
        this.avatar = response.data.avatar;
      } catch (error) {
        console.error('Error al obtener los datos del usuario:', error);
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

    logout() {
      console.log("Cerrando sesión...");
      router.push("/");
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
  padding: 5px 10px;
  cursor: pointer;
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
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.user-details button {
  background-color: #2196F3;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
}

.user-details button:hover {
  background-color: #1b82c7;
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

.side-menu ul {
  list-style-type: none;
  padding: 0;
}

.side-menu ul li {
  margin: 15px 0;
}

.side-menu ul li a {
  text-decoration: none;
  color: #14124f;
  display: block;
  padding: 10px;
  border-radius: 4px;
}

.side-menu ul li a:hover {
  background-color: #f0f0f0;
  cursor: pointer;
}
</style>