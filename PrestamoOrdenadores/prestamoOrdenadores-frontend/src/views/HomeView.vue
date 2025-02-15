<template>
  <div class="home">
    <h1>Bienvenido al Sistema de Préstamo de Ordenadores</h1>
    <p>Aquí podrás gestionar todos los préstamos.</p>
    <router-link to="/dashboard">Ir al Dashboard</router-link>  <!-- Enlace al dashboard -->
  </div>
</template>

<script lang="ts">
export default {
  name: 'Home',
};
</script>
