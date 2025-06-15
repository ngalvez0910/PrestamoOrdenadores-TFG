<template>
  <div class="menubar">
    <div class="brand-and-nav">
      <h1><a :href="getHomeRoute()">LoanTech</a></h1>

      <nav class="main-nav desktop-nav">
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

    <div class="right-section">
      <Button
          icon="pi pi-bars"
          class="p-button-text p-button-rounded hamburger-menu-button"
          @click="toggleSidebar"
          aria-controls="sidebar_menu"
          aria-expanded="false"
      />

      <div class="notification-button-container">
        <Button
            class="notifications-button p-button-rounded p-button-text"
            icon="pi pi-bell"
            @click="goToNotifications"
        />
        <span v-if="unreadNotificationsCount > 0" class="notification-badge"></span>
      </div>

      <Button class="user-info" @click="toggleUserMenu($event)" text aria-haspopup="true" aria-controls="user_profile_menu">
        <p class="username">{{ username || "Usuario" }}</p>
        <Avatar :image="currentAvatar" shape="circle" class="avatar" />
      </Button>
    </div>


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

    <Sidebar v-model:visible="sidebarVisible" :baseZIndex="1000" position="left" class="custom-sidebar" style="fontFamily: 'Montserrat', sans-serif">
      <template #header>
        <h3 style="color: var(--color-primary); font-weight: 600;">Navegación</h3>
      </template>
      <div class="sidebar-nav-links">
        <router-link :to="getHomeRoute()" class="nav-link-sidebar" @click="sidebarVisible = false">
          <i class="pi pi-home nav-link-icon"></i> Inicio
        </router-link>

        <template v-if="isAdmin">
          <router-link to="/admin/dashboard" class="nav-link-sidebar" @click="sidebarVisible = false">
            <i class="pi pi-sliders-h nav-link-icon"></i> Administración
          </router-link>
        </template>

        <router-link v-for="link in userSpecificLinks" :key="link.label" :to="link.url" class="nav-link-sidebar" @click="sidebarVisible = false">
          <i v-if="link.icon" :class="link.icon + ' nav-link-icon'"></i>
          {{ link.label }}
        </router-link>
      </div>
    </Sidebar>

  </div>
</template>

<script lang="ts">
import Avatar from "primevue/avatar";
import Menu from "primevue/menu";
import Button from "primevue/button";
import {ref, computed, onMounted, watch, inject, type Ref} from "vue";
import { useRouter } from "vue-router";
import {authService, type UserData} from "@/services/AuthService.ts";
import Sidebar from 'primevue/sidebar';
import Tooltip from 'primevue/tooltip';
import axios from 'axios';

interface Notificacion {
  id: string;
  titulo: string;
  mensaje?: string;
  fecha: Date | string;
  leida: boolean;
  tipo?: string;
  enlace?: string;
}

export default {
  name: "MenuBar",
  components: {Avatar, Menu, Button, Sidebar},
  directives: {Tooltip},
  setup() {
    const userMenu = ref();
    const adminMenu = ref();
    const router = useRouter();
    const avatarUpdateKey = ref(Date.now());
    const sidebarVisible = ref(false);
    const unreadNotificationsCount = ref(0);

    const API_BASE_URL = inject<string>('API_BASE_URL', 'https://loantechoficial.onrender.com');
    const lastReceivedNotification = inject<Readonly<Ref<Notificacion | null>>>('lastReceivedNotification');

    const forceAvatarUpdate = () => {
      avatarUpdateKey.value = Date.now();
    };

    const fetchUnreadNotificationsCount = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const response = await axios.get<Notificacion[]>(`${API_BASE_URL}/notificaciones`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.data && Array.isArray(response.data)) {
          unreadNotificationsCount.value = response.data.filter(n => !n.leida).length;
        }
      } catch (error) {
        console.error("[MenuBar] Error al obtener notificaciones:", error);
      }
    };

    if (lastReceivedNotification) {
      watch(lastReceivedNotification, (newNotification) => {
        if (newNotification && !newNotification.leida) {
          unreadNotificationsCount.value += 1;
        }
      });
    }

    onMounted(() => {
      authService.syncFromStorage();

      if (authService.token && !authService.user) {
        authService.fetchUser().catch(console.error);
      }

      fetchUnreadNotificationsCount();
    });

    const user = computed<UserData | null>(() => authService.user);
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

    const toggleSidebar = () => {
      sidebarVisible.value = !sidebarVisible.value;
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
      { label: 'Mis Sanciones', url: '/sanciones/me', icon: 'pi pi-ban' },
    ]);

    const getHomeRoute = () => {
      return isAdmin.value ? "/admin/dashboard" : "/profile";
    };

    const goToNotifications = () => {
      unreadNotificationsCount.value = 0;
      router.push('/notificaciones');
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
      forceAvatarUpdate,
      goToNotifications,
      sidebarVisible,
      toggleSidebar,
      unreadNotificationsCount
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

.right-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.notification-button-container {
  position: relative;
  display: inline-block;
}

.notifications-button.p-button {
  background-color: transparent !important;
  color: var(--color-primary) !important;
  border: none !important;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50% !important;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.notifications-button.p-button:hover {
  background-color: rgba(var(--color-primary-rgb), 0.1) !important;
  color: var(--color-interactive) !important;
}

.notifications-button.p-button .p-button-icon {
  font-size: 1.3rem;
}

.notification-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background-color: #007bff;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  border: 2px solid var(--color-text-on-dark);
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

.hamburger-menu-button {
  display: none !important;
  color: var(--color-primary) !important;
  font-size: 1.5rem !important;
  width: 40px !important;
  height: 40px !important;
}

.hamburger-menu-button:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
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
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
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

:global(.custom-sidebar.p-sidebar) {
  background-color: var(--color-text-on-dark) !important;
  color: var(--color-primary) !important;
}

:global(.custom-sidebar .p-sidebar-header) {
  padding: 1.5rem !important;
  border-bottom: 1px solid rgba(var(--color-primary-rgb), 0.1);
}

:global(.custom-sidebar .p-sidebar-close) {
  color: var(--color-primary) !important;
  font-size: 1.3rem !important;
}

:global(.custom-sidebar .p-sidebar-close:hover) {
  background-color: rgba(var(--color-primary-rgb), 0.1) !important;
  color: var(--color-interactive) !important;
}

.sidebar-nav-links {
  display: flex;
  flex-direction: column;
  padding: 1rem 0;
}

.nav-link-sidebar {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: var(--color-primary);
  text-decoration: none;
  font-size: 1.1rem;
  font-weight: 500;
  transition: background-color 0.2s ease, color 0.2s ease;
  border-radius: 0;
}

.nav-link-sidebar:hover {
  background-color: rgba(var(--color-interactive-rgb), 0.1) !important;
  color: var(--color-interactive-darker) !important;
}

.nav-link-sidebar.router-link-exact-active {
  background-color: rgba(var(--color-interactive-rgb), 0.15) !important;
  color: var(--color-interactive-darker) !important;
  border-left: 4px solid var(--color-interactive) !important;
  padding-left: 16px;
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
  .main-nav.desktop-nav {
    display: none;
  }
  .hamburger-menu-button {
    display: inline-flex !important;
  }
  .brand-and-nav {
    flex-grow: 0;
    gap: 10px;
  }
  .menubar {
    padding: 10px 15px;
  }
  .right-section {
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
  .notifications-button.p-button {
    width: 36px;
    height: 36px;
  }
  .notifications-button.p-button .p-button-icon {
    font-size: 1.1rem;
  }
}
</style>