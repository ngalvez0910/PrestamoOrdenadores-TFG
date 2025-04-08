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
