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
  justify-content: space-between;
  align-items: center;
  background-color: #14124f;
  color: #fff;
  padding: 10px 30px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.menubar h1 a {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
  transition: color 0.3s ease;
}

.menubar h1 a:hover {
  color: inherit !important;
  background-color: inherit !important;
}

@media (max-width: 768px) {
  .menubar {
    padding: 12px 20px;
  }

  .menubar h1 a {
    font-size: 1.3rem;
  }
}
</style>
