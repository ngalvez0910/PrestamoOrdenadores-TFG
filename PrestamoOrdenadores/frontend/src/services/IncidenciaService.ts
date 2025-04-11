import axios from 'axios';

interface Incidencia {
    guid: string;
    asunto: string;
    descripcion: string;
    estado: string;
    user: { guid: string; };
    userGuid: string;
    createdDate: string;
    updatedDate: string;
}

export const getIncidenciaByGuid = async (guid: string): Promise<Incidencia | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/incidencias/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const incidencia: Incidencia = {
            ...response.data,
            user: response.data.user ? { guid: response.data.user.guid } : null,
            userGuid: response.data.user ? response.data.user.guid : null,
        };

        return incidencia;
    } catch (error) {
        console.error('Error obteniendo incidencia por GUID:', error);
        return null;
    }
};

export const actualizarIncidencia = async (
    guid: string,
    data: { estadoIncidencia: string }
): Promise<Incidencia | null> => {
    try {
        const response = await axios.patch(`http://localhost:8080/incidencias/${guid}`, data);
        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar la incidencia:', error);
        return null;
    }
};

export const descargarCsvIncidencias = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/csv/incidencias`, {
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
        link.setAttribute('download', `incidencias_${formattedDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el CSV de incidencias', error);
        throw error;
    }
}