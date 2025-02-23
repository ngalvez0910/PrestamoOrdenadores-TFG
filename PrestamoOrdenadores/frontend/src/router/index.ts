import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminDashboard from '../views/AdminDashboard.vue';
import DispositivosDashboard from "@/views/DispositivosDashboard.vue";
import UsuariosDashboard from "@/views/UsuariosDashboard.vue";
import IncidenciasDashboard from "@/views/IncidenciasDashboard.vue";
import Profile from "@/views/Profile.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Login,
    },
    {
      path: '/registro',
      name: 'registro',
      component: () => import('../views/Registro.vue'),
    },
    {
      path: '/admin/dashboard',
      name: 'adminDashboard',
      component: AdminDashboard,
    },
    {
      path: '/admin/dashboard/dispositivos',
      name: 'adminDashboardDispositivos',
      component: DispositivosDashboard,
    },
    {
      path: '/admin/dashboard/usuarios',
      name: 'adminDashboardUsuarios',
      component: UsuariosDashboard,
    },
    {
      path: '/admin/dashboard/incidencias',
      name: 'adminDashboardIncidencias',
      component: IncidenciasDashboard,
    },
    {
      path: '/admin/profile',
      name: 'Profile',
      component: Profile,
    }
  ],
})

export default router
