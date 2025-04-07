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
