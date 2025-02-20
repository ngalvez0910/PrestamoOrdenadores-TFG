<template>
  <div class="menubar">
    <h1><a href="/admin/dashboard">LoanTech</a></h1>
    <div class="user-info" @click="toggleMenu($event)">
      <p class="username">Usuario</p>
      <Avatar image="https://placehold.co/400" shape="circle" class="avatar" />
    </div>

    <OverlayPanel ref="menu" class="user-menu">
      <ul>
        <li @click="goToProfile"><i class="pi pi-user"></i> Perfil</li>
        <li @click="goToSettings"><i class="pi pi-cog"></i> Configuración</li>
        <li @click="logout"><i class="pi pi-sign-out"></i> Cerrar sesión</li>
      </ul>
    </OverlayPanel>
  </div>
</template>

<script lang="ts">
import Avatar from "primevue/avatar";
import OverlayPanel from "primevue/overlaypanel";
import { ref } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "MenuBar",
  components: {
    Avatar,
    OverlayPanel,
  },
  setup() {
    const menu = ref<InstanceType<typeof OverlayPanel> | null>(null);
    const router = useRouter();

    const toggleMenu = (event: Event) => {
      menu.value?.toggle(event);
    };

    const goToProfile = () => {
      router.push("/admin/profile");
    };

    const goToSettings = () => {
      router.push("/admin/settings");
    };

    const logout = () => {
      console.log("Cerrando sesión...");
    };

    return { menu, toggleMenu, goToProfile, goToSettings, logout };
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
  padding: 15px 30px;
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
  color: #f0db4f;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.username {
  font-size: 1rem;
  font-weight: 500;
}

.avatar {
  border: 2px solid #f0db4f;
  transition: transform 0.2s ease-in-out;
}

.avatar:hover {
  transform: scale(1.1);
}

.user-menu {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  padding: 10px 0;
  min-width: 180px;
}

.user-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-menu li {
  padding: 10px 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-menu li:hover {
  background: #f0f0f0;
}

.user-menu i {
  font-size: 1.2rem;
  color: #14124f;
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
