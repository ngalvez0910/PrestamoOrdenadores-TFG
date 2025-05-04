<template>
  <div class="app">
    <router-view />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Login from './views/Login.vue';

export default defineComponent({
  name: 'App',
  components: {
    Login
  },
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,400;0,500;0;700&display=swap');

:root {
  --color-primary: #14124f;
  --color-primary-rgb: 20, 18, 79;
  --color-interactive: #4A90E2;
  --color-interactive-rgb: 74, 144, 226;
  --color-interactive-darker: #3B7ACC;
  --color-accent-soft: #AEC6F4;
  --color-background-main: #F8F9FA;
  --color-text-dark: #495057;
  --color-neutral-medium: #CED4DA;
  --color-text-on-dark: #E0E7FF;
  --color-text-on-dark-hover: #FFFFFF;
  --color-error: #DC3545;
  --color-error-rgb: 220, 53, 69;
  --color-success: #198754;
  --color-success-rgb: 25, 135, 84;
  --color-warning: #FFC107;
  --color-warning-rgb: 255, 193, 7;
}

body {
  font-family: 'Montserrat', sans-serif;
  background-color: var(--color-background-main);
  color: var(--color-text-dark);
  margin: 0;
  padding: 0;
  line-height: 1.5;
}

* {
  box-sizing: border-box;
}

a {
  color: var(--color-interactive);
  text-decoration: none;
}
</style>