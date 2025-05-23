<template>
  <div class="menubar">
    <div class="brand-and-nav">
      <h1><a :href="getHomeRoute()">LoanTech</a></h1>
      <nav class="main-nav">
        <router-link :to="getHomeRoute()" class="nav-link">
          <i class="pi pi-home nav-link-icon"></i> Inicio
        </router-link>

        <template v-if="isAdmin">
          <Button
              text
              class="nav-button admin-dropdown-trigger"
              :class="{ 'admin-section-active': isInAdminSection }"
          @click="toggleAdminMenu($event)"
          aria-haspopup="true"
          aria-controls="admin_menu"
          >
          <i class="pi pi-sliders-h nav-link-icon"></i>
          Administración <i class="pi pi-angle-down" style="margin-left: 5px;"></i>
          </Button>
        </template>

        <router-link v-for="link in userSpecificLinks" :key="link.label" :to="link.url" class="nav-link">
          <i v-if="link.icon" :class="link.icon + ' nav-link-icon'"></i>
          {{ link.label }}
        </router-link>
      </nav>
    </div>

    <Button class="user-info" @click="toggleUserMenu($event)" text aria-haspopup="true" aria-controls="user_profile_menu">
      <p class="username">{{ username || "Usuario" }}</p>
      <Avatar :image="currentAvatar" shape="circle" class="avatar" />
    </Button>


    <Menu ref="userMenu" id="user_profile_menu" class="user-menu" :model="userMenuItems" :popup="true">
      <template #item="{ item, props }">
        <a v-if="item.url" :href="item.url" class="menu-item-link" v-bind="props.action">
          <i v-if="item.icon" :class="item.icon + ' menu-item-icon'"></i>
          <span class="menu-item-label">{{ item.label }}</span>
        </a>
        <div v-else-if="item.separator" class="menu-separator"></div>
        <button v-else class="menu-item-link button-link" v-bind="props.action">
          <i v-if="item.icon" :class="item.icon + ' menu-item-icon'"></i>
          <span class="menu-item-label">{{ item.label }}</span>
        </button>
      </template>
    </Menu>

    <Menu v-if="isAdmin" ref="adminMenu" id="admin_menu" class="admin-menu" :model="adminNavItems" :popup="true">
      <template #item="{ item, props }">
        <a v-if="item.url" :href="item.url" class="menu-item-link" v-bind="props.action">
          <i v-if="item.icon" :class="item.icon + ' menu-item-icon'"></i>
          <span class="menu-item-label">{{ item.label }}</span>
        </a>
        <button v-else class="menu-item-link button-link" v-bind="props.action">
          <i v-if="item.icon" :class="item.icon + ' menu-item-icon'"></i>
          <span class="menu-item-label">{{ item.label }}</span>
        </button>
      </template>
    </Menu>

  </div>
</template>

<script lang="ts">
import Avatar from "primevue/avatar";
import Menu from "primevue/menu";
import Button from "primevue/button";
import {ref, computed, onMounted, watch} from "vue";
import { useRouter } from "vue-router";
import {authService, type UserData} from "@/services/AuthService.ts";

export default {
  name: "MenuBar",
  components: {
    Avatar,
    Menu,
    Button
  },
  setup() {
    const userMenu = ref();
    const adminMenu = ref();
    const router = useRouter();
    const avatarUpdateKey = ref(Date.now());
    const forceAvatarUpdate = () => {
      avatarUpdateKey.value = Date.now();
    };

    onMounted(() => {
      authService.syncFromStorage();

      if (authService.token && !authService.user) {
        authService.fetchUser().catch(console.error);
      }
    });

    const user = computed<UserData | null>(() => authService.user);
    const token = computed<string | null>(() => authService.token);

    const rol = computed(() => user.value?.rol || "");
    const username = computed(() => user.value?.nombre || "Usuario");

    const avatar = computed(() => {
      avatarUpdateKey.value;
      return user.value?.avatar || null;
    });

    const currentAvatar = computed(() => {
      return avatar.value || "https://placehold.co/400";
    });

    const roleFromService = computed(() => authService.role);
    const isAdmin = computed(() => roleFromService.value === "ADMIN");

    watch(() => user.value, (newUser, oldUser) => {
      if (newUser?.avatar !== oldUser?.avatar) {
        console.log("[MenuBar] User avatar changed, forcing update");
        forceAvatarUpdate();
      }
    }, { deep: true });

    const toggleUserMenu = (event: Event) => {
      userMenu.value?.toggle(event);
    };

    const isInAdminSection = computed(() => {
      return router.currentRoute.value.path.startsWith('/admin/');
    });

    const toggleAdminMenu = (event: Event) => {
      if (isAdmin.value) {
        adminMenu.value?.toggle(event);
      }
    };

    const userMenuItems = ref([
      {
        label: 'Perfil',
        icon: 'pi pi-user',
        command: () => router.push("/profile")
      },
      {
        separator: true
      },
      {
        label: 'Cerrar sesión',
        icon: 'pi pi-sign-out',
        command: async () => {
          console.log("[MenuBar] Ejecutando comando Cerrar sesión...");
          try {
            await authService.logout();
            console.log("[MenuBar] authService.logout() ejecutado.");
            await router.push("/");
          } catch (error) {
            console.error("[MenuBar] Error durante logout:", error);
            await router.push("/");
          }
        }
      }
    ]);

    const adminNavItems = ref([
      { label: 'Préstamos Admin', icon: 'pi pi-arrow-right-arrow-left', command: () => router.push('/admin/dashboard/prestamos') },
      { label: 'Dispositivos Admin', icon: 'pi pi-desktop', command: () => router.push('/admin/dashboard/dispositivos') },
      { label: 'Usuarios Admin', icon: 'pi pi-users', command: () => router.push('/admin/dashboard/usuarios') },
      { label: 'Incidencias Admin', icon: 'pi pi-flag-fill', command: () => router.push('/admin/dashboard/incidencias') },
      { label: 'Sanciones Admin', icon: 'pi pi-ban', command: () => router.push('/admin/dashboard/sanciones') },
      { label: 'Almacenamiento', icon: 'pi pi-database', command: () => router.push('/admin/dashboard/storage') },
    ]);

    const userSpecificLinks = ref([
      { label: 'Mis Préstamos', url: '/prestamo/me', icon: 'pi pi-arrow-right-arrow-left' },
      { label: 'Mis Incidencias', url: '/incidencias/me', icon: 'pi pi-flag-fill' },
      { label: 'Notificaciones', url: '/notificaciones', icon: 'pi pi-bell' },
    ]);

    const getHomeRoute = () => {
      return isAdmin.value ? "/admin/dashboard" : "/profile";
    };

    return {
      userMenu,
      adminMenu,
      toggleUserMenu,
      toggleAdminMenu,
      userMenuItems,
      adminNavItems,
      userSpecificLinks,
      username,
      currentAvatar,
      isAdmin,
      isInAdminSection,
      getHomeRoute,
      forceAvatarUpdate
    };
  },
};
</script>

