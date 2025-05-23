import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue';
import DispositivosDashboard from "@/views/admin/dispositivos/DispositivosDashboard.vue";
import UsuariosDashboard from "@/views/admin/users/UsuariosDashboard.vue";
import IncidenciasDashboard from "@/views/admin/incidencias/IncidenciasDashboard.vue";
import Profile from "@/views/user/Profile.vue";
import SancionDashboard from "@/views/admin/sanciones/SancionDashboard.vue";
import StorageDashboard from "@/views/admin/storage/StorageDashboard.vue";
import PrestamoDashboard from "@/views/admin/prestamos/PrestamoDashboard.vue";
import {authService} from "@/services/AuthService.ts";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Login,
      meta: { requiresAuth: false },
    },
    {
      path: '/registro',
      name: 'registro',
      component: () => import('../views/Registro.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/admin/dashboard',
      name: 'adminDashboard',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/dispositivos',
      name: 'adminDashboardDispositivos',
      component: DispositivosDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/prestamos',
      name: 'adminDashboardPrestamos',
      component: PrestamoDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/usuarios',
      name: 'adminDashboardUsuarios',
      component: UsuariosDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/storage',
      name: 'adminDashboardStorage',
      component: StorageDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/incidencias',
      name: 'adminDashboardIncidencias',
      component: IncidenciasDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/admin/dashboard/sanciones',
      name: 'adminDashboardSanciones',
      component: SancionDashboard,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
      meta: { requiresAuth: true },
    },
    {
      path: "/admin/dispositivo/detalle/:guid",
      name: "DispositivoDetalle",
      component: () => import("@/views/admin/dispositivos/DispositivoDetalle.vue"),
      props: route => ({
        guid: route.params.guid,
        numeroSerie: route.query.numeroSerie,
        componentes: route.query.componentes,
        estado: route.query.estado,
        incidenciaGuid: route.query.incidenciaGuid,
      }),
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: "/admin/usuario/detalle/:guid",
      name: "UsuarioDetalle",
      component: () => import("@/views/admin/users/UsuarioDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: "/admin/incidencia/detalle/:guid",
      name: "IncidenciaDetalle",
      component: () => import("@/views/admin/incidencias/IncidenciaDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: "/admin/sancion/detalle/:guid",
      name: "SancionDetalle",
      component: () => import("@/views/admin/sanciones/SancionDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: "/admin/prestamo/detalle/:guid",
      name: "PrestamoDetalle",
      component: () => import("@/views/admin/prestamos/PrestamoDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
    {
      path: '/notificaciones',
      name: 'Notificaciones',
      component: () => import('../views/user/Notificaciones.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/prestamo/me',
      name: 'PrestamoMe',
      component: () => import('../views/user/PrestamoMe.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/incidencias/me',
      name: 'IncidenciasMe',
      component: () => import('../views/user/IncidenciasMe.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/politicaPrivacidad',
      name: 'PoliticaPrivacidad',
      component: () => import('../views/PoliticaPrivacidad.vue'),
      meta: { requiresAuth: true},
    },
    {
      path: '/terminosServicio',
      name: 'TerminosServicios',
      component: () => import('../views/TerminosServicios.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/unauthorized',
      name: 'Unauthorized',
      component: () => import('../views/Unauthorized.vue'),
      meta: { requiresAuth: false },
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  authService.syncFromStorage();

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  const requiredRole = to.matched.find(record => record.meta.requiredRole)?.meta.requiredRole;

  if (requiresAuth) {
    try {
      const isAuthenticated = authService.isAuthenticated();

      if (!isAuthenticated) {
        next({
          name: 'home',
          query: { redirect: to.fullPath }
        });
        return;
      }

      if (requiredRole) {
        const userRole = authService.role;

        if (userRole !== requiredRole) {
          console.warn(`Access denied. Required role: ${requiredRole}, User role: ${userRole}`);
          next({ name: 'Unauthorized' });
          return;
        }
      }

      next();

    } catch (error) {
      console.error('Error checking authentication:', error);
      next({ name: 'home' });
    }
  } else {
    next();
  }
});

export default router