import axios from 'axios';

export type DeviceState = 'DISPONIBLE' | 'NO_DISPONIBLE' | 'PRESTADO';

interface Dispositivo {
    guid: string;
    numeroSerie: string;
    componentes: string;
    estado: DeviceState;
    estadoDispositivo: string;
    incidencia?: { guid: string; };
    incidenciaGuid?: string;
    stock: number;
    isActivo: boolean;
    createdDate: string;
    updatedDate: string;
    isDeleted: boolean;
}

interface DispositivoCreateRequest {
    numeroSerie: string;
    componentes: string;
}

const API_URL = "https://loantechoficial.onrender.com";

export const getDispositivoByGuid = async (guid: string): Promise<Dispositivo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`${API_URL}/dispositivos/${guid}`, {
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

export const actualizarDispositivo = async (guid: string, data: { componentes: string; estadoDispositivo: string; incidenciaGuid: string | null }): Promise<Dispositivo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`${API_URL}/dispositivos/${guid}`, data, {
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

export const descargarDispositivosXLSX = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`${API_URL}/storage/excel/dispositivos`, {
            responseType: 'blob',
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const url = window.URL.createObjectURL(new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }));
        const link = document.createElement('a');
        link.href = url;

        const today = new Date();
        const formattedDate = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}-${today.getDate().toString().padStart(2, '0')}`;
        link.setAttribute('download', `dispositivos_${formattedDate}.xlsx`);

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el archivo Excel de dispositivos', error);
        throw error;
    }
};

export const addDispositivoStock = async (request: DispositivoCreateRequest): Promise<Dispositivo> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('Intento de añadir dispositivo sin token.');
        throw new Error('No autenticado o token no encontrado.');
    }
    try {
        console.log('[DispositivoService] Añadiendo nuevo dispositivo con request:', request);
        const response = await axios.post<Dispositivo>(`${API_URL}/dispositivos`, request, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        console.log('[DispositivoService] Dispositivo añadido, respuesta:', response.data);
        return response.data;
    } catch (error) {
        console.error('[DispositivoService] Error al añadir dispositivo:', error);
        throw error;
    }
};

export const deleteDispositivo = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error("No autenticado");
        }

        const url = `${API_URL}/dispositivos/delete/${guid}`;
        await axios.patch(url, {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error('Error obteniendo dispositivo por GUID:', error);
        return null;
    }
};