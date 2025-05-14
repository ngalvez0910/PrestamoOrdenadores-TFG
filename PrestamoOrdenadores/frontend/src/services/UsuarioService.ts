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
    avatar: string;
    lastLoginDate: string;
    lastPasswordResetDate: string;
    isActivo: boolean;
    createdDate: string;
    updatedDate: string;
}

interface UserAvatarUpdateRequest {
    avatar: string;
}

interface UserIsActivoUpdateRequest {
    isActivo: boolean;
}

export interface UserUpdateRequest {
    rol?: UserRole | string;
    isActivo?: boolean;
}

type UserRole = 'ADMIN' | 'USER' | 'PROFESOR';

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
};

export const updateAvatar = async (guid: string, avatar: string): Promise<void> => {
    const token = localStorage.getItem("token");
    if (!token) {
        throw new Error("No autenticado");
    }
    if (!guid) {
        throw new Error("GUID de usuario no encontrado");
    }

    const requestBody: UserAvatarUpdateRequest = {
        avatar: avatar
    };

    try {
        const response = await axios.patch(
            `http://localhost:8080/users/avatar/${guid}`,
            requestBody,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
            }
        );

        console.log("Avatar actualizado en backend:", response.data)
    } catch (error: any) {
        console.error("Error al actualizar el avatar en el backend:", error);
        const errorMessage = error.response?.data || error.message || "Error desconocido al actualizar avatar.";
        throw new Error(errorMessage);
    }
};

export const actualizarUsuario = async (guid: string, payload: UserUpdateRequest): Promise<any> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('Intento de actualizar usuario sin token.');
        throw new Error('No autenticado o token no encontrado.');
    }
    try {
        console.log(`[UsuarioService] Actualizando usuario ${guid} con payload:`, payload);

        const response = await axios.put(`http://localhost:8080/users/${guid}`, payload, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        console.log(`[UsuarioService] Usuario ${guid} actualizado, respuesta:`, response.data);
        return response.data;
    } catch (error) {
        console.error(`[UsuarioService] Error al actualizar usuario ${guid}:`, error);
        throw error;
    }
}