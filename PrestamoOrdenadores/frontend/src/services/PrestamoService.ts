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

export const getPrestamoByGuid = async (guid: string): Promise<Prestamo | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/prestamos/${guid}`, {
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

        const userResponse = await axios.get<UserData>(`http://localhost:8080/users/email/${userEmail}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const userGuid = userResponse.data.guid;

        const prestamosResponse = await axios.get<Prestamo[]>(`http://localhost:8080/prestamos/user/${userGuid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const resultados: Prestamo[] = [];

        for (const prestamo of prestamosResponse.data) {
            let dispositivo: Dispositivo | undefined = prestamo.dispositivo;

            if (!dispositivo && prestamo.dispositivoGuid) {
                try {
                    const dispositivoResponse = await axios.get<Dispositivo>(`http://localhost:8080/dispositivos/${prestamo.dispositivoGuid}`, {
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

export const descargarCsvPrestamos = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/csv/prestamos`, {
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
        link.setAttribute('download', `prestamos_${formattedDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el CSV de prestamos', error);
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
            'http://localhost:8080/prestamos',
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

        const response = await axios.get(`http://localhost:8080/prestamos/export/pdf/${guid}`, {
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

        const response = await axios.patch(`http://localhost:8080/prestamos/${guid}`, data, {
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

        const response = await axios.patch(`http://localhost:8080/prestamos/cancelar/${guid}`, {}, {
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

        const url = `http://localhost:8080/prestamos/delete/${guid}`;
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