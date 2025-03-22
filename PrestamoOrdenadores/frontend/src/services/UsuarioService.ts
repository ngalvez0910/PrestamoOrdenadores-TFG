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
        const response = await axios.get(`http://localhost:8080/users/admin/${guid}`);
        return response.data || null;
    } catch (error) {
        console.error('Error obteniendo usuario por GUID:', error);
        return null;
    }
};
