import axios from 'axios';

interface Sancion {
    guid: string;
    tipo: string;
    user: { guid: string; };
    userGuid: string;
    fechaSancion: string;
    createdDate: string;
    updatedDate: string;
}

export const getSancionByGuid = async (guid: string): Promise<Sancion | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/sanciones/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const sancion: Sancion = {
            ...response.data,
            user: response.data.user ? { guid: response.data.user.guid } : null,
            userGuid: response.data.user ? response.data.user.guid : null,
        };

        return sancion;
    } catch (error) {
        console.error('Error obteniendo sancion por GUID:', error);
        return null;
    }
};

export const actualizarSancion = async (
    guid: string,
    data: { tipoSancion: string }
): Promise<Sancion | null> => {
    try {
        const response = await axios.patch(`http://localhost:8080/sanciones/${guid}`, data);
        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar la sancion:', error);
        return null;
    }
};
