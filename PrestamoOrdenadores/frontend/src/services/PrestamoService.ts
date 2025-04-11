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
    user?: { guid: string; };
    userGuid?: string;
    estadoPrestamo: string;
    fechaPrestamo: string;
    fechaDevolucion: string;
}

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
            });        }

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