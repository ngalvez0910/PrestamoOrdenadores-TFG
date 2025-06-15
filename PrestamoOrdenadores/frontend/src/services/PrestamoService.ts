import axios from 'axios';
import type { UserData } from "@/services/AuthService.ts";
import { jwtDecode } from "jwt-decode";

export interface Dispositivo {
    guid: string;
    numeroSerie: string;
}

export interface Prestamo {
    guid: string;
    dispositivo?: Dispositivo;
    dispositivoGuid?: string;
    user?: { guid: string; numeroIdentificacion: string; };
    userGuid?: string;
    estadoPrestamo: string;
    fechaPrestamo: string;
    fechaDevolucion: string;
    isDeleted: boolean;
}

const API_URL = "https://loantechoficial.onrender.com";

export const getPrestamoByGuid = async (guid: string): Promise<Prestamo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`${API_URL}/prestamos/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const prestamo: Prestamo = {
            ...response.data,
            user: response.data.user ? { numeroIdentificacion: response.data.user.numeroIdentificacion } : null,
            userGuid: response.data.user ? response.data.user.guid : null,
            dispositivo: response.data.dispositivo ? { numeroSerie: response.data.dispositivo.numeroSerie } : null,
            dispositivoGuid: response.data.dispositivo ? response.data.dispositivo.guid : null,
        };

        return prestamo;
    } catch (error) {
        console.error('Error obteniendo prestamo por GUID:', error);
        return null;
    }
};

export const getPrestamosByUserGuid = async (): Promise<Prestamo[]> => {
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

        const userResponse = await axios.get<UserData>(`${API_URL}/users/email/${userEmail}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const userGuid = userResponse.data.guid;

        const prestamosResponse = await axios.get<Prestamo[]>(`${API_URL}/prestamos/user/${userGuid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const resultados: Prestamo[] = [];

        for (const prestamo of prestamosResponse.data) {
            let dispositivo: Dispositivo | undefined = prestamo.dispositivo;

            if (!dispositivo && prestamo.dispositivoGuid) {
                try {
                    const dispositivoResponse = await axios.get<Dispositivo>(`${API_URL}/dispositivos/${prestamo.dispositivoGuid}`, {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    });
                    dispositivo = dispositivoResponse.data;
                } catch (error: any) {
                    console.error(`Error obteniendo el dispositivo con GUID ${prestamo.dispositivoGuid}:`, error.response?.data || error.message);
                }
            }

            resultados.push({
                guid: prestamo.guid,
                dispositivo,
                dispositivoGuid: prestamo.dispositivoGuid,
                user: prestamo.user,
                userGuid: prestamo.userGuid,
                estadoPrestamo: prestamo.estadoPrestamo,
                fechaPrestamo: prestamo.fechaPrestamo,
                fechaDevolucion: prestamo.fechaDevolucion,
                isDeleted: prestamo.isDeleted
            });
        }

        return resultados;

    } catch (error: any) {
        console.error('Error obteniendo préstamos y/o número de serie:', error.response?.data || error.message);
        throw new Error(error.response?.data || error.message);
    }
};

export const descargarPrestamosXLSX = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`${API_URL}/storage/excel/prestamos`, {
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
        link.setAttribute('download', `prestamos_${formattedDate}.xlsx`);

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el archivo Excel de préstamos', error);
        throw error;
    }
}

export const createPrestamo = async (): Promise<string | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.post(
            '${API_URL}/prestamos',
            {},
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (response.status === 201 && response.data?.guid) {
            console.log('Préstamo creado con éxito, GUID:', response.data.guid);
            return response.data.guid;
        } else {
            console.error('Error al realizar el préstamo:', response.data);
            return null;
        }
    } catch (error: any) {
        console.error('Error al realizar el préstamo:', error.response?.data || error.message);
        throw error;
    }
};

export const descargarPdfPrestamo = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("descargarPdfPrestamo - No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`${API_URL}/prestamos/export/pdf/${guid}`, {
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
            const today = new Date();
            const formattedDate = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}-${today.getDate().toString().padStart(2, '0')}`;
            link.download = `prestamo_${formattedDate}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } else {
            console.error('descargarPdfPrestamo - Error al descargar el PDF:', response.data);
            return null;
        }
    } catch (error: any) {
        console.error('descargarPdfPrestamo - Error al descargar el PDF:', error.response?.data || error.message);
        return null;
    }
};

export const actualizarPrestamo = async (guid: string, data: { estadoPrestamo: string }): Promise<Prestamo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`${API_URL}/prestamos/${guid}`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return response.data || null;
    } catch (error) {
        console.error('Error al actualizar el préstamo:', error);
        return null;
    }
};

export const cancelarPrestamo = async (guid: string): Promise<Prestamo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.patch(`${API_URL}/prestamos/cancelar/${guid}`, {}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        return response.data || null;
    } catch (error: any) {
        console.error('Error al cancelar el préstamo (servicio):', error.response?.data || error.message);
        throw error;
    }
};

export const deletePrestamo = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error("No autenticado");
        }

        const url = `${API_URL}/prestamos/delete/${guid}`;
        await axios.patch(url, {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error('Error obteniendo prestamo por GUID:', error);
        return null;
    }
};

export const getPrestamosCountByUserGuid = async (): Promise<number> => {
    try {
        const prestamos = await getPrestamosByUserGuid();
        return prestamos.length;
    } catch (error) {
        console.error("Error obteniendo el recuento de préstamos:", error);
        return 0;
    }
};