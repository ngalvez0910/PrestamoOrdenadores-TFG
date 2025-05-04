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
.menubar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  background-color: var(--color-text-on-dark);
  color: var(--color-primary);
  padding: 10px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  box-sizing: border-box;
}

.menubar h1 {
  margin: 0;
}

.menubar h1 a {
  color: var(--color-primary);
  text-decoration: none;
  font-size: 1.8rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: var(--color-interactive);
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
