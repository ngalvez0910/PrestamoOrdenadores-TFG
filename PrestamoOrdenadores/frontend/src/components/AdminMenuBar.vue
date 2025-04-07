<template>
  <div class="menubar">
    <h1><a :href="getHomeRoute()">LoanTech</a></h1>
    <Button class="user-info" @click="toggleMenu($event)">
      <p class="username">{{ username || "Usuario" }}</p>
      <Avatar :image="avatarUrl || 'https://placehold.co/400'" shape="circle" class="avatar" />
    </Button>

    <Menu ref="menu" class="user-menu" :model="items" :popup="true">
      <template #item="{ item, props }">
        <a class="flex items-center" v-bind="props.action">
          <i v-if="item.icon" :class="item.icon + ' mr-2'"></i>
          <span>{{ item.label }}</span>
          <span v-if="item.shortcut" class="ml-auto border border-surface rounded bg-emphasis text-muted-color text-xs p-1">{{ item.shortcut }}</span>
          <i v-if="item.items && item.items.length > 0" :class="['pi pi-angle-down ml-auto', { 'pi-angle-down': item.root, 'pi-angle-right': !item.root }]"></i>
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
.menubar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #14124f;
  color: #fff;
  padding: 10px 30px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.menubar h1 a {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: inherit !important;
  background-color: inherit !important;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  width: 15%;
  height: 40px;
  margin-top: 2%;
  background: white;
  color: #14124f;
  transition: all 0.3s ease;
  border:none;
}

.user-info:hover {
  border: 1px solid #ec9160;
  box-shadow: 0 4px 8px rgb(236, 145, 96);
  transition: all 0.3s ease;
}

.username {
  font-size: 1rem;
  font-weight: 500;
  margin-left: 5%;
  margin-top: 10%;
}

.avatar {
  border: 2px solid #ec9160;
  transition: transform 0.2s ease-in-out;
}

.avatar:hover {
  transform: scale(1.1);
}

.user-menu {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgb(236, 145, 96);
  padding: 10px 0;
  min-width: 130px;
  overflow: visible !important;
}

.user-menu a i, .user-menu a {
  color: #14124f !important;
  margin-right: 8px;
  margin-left: -5%;
}

.user-menu a i:hover, .user-menu a:hover {
  color: #14124f !important;
  background-color: inherit !important;
}

@media (max-width: 768px) {
  .menubar {
    padding: 12px 20px;
  }

  .menubar h1 a {
    font-size: 1.3rem;
  }

  .username {
    display: none;
  }
}
</style>
