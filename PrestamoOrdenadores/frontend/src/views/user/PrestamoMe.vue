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
  <Toast />
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
          } catch (error: any) {
            console.error('Error al realizar el préstamo:', error.response?.data || error.message);
            if (error.response?.status === 404 && error.response?.data === 'No hay dispositivos disponibles actualmente') {
              toast.add({
                severity: 'warn',
                summary: 'Advertencia',
                detail: 'No hay dispositivos disponibles para realizar el préstamo en este momento.',
                life: 5000,
                styleClass: 'custom-toast-warning'
              });
            } else {
              toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 5000 });
            }
          }
        },
        reject: () => {
          console.log('Confirmación rechazada');
          toast.add({
            severity: 'info',
            summary: 'Cancelado',
            detail: 'Operación cancelada.',
            life: 3000,
            styleClass: 'custom-toast-info'
          });
        },
      });
    };

    return { prestamos, realizarPrestamo, toast };
  },
});
</script>

<style>
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

.custom-toast-warning, .custom-toast-info {
  background-color: white !important;
  border-radius: 10px !important;
  padding: 10px !important;
}

.custom-toast-warning button, .custom-toast-info button {
  background-color: white !important;
  color: #14124f !important;
  width: 100% !important;
  margin-top: -75%
}

.p-confirm-dialog .p-confirm-dialog-accept {
  background-color: #28a745 !important;
  color: white !important;
  transition: all 0.3s ease-in-out;
}

.p-confirm-dialog .p-confirm-dialog-accept:hover {
  background-color: #218838 !important;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(103, 250, 29);
}

.p-confirm-dialog .p-confirm-dialog-reject {
  background-color: #dc3545 !important;
  color: white !important;
  transition: all 0.3s ease-in-out;
}

.p-confirm-dialog .p-confirm-dialog-reject:hover {
  background-color: #c82333 !important;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgb(253, 18, 18);
}

.p-confirm-dialog .p-confirm-dialog-accept,
.p-confirm-dialog .p-confirm-dialog-reject {
  width: 120px;
  text-align: center;
  align-items: center;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  justify-content: center;
  border-radius: 40px;
  border:none;
}

.p-confirm-dialog .p-dialog-header .p-dialog-header-icon {
  background-color: white !important;
  color: #14124f !important;
  width: 4.5rem !important;
  margin-top: -15%;
  margin-right: -25%;
  font-weight: bold;
  padding: 5px;
  font-size: 1.2rem !important;
}
</style>