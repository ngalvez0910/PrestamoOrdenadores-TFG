import axios from 'axios';

interface Sancion {
    guid: string;
    tipo: string;
    user: { guid: string; numeroIdentificacion: string; };
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
            user: response.data.user ? { numeroIdentificacion: response.data.user.numeroIdentificacion } : null,
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
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`http://localhost:8080/sanciones/${guid}`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar la sancion:', error);
        return null;
    }
};

export const descargarCsvSanciones = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/csv/sanciones`, {
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
        link.setAttribute('download', `sanciones_${formattedDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el CSV de sanciones', error);
        throw error;
    }
}