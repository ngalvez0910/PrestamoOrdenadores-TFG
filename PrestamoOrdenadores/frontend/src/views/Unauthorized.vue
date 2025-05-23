<template>
  <div class="unauthorized-container">
    <div class="unauthorized-content">
      <h1>⚠️ Acceso No Autorizado</h1>
      <p>No tienes permisos para acceder a esta página.</p>
      <p>Necesitas tener rol de Administrador para ver este contenido.</p>

      <div class="actions">
        <button @click="goBack" class="btn btn-primary">
          Volver
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.go(-1);
};
</script>

<style scoped>
.unauthorized-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.unauthorized-content {
  text-align: center;
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 500px;
}

.unauthorized-content h1 {
  color: var(--color-error);
  margin-bottom: 1rem;
}

.unauthorized-content p {
  color: #666;
  margin-bottom: 1rem;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
}
</style>