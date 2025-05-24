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
      path: '/sanciones/me',
      name: 'SancionesMe',
      component: () => import('../views/user/SancionesMe.vue'),
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
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFound.vue'),
      meta: { requiresAuth: false }
    }
  ],
})

router.beforeEach(async (to, from, next) => {
  authService.syncFromStorage();

  await authService.refreshAuth();

  const isAuthenticated = authService.isAuthenticated();
  const userRole = authService.role;
  const isLoginPage = to.name === 'home' || to.name === 'Login';

  if (isAuthenticated && isLoginPage) {
    console.log('[Router Guard] Usuario autenticado intentando ir a la página de inicio/login. Redirigiendo al dashboard por defecto.');
    if (userRole === 'ADMIN') {
      next({ name: 'adminDashboard' });
    } else if (userRole === 'ALUMNO' || userRole === 'PROFESOR') {
      next({ name: 'Profile' });
    } else {
      console.warn(`[Router Guard] Rol de usuario desconocido: ${userRole}. Redirigiendo a /profile.`);
      next({ name: 'Profile' });
    }
    return;
  }

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiredRole = to.matched.find(record => record.meta.requiredRole)?.meta.requiredRole;

  if (requiresAuth) {
    try {
      if (!isAuthenticated) {
        console.log('[Router Guard] Ruta requiere autenticación, pero el usuario no está logueado. Redirigiendo a login.');
        next({
          name: 'home',
          query: { redirect: to.fullPath }
        });
        return;
      }

      if (requiredRole) {
        if (userRole !== requiredRole) {
          console.warn(`[Router Guard] Acceso denegado. Rol requerido: ${requiredRole}, Rol del usuario: ${userRole}. Redirigiendo a no autorizado.`);
          next({ name: 'Unauthorized' });
          return;
        }
      }
      next();

    } catch (error) {
      console.error('[Router Guard] Error al verificar autenticación o roles:', error);
      next({ name: 'home' });
    }
  } else {
    next();
  }
});

export default router