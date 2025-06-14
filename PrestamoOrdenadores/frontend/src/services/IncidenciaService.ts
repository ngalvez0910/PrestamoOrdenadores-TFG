import axios from 'axios';
import {jwtDecode} from "jwt-decode";
import type {UserData} from "@/services/AuthService.ts";

export interface Incidencia {
    guid: string;
    asunto: string;
    descripcion: string;
    estado: string;
    user: { guid: string; numeroIdentificacion: string; };
    userGuid: string;
    createdDate: string;
    updatedDate: string;
    isDeleted: boolean;
}

export interface IncidenciaCreateRequest {
    asunto: string;
    descripcion: string;
}

export const getIncidenciaByGuidAdmin = async (guid: string): Promise<Incidencia | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`https://loantechoficial.onrender.com/incidencias/admin/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const incidencia: Incidencia = {
            ...response.data,
            user: response.data.user ? { numeroIdentificacion: response.data.user.numeroIdentificacion } : null,
            userGuid: response.data.user ? response.data.user.guid : null,
        };

        return incidencia;
    } catch (error) {
        console.error('Error obteniendo incidencia por GUID:', error);
        return null;
    }
};

export const getIncidenciasByUserGuid = async (): Promise<Incidencia[]> => {
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

        const userResponse = await axios.get<UserData>(`https://loantechoficial.onrender.com/users/email/${userEmail}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const userGuid = userResponse.data.guid;

        const incidenciasResponse = await axios.get<Incidencia[]>(`https://loantechoficial.onrender.com/incidencias/user/${userGuid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return incidenciasResponse.data || [];

    } catch (error: any) {
        console.error('Error obteniendo préstamos y/o número de serie:', error.response?.data || error.message);
        throw new Error(error.response?.data || error.message);
    }
};

export const actualizarIncidencia = async (guid: string, data: { estadoIncidencia: string }): Promise<Incidencia | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`https://loantechoficial.onrender.com/incidencias/${guid}`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar la incidencia:', error);
        return null;
    }
};

export const descargarIncidenciasXLSX = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`https://loantechoficial.onrender.com/storage/excel/incidencias`, {
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
        link.setAttribute('download', `incidencias_${formattedDate}.xlsx`);

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el archivo Excel de incidencias', error);
        throw error;
    }
};

export const createIncidencia = async (incidenciaData: IncidenciaCreateRequest): Promise<Incidencia | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('No se encontró el token de autenticación.');
            return null;
        }

        const decodedToken = jwtDecode(token);
        const userEmail = decodedToken.sub;

        if (!userEmail) {
            console.error('No se encontró el email del usuario en el token.');
            return null;
        }

        const userResponse = await axios.get<UserData>(`https://loantechoficial.onrender.com/users/email/${userEmail}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const userGuid = userResponse.data.guid;

        const response = await axios.post<Incidencia>(
            'https://loantechoficial.onrender.com/incidencias',
            {
                ...incidenciaData,
                userGuid: userGuid,
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );

        return response.data;
    } catch (error: any) {
        console.error('Error al crear la incidencia:', error.response?.data || error.message);
        return null;
    }
};

export const deleteIncidencia = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error("No autenticado");
        }

        const url = `https://loantechoficial.onrender.com/incidencias/delete/${guid}`;
        await axios.patch(url, {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error('Error obteniendo usuario por GUID:', error);
        return null;
    }
};

export const getIncidenciasCountByUserGuid = async (): Promise<number> => {
    try {
        const incidencias = await getIncidenciasByUserGuid();
        return incidencias.length;
    } catch (error) {
        console.error("Error obteniendo el recuento de incidencias:", error);
        return 0;
    }
};

export const descargarIncidenciaPDF = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("descargarIncidenciaPDF - No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`https://loantechoficial.onrender.com/incidencias/export/pdf/${guid}`, {
            responseType: 'blob',
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (response.status === 200) {
            const blob = new Blob([response.data], { type: 'application/pdf' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;

            const contentDisposition = response.headers['content-disposition'];
            let fileName = `incidencia_${guid}.pdf`;
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="([^"]+)"/);
                if (fileNameMatch && fileNameMatch[1]) {
                    fileName = fileNameMatch[1];
                }
            }
            link.download = fileName;

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            return null;
        } else {
            console.error('descargarIncidenciaPDF - Error al descargar el PDF:', response.data);
            throw new Error('No se pudo descargar el PDF de la incidencia.');
        }
    } catch (error: any) {
        console.error('descargarIncidenciaPDF - Error al descargar el PDF:', error.response?.data || error.message);
        throw error;
    }
};