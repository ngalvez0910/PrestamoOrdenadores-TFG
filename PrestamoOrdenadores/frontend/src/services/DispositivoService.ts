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


export const actualizarDispositivo = async (
    guid: string,
    data: { componentes: string; estadoDispositivo: string; incidenciaGuid: string | null }
): Promise<Dispositivo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`http://localhost:8080/dispositivos/${guid}`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar el dispositivo:', error);
        return null;
    }
};