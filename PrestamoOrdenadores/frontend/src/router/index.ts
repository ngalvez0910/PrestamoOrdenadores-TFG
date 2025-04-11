import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue';
import DispositivosDashboard from "@/views/admin/DispositivosDashboard.vue";
import UsuariosDashboard from "@/views/admin/UsuariosDashboard.vue";
import IncidenciasDashboard from "@/views/admin/IncidenciasDashboard.vue";
import Profile from "@/views/user/Profile.vue";
import DispositivoDetalle from "@/views/admin/DispositivoDetalle.vue";
import SancionDashboard from "@/views/admin/SancionDashboard.vue";

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
      path: '/admin/dashboard/usuarios',
      name: 'adminDashboardUsuarios',
      component: UsuariosDashboard,
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
      component: () => import("@/views/admin/DispositivoDetalle.vue"),
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
      component: () => import("@/views/admin/UsuarioDetalle.vue"),
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: "/admin/incidencia/detalle/:guid",
      name: "IncidenciaDetalle",
      component: () => import("@/views/admin/IncidenciaDetalle.vue"),
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
