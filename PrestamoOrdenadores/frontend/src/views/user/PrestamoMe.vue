<template>
  <AdminMenuBar/>

  <div class="botones-container">
    <div class="boton-atras">
      <a href="/profile">
        <button class="back-button">
          <i class="pi pi-arrow-left"></i>
        </button>
      </a>
    </div>
  </div>

  <button class="buttonPrestamo" label="realizarPrestamo" @click="realizarPrestamo">Realizar Préstamo</button>

  <div class="table row-12" style="margin-left: -17%; margin-top: 5%">
    <DataTable :value="prestamos" stripedRows tableStyle="min-width: 50rem">
      <Column header="Número de serie">
        <template #body="slotProps">
          {{ slotProps.data.dispositivo?.numeroSerie ?? '—' }}
        </template>
      </Column>
      <Column field="fechaPrestamo" header="Fecha Préstamo"></Column>
      <Column field="fechaDevolucion" header="Fecha Devolución"></Column>
      <Column field="estadoPrestamo" header="Estado"></Column>
    </DataTable>
  </div>

  <ConfirmDialog />
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import { getPrestamosByUserGuid, createPrestamo, type Prestamo } from '@/services/PrestamoService.ts';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import { useConfirm } from 'primevue/useconfirm';


export default defineComponent({
  name: "PrestamoMe",
  components: { AdminMenuBar },
  setup() {
    const prestamos = ref<Prestamo[]>([]);
    const toast = useToast();
    const router = useRouter();
    const confirm = useConfirm();


    onMounted(async () => {
      await fetchPrestamos();
    });

    const fetchPrestamos = async () => {
      try {
        const data = await getPrestamosByUserGuid();
        prestamos.value = data;
        console.log("Datos de préstamos:", prestamos.value);
      } catch (error) {
        console.error("Error al obtener los préstamos:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los préstamos.', life: 3000 });
      }
    };

    const realizarPrestamo = async () => {
      confirm.require({
        message: '¿Estás seguro de que deseas realizar el préstamo?',
        header: 'Confirmación',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Sí',
        rejectLabel: 'No',
        accept: async () => {
          try {
            await createPrestamo();
            toast.add({ severity: 'success', summary: 'Éxito', detail: 'Préstamo realizado con éxito.', life: 3000 });
            await fetchPrestamos();
          } catch (error: any) {
            console.error('Error al realizar el préstamo:', error.message);
            toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 5000 });
          }
        },
        reject: () => {
          toast.add({ severity: 'info', summary: 'Cancelado', detail: 'Operación cancelada.', life: 3000 });
        },
      });
    };


    return { prestamos, realizarPrestamo };
  },
});
</script>

<style scoped>
.boton-atras {
  margin-left: -45%;
  margin-top: 15%;
}

.back-button {
  padding: 0.7rem 1.2rem;
  font-size: 0.875rem;
  background-color: #14124f;
  color: white;
  border: none;
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
  margin-top: 20%;
  width: 5%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-button:hover{
  background-color: #0d0c34;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(72, 70, 159);
}

.back-button i {
  pointer-events: none;
}

a{
  background-color: inherit !important;
}

.botones-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-top: -35%;
}

.buttonPrestamo {
  background-color: #d6621e;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 5px;
  font-size: 1.1rem;
  width: 25%;
  transition: all 0.3s ease-in-out;
  margin-bottom: -1%;
  margin-left: 80%;
  margin-top: -35%;
}

.buttonPrestamo:hover {
  background-color: #a14916;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(236, 145, 96);
}
</style>