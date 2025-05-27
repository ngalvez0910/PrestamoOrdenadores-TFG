import axios from 'axios';
import {jwtDecode} from "jwt-decode";
import type {UserData} from "@/services/AuthService.ts";

export interface Sancion {
    guid: string;
    tipo: string;
    user: { guid: string; numeroIdentificacion: string; };
    userGuid: string;
    fechaSancion: string;
    fechaFin: string;
    createdDate: string;
    updatedDate: string;
    isDeleted: boolean;
}

export const getSancionByGuidAdmin = async (guid: string): Promise<Sancion | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/sanciones/admin/${guid}`, {
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

export const actualizarSancion = async (guid: string, data: { tipoSancion: string }): Promise<Sancion | null> => {
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

export const descargarSancionesXLSX = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/excel/sanciones`, {
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
        link.setAttribute('download', `sanciones_${formattedDate}.xlsx`);

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el archivo Excel de sanciones', error);
        throw error;
    }
};

export const deleteSancion = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error("No autenticado");
        }

        const url = `http://localhost:8080/sanciones/delete/${guid}`;
        await axios.patch(url, {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error('Error obteniendo sancion por GUID:', error);
        return null;
    }
};

export const getSancionesByUserGuid = async(): Promise<Sancion[]> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error("No se encontró el token de autenticación.");
        return [];
    }

    try {
        const decodedToken = jwtDecode(token);
        const userEmail = decodedToken.sub;

        if (!userEmail) {
            console.error("No se encontró el email del usuario en el token.");
            return [];
        }

        const userResponse = await axios.get<UserData>(`http://localhost:8080/users/email/${userEmail}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const userGuid = userResponse.data.guid;

        const sancionesResponse = await axios.get<Sancion[]>(`http://localhost:8080/sanciones/user/${userGuid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return sancionesResponse.data || [];

    } catch (error: any) {
        console.error('Error obteniendo sanciones y/o guid:', error.response?.data || error.message);
        throw new Error(error.response?.data || error.message);
    }
};

export const getSancionesCountByUserGuid = async (): Promise<number> => {
    try {
        const sanciones = await getSancionesByUserGuid();
        return sanciones.length;
    } catch (error) {
        console.error("Error obteniendo el recuento de sanciones:", error);
        return 0;
    }
};