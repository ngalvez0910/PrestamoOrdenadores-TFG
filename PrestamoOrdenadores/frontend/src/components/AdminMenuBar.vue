<template>
  <div class="menubar">
    <div class="brand-and-nav">
      <h1><a :href="getHomeRoute()">LoanTech</a></h1>
      <nav class="main-nav">
        <a :href="getHomeRoute()">Inicio</a>
        <a href="/admin/dashboard/prestamos">Préstamos</a>
        <a href="/admin/dashboard/dispositivos">Dispositivos</a>
        <a href="/admin/dashboard/usuarios">Usuarios</a>
        <a href="/admin/dashboard/incidencias">Incidencias</a>
        <a href="/admin/dashboard/sanciones">Sanciones</a>
        <a href="/admin/dashboard/storage">Almacenamiento</a>
      </nav>
    </div>

    <Button class="user-info" @click="toggleMenu($event)" text>
      <p class="username">{{ username || "Usuario" }}</p>
      <Avatar :image="avatarUrl || 'https://placehold.co/400'" shape="circle" class="avatar" />
    </Button>

    <Menu ref="menu" class="user-menu" :model="items" :popup="true">
      <template #item="{ item, props }">
        <a :href="item.url" class="menu-item-link" v-bind="props.action" @click="item.command">
          <i v-if="item.icon" :class="item.icon + ' menu-item-icon'"></i>
          <span class="menu-item-label">{{ item.label }}</span>
        </a>
      </template>
    </Menu>
  </div>
</template>

<script lang="ts">
import Avatar from "primevue/avatar";
import Menu from "primevue/menu";
import Button from "primevue/button";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

export default {
  name: "MenuBar",
  components: {
    Avatar,
    Menu,
    Button
  },
  data() {
    return {
      items: [
        {
          label: 'Perfil',
          icon: 'pi pi-user',
          command: () => this.goToProfile()
        },
        {
          separator: true
        },
        {
          label: 'Cerrar sesión',
          icon: 'pi pi-sign-out',
          command: () => this.logout()
        }
      ],
      username: "",
      avatarUrl: "",
      rol: "",
    };
  },
  setup() {
    const menu = ref();
    const router = useRouter();

    const toggleMenu = (event: Event) => {
      menu.value?.toggle(event);
    };

    const goToProfile = () => {
      router.push("/profile");
    };

    const logout = () => {
      console.log("Cerrando sesión...");
      localStorage.removeItem("token");
      router.push("/");
    };

    return { menu, toggleMenu, goToProfile, logout  };
  },
  mounted() {
    this.loadUserData();
  },
  methods: {
    async loadUserData() {
      const token = localStorage.getItem("token");
      if (token) {
        try {
          const decodedToken: any = jwtDecode(token);
          const email = decodedToken.sub;
          this.rol = decodedToken.rol;

          const response = await axios.get(`http://localhost:8080/users/email/${email}`, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          });

          const userData = response.data;

          this.username = userData.nombre || "Usuario";
          this.avatarUrl = userData.avatarUrl || "https://placehold.co/400";
        } catch (error) {
          console.error("Error al obtener la información del usuario:", error);
        }
      }
    },
    getHomeRoute(): string {
      if (this.rol === "ADMIN") {
        return "/admin/dashboard";
      } else {
        return "/profile";
      }
    },
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=Inconsolata:wght@200..900&family=Montserrat:ital,wght@0,100..900;1,100..900&family=NTR&family=Quicksand&display=swap');

:root {
  --color-primary: #14124f;
  --color-interactive: #4A90E2;
  --color-accent-soft: #AEC6F4;
  --color-background-main: #F8F9FA;
  --color-text-dark: #495057;
  --color-neutral-medium: #CED4DA;
  --color-text-on-dark: #E0E7FF;
  --color-text-on-dark-hover: #FFFFFF;
}

body {
  font-family: 'Montserrat', sans-serif;
  margin: 0;
}

.menubar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--color-primary);
  color: var(--color-text-on-dark);
  padding: 10px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  box-sizing: border-box;
}

.brand-and-nav {
  display: flex;
  align-items: center;
}

.menubar h1 {
  margin-right: 40px;
}

.menubar h1 a {
  color: var(--color-text-on-dark);
  text-decoration: none;
  font-size: 1.8rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: var(--color-text-on-dark-hover);
}

.main-nav {
  display: flex;
  gap: 25px;
}

.main-nav a {
  color: var(--color-text-on-dark);
  text-decoration: none;
  font-size: 1rem;
  padding: 5px 0;
  position: relative;
  transition: color 0.3s ease;
}

.main-nav a:hover {
  color: var(--color-text-on-dark-hover);
}

.main-nav a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--color-interactive);
  transition: width 0.3s ease;
}

.main-nav a:hover::after {
  width: 100%;
}

.user-info.p-button {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  background-color: var(--color-background-main) !important;
  color: var(--color-text-dark) !important;
  border: 1px solid transparent !important;
  padding: 5px 10px !important;
  border-radius: 20px;
  transition: border-color 0.3s ease, background-color 0.3s ease;
  height: auto;
  margin-left: -25%;
  width: 15%;
}

.user-info.p-button:hover,
.user-info.p-button:focus {
  border-color: var(--color-interactive) !important;
  background-color: var(--color-text-on-dark) !important;
  box-shadow: none !important;
}

.username {
  font-size: 0.95rem;
  font-weight: 500;
  margin: 0;
}

.avatar {
  width: 32px;
  height: 32px;
  border: 2px solid var(--color-interactive);
  transition: transform 0.2s ease-in-out;
  vertical-align: middle;
}

.user-info:hover .avatar {
  transform: scale(1.1);
}

:global(.p-menu.user-menu) {
  background-color: var(--color-background-main) !important;
  border: 1px solid var(--color-neutral-medium) !important;
  border-radius: 6px !important;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1) !important;
  padding: 5px 0 !important;
  min-width: 150px;
}

.menu-item-link {
  display: flex !important;
  align-items: center !important;
  padding: 10px 15px !important;
  color: var(--color-text-dark) !important;
  text-decoration: none !important;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-item-icon {
  margin-right: 10px !important;
  color: var(--color-text-dark);
}

.menu-item-label {
  flex-grow: 1;
}

.menu-item-link:hover {
  background-color: var(--color-accent-soft) !important;
  color: var(--color-primary) !important;
}

.menu-item-link:hover .menu-item-icon {
  color: var(--color-primary) !important;
}

.menu-separator {
  height: 1px;
  background-color: var(--color-neutral-medium);
  margin: 5px 0;
}

@media (max-width: 992px) {
  .main-nav {
    display: none;
  }
  .menubar {
    padding: 10px 15px;
  }
}

@media (max-width: 768px) {
  .menubar h1 a {
    font-size: 1.5rem;
  }

  .username {
    display: none;
  }

  .user-info.p-button {
    padding: 5px !important;
    border-radius: 50%;
    gap: 0;
  }
}
</style>