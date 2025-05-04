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
@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=Inconsolata:wght@200..900&family=Montserrat:ital,wght@0,100..900;1,100..900&family=NTR&family=Quicksand&display=swap');

body{
  font-family: 'Montserrat', sans-serif;
}

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
  font-size: 1.8rem;
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
