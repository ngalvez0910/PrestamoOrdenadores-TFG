import axios from 'axios';

interface Incidencia {
    guid: string;
    asunto: string;
    descripcion: string;
    estado: string;
    userGuid: string;
    createdDate: string;
    updatedDate: string;
}

export const getIncidenciaByGuid = async (guid: string): Promise<Incidencia | null> => {
    try {
        const response = await axios.get(`http://localhost:8080/incidencias/${guid}`);
        return response.data || null;
    } catch (error) {
        console.error('Error obteniendo incidencia por GUID:', error);
        return null;
    }
};
