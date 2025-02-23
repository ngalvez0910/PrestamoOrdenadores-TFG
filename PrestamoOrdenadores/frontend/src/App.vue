<template>
  <div class="app">
    <router-view />  <!-- Aquí se cargan las vistas (Home, Dashboard, etc.) -->
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Login from './views/Login.vue';  // Componente de login

export default defineComponent({
  name: 'App',
  components: {
    Login
  },
});
</script>