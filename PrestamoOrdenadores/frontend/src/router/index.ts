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
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/dispositivos',
      name: 'adminDashboardDispositivos',
      component: DispositivosDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/prestamos',
      name: 'adminDashboardPrestamos',
      component: PrestamoDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/usuarios',
      name: 'adminDashboardUsuarios',
      component: UsuariosDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/storage',
      name: 'adminDashboardStorage',
      component: StorageDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/incidencias',
      name: 'adminDashboardIncidencias',
      component: IncidenciasDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/admin/dashboard/sanciones',
      name: 'adminDashboardSanciones',
      component: SancionDashboard,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
      meta: { requiresAuth: true, roles: ['ADMIN', 'ALUMNO', 'PROFESOR'] },
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
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: "/admin/usuario/detalle/:guid",
      name: "UsuarioDetalle",
      component: () => import("@/views/admin/users/UsuarioDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: "/admin/incidencia/detalle/:guid",
      name: "IncidenciaDetalle",
      component: () => import("@/views/admin/incidencias/IncidenciaDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: "/admin/sancion/detalle/:guid",
      name: "SancionDetalle",
      component: () => import("@/views/admin/sanciones/SancionDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: "/admin/prestamo/detalle/:guid",
      name: "PrestamoDetalle",
      component: () => import("@/views/admin/prestamos/PrestamoDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/cambioContrasena',
      name: 'CambioContrasena',
      component: () => import('../views/user/CambioContrasena.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN', 'ALUMNO', 'PROFESOR'] },
    },
    {
      path: '/prestamo/me',
      name: 'PrestamoMe',
      component: () => import('../views/user/PrestamoMe.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN', 'ALUMNO', 'PROFESOR'] },
    },
  ],
})

export default router
