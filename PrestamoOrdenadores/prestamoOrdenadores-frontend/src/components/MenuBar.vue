<template>
  <Menubar :model="items" class="menubar" />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Menubar from 'primevue/menubar';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'CustomMenubar',
  components: {
    Menubar, // Registrar el Menubar dentro del componente
  },
  setup() {
    const router = useRouter(); // Usamos `useRouter` para obtener acceso al enrutador

    const items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        command: () => router.push('/') // Navegación al Home
      },
      {
        label: 'Features',
        icon: 'pi pi-star'
      },
      {
        label: 'Projects',
        icon: 'pi pi-search',
        items: [
          {
            label: 'Components',
            icon: 'pi pi-bolt'
          },
          {
            label: 'Blocks',
            icon: 'pi pi-server'
          },
          {
            label: 'UI Kit',
            icon: 'pi pi-pencil'
          }
        ]
      },
      {
        label: 'Contact',
        icon: 'pi pi-envelope'
      }
    ];

    return { items };
  }
});
</script>

<style scoped>
.menubar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}
</style>
