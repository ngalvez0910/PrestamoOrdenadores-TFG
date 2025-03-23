import axios from 'axios';

interface Dispositivo {
    guid: string;
    numeroSerie: string;
    componentes: string;
    estadoDispositivo: string;
    incidenciaGuid: string | null;
    stock: number;
    isActivo: boolean;
    createdDate: string;
    updatedDate: string;
}

export const getDispositivoByGuid = async (guid: string): Promise<Dispositivo | null> => {
    try {
        const response = await axios.get(`http://localhost:8080/dispositivos/${guid}`);
        return response.data || null;
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
        const response = await axios.patch(`http://localhost:8080/dispositivos/${guid}`, data);
        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar el dispositivo:', error);
        return null;
    }
};