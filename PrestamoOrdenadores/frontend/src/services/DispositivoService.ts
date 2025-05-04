import axios from 'axios';

interface Dispositivo {
    guid: string;
    numeroSerie: string;
    componentes: string;
    estadoDispositivo: string;
    incidencia?: { guid: string; };
    incidenciaGuid?: string;
    stock: number;
    isActivo: boolean;
    createdDate: string;
    updatedDate: string;
}

export const getDispositivoByGuid = async (guid: string): Promise<Dispositivo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/dispositivos/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const dispositivo: Dispositivo = {
            ...response.data,
            estadoDispositivo: response.data.estadoDispositivo?.toUpperCase() || '',
            incidencia: response.data.incidencia ? { guid: response.data.incidencia.guid } : null,
            incidenciaGuid: response.data.incidencia ? response.data.incidencia.guid : null,
        };

        return dispositivo;
    } catch (error) {
        console.error('Error obteniendo dispositivo por GUID:', error);
        return null;
    }
};

export const descargarCsvDispositivos = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/csv/dispositivos`, {
            responseType: 'blob',
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        const today = new Date();
        const formattedDate = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}-${today.getDate().toString().padStart(2, '0')}`;
        link.setAttribute('download', `dispositivos_${formattedDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el CSV de dispositivos', error);
        throw error;
    }
}