<style scoped>
.menubar {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--color-text-on-dark);
  color: var(--color-primary);
  padding: 10px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.brand-and-nav {
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.menubar h1 {
  margin-right: 40px;
}

.menubar h1 a {
  color: var(--color-primary);
  text-decoration: none;
  font-size: 1.8rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: var(--color-interactive);
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link, .nav-button {
  color: var(--color-primary);
  text-decoration: none;
  font-size: 1rem;
  padding: 8px 10px;
  position: relative;
  transition: color 0.3s ease, background-color 0.3s ease;
  border-radius: 6px;
}

.nav-link:hover, .nav-button:hover,
.nav-link:focus, .nav-button:focus{
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
}

.nav-link.router-link-exact-active{
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
}

.admin-dropdown-trigger.p-button {
  background-color: transparent !important;
  border: none !important;
  color: var(--color-primary) !important;
  padding: 8px 10px !important;
  font-size: 1rem !important;
  font-weight: normal !important;
  box-shadow: none !important;
}

.admin-dropdown-trigger.p-button:hover,
.admin-dropdown-trigger.p-button:focus {
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
}

.admin-dropdown-trigger .p-button-label {
  color: var(--color-primary);
}

.admin-dropdown-trigger:hover .p-button-label {
  color: var(--color-interactive-darker);
}

.admin-dropdown-trigger.admin-section-active .p-button-label,
.admin-dropdown-trigger.admin-section-active .nav-link-icon,
.admin-dropdown-trigger.admin-section-active .pi-angle-down,
.admin-dropdown-trigger.admin-section-active{
  color: var(--color-interactive-darker) !important;
}

.admin-dropdown-trigger.admin-section-active{
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
}

.nav-link-icon {
  margin-right: 6px;
}

.user-info.p-button {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  background-color: transparent !important;
  color: var(--color-primary) !important;
  border: 1px solid transparent !important;
  padding: 5px 10px !important;
  border-radius: 25px;
  transition: background-color 0.3s ease, border-color 0.3s ease;
  height: auto;
}

.user-info.p-button:hover,
.user-info.p-button:focus {
  background-color: rgba(var(--color-primary-rgb), 0.05) !important;
  border-color: rgba(var(--color-primary-rgb), 0.2) !important;
  box-shadow: none !important;
}

.username {
  font-size: 0.95rem;
  font-weight: 500;
  margin: 0;
  color: var(--color-primary);
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

:global(.p-menu.user-menu),
:global(.p-menu.admin-menu) {
  background-color: var(--color-text-on-dark-hover, white) !important;
  border: 1px solid var(--color-neutral-medium) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
  padding: 8px 0 !important;
  min-width: 200px;
}

.menu-item-link {
  font-family: 'Montserrat', sans-serif !important;
  display: flex !important;
  align-items: center !important;
  padding: 10px 15px !important;
  color: var(--color-text-dark) !important;
  text-decoration: none !important;
  transition: background-color 0.2s ease, color 0.2s ease;
  font-size: 0.95rem;
  border-radius: 6px;
  margin: 0 5px;
}

.menu-item-link.button-link {
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
}

.menu-item-icon {
  margin-right: 12px !important;
  color: var(--color-text-dark);
  font-size: 1.1rem;
}

.menu-item-label {
  flex-grow: 1;
}

.menu-item-link:hover {
  background-color: var(rgba(var(--color-interactive-rgb), 0.1)) !important;
  color: var(--color-interactive-darker) !important;
}

.menu-item-link:hover .menu-item-icon {
  color: var(--color-interactive-darker) !important;
}

.menu-separator {
  height: 1px;
  background-color: var(--color-neutral-medium);
  margin: 8px 0;
}

@media (max-width: 1100px) {
  .main-nav {
    gap: 10px;
  }
  .nav-link, .nav-button.admin-dropdown-trigger {
    font-size: 0.9rem;
    padding: 8px;
  }
  .menubar h1 {
    margin-right: 20px;
  }
}


@media (max-width: 992px) {
  .main-nav {
    display: none;
  }

  .brand-and-nav {
    flex-grow: 0;
  }

  .menubar {
    padding: 10px 15px;
  }

  .user-info.p-button {
    margin-left: auto;
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