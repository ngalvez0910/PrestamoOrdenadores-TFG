<template>
  <div class="menubar">
    <h1><a href="/">LoanTech</a></h1>
  </div>
</template>

<script lang="ts">

export default {
  name: "MenuBarNoSession",
  components: {}
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,400;0,500;0;700&display=swap');

:root {
  --color-primary: #14124f;
  --color-interactive: #4A90E2;
  --color-accent-soft: #AEC6F4;
  --color-background-main: #F8F9FA;
  --color-text-dark: #495057;
  --color-neutral-medium: #CED4DA;
  --color-text-on-dark: #E0E7FF;
  --color-text-on-dark-hover: #FFFFFF;
}

body {
  font-family: 'Montserrat', sans-serif;
  margin: 0;
}

.menubar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  background-color: var(--color-primary);
  color: var(--color-text-on-dark);
  padding: 10px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  box-sizing: border-box;
}

.menubar h1 {
  margin: 0;
}

.menubar h1 a {
  color: var(--color-text-on-dark);
  text-decoration: none;
  font-size: 1.8rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: var(--color-text-on-dark-hover);
}

@media (max-width: 768px) {
  .menubar {
    padding: 12px 20px;
  }

  .menubar h1 a {
    font-size: 1.5rem;
  }
}
</style>
