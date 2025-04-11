import axios from 'axios';

interface User {
    guid: string;
    email: string;
    nombre: string;
    apellidos: string;
    curso: string;
    tutor: string;
    rol: string;
    numeroIdentificacion: string;
    fotoCarnet: string;
    lastLoginDate: string;
    lastPasswordResetDate: string;
    createdDate: string;
    updatedDate: string;
}

export const getUserByGuidAdmin = async (guid: string): Promise<User | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/users/admin/${guid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.data) {
            return null;
        }

        const user: User = {
            ...response.data,
        };

        return user;
    } catch (error) {
        console.error('Error obteniendo usuario por GUID:', error);
        return null;
    }
};

export const descargarCsvUsers = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`http://localhost:8080/storage/csv/users`, {
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
        link.setAttribute('download', `usuarios_${formattedDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el CSV de usuarios', error);
        throw error;
    }
}