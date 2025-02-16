<template>
  <div class="menubar">
    <h1><a href="/admin/dashboard">LoanTech</a></h1>
    <div class="user-info">
      <p>usuario</p>
      <Avatar image="https://placehold.co/400" shape="circle" />
    </div>
  </div>
</template>

<script lang="ts">
import Avatar from 'primevue/avatar';

export default {
  name: 'MenuBar',
  components: {
    Avatar
  }
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
  padding: 15px 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

h1, p {
  margin: 0;
}

a{
  color: white;
}
</style>
