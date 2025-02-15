<template>
  <div class="app">
    <CustomMenubar />  <!-- Menú que aparece en todas las vistas -->
    <router-view />  <!-- Aquí se cargan las vistas (Home, Dashboard, etc.) -->
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import CustomMenubar from './components/MenuBar.vue'; // Importar el componente CustomMenubar

export default defineComponent({
  name: 'App',
  components: {
    CustomMenubar // Registrar el componente
  }
});
</script>
