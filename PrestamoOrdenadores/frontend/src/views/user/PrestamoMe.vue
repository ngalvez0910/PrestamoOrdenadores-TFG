<template>
  <AdminMenuBar/>

  <button class="back-button">
    <a href="/profile">
      <i class="pi pi-arrow-left"></i>
    </a>
  </button>

  <button class="buttonPrestamo" label="realizarPrestamo" @click="realizarPrestamo">Realizar Préstamo</button>

  <div style="margin-left: -40%; margin-top: 2%; width: 150%; height: 600px; overflow-y: auto;">
    <div class="table row-12">
      <DataTable :value="prestamos" stripedRows tableStyle="min-width: 50rem">
        <Column>
          <template #header>
            <b>Número de serie</b>
          </template>
          <template #body="slotProps">
            {{ slotProps.data.dispositivo?.numeroSerie ?? '—' }}
          </template>
        </Column>
        <Column field="fechaPrestamo">
          <template #header>
            <b>Fecha Préstamo</b>
          </template>
        </Column>
        <Column field="fechaDevolucion">
          <template #header>
            <b>Fecha Devolución</b>
          </template>
        </Column>
        <Column field="estadoPrestamo">
          <template #header>
            <b>Estado</b>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <ConfirmDialog />
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import { getPrestamosByUserGuid, createPrestamo, descargarPdfPrestamo, type Prestamo } from '@/services/PrestamoService.ts';
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
      console.log('Iniciando proceso de préstamo');
      confirm.require({
        message: '¿Estás seguro de que deseas realizar el préstamo?',
        header: 'Confirmación',
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Sí',
        rejectLabel: 'No',
        accept: async () => {
          console.log('Confirmación aceptada, llamando a createPrestamo');
          try {
            const prestamoGuid = await createPrestamo();
            if (prestamoGuid) {
              toast.add({ severity: 'success', summary: 'Éxito', detail: 'Préstamo realizado con éxito.', life: 3000 });
              await fetchPrestamos();
              console.log('Llamando a descargarPdfPrestamo con GUID:', prestamoGuid);
              await descargarPdfPrestamo(prestamoGuid);
              console.log('Descarga de PDF iniciada');
            } else {
              toast.add({ severity: 'error', summary: 'Error', detail: 'Error al realizar el préstamo.', life: 5000 });
            }
            console.log('Proceso de préstamo completado');
          } catch (error: any) {
            console.error('Error al realizar el préstamo:', error.message);
            toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 5000 });
            console.log('Proceso de préstamo fallido');
          }
        },
        reject: () => {
          console.log('Confirmación rechazada');
          toast.add({ severity: 'info', summary: 'Cancelado', detail: 'Operación cancelada.', life: 3000 });
        },
      });
    };

    return { prestamos, realizarPrestamo };
  },
});
</script>

<style scoped>
.back-button {
  padding: 0.3rem 0.5rem;
  font-size: 0.875rem;
  background-color: #14124f;
  color: white;
  border: none;
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
  margin-left: -40%;
  margin-top: 40%;
  width: 5%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
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
  color: inherit !important;
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