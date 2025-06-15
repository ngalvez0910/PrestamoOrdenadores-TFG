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
    isDeleted: boolean;
    isOlvidado: boolean;
}

interface UserAvatarUpdateRequest {
    avatar: string;
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

        const response = await axios.get(`https://loantechoficial.onrender.com/users/admin/${guid}`, {
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

export const descargarUsersXLSX = async (): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("No se encontró el token de autenticación.");
            return null;
        }

        const response = await axios.get(`https://loantechoficial.onrender.com/storage/excel/users`, {
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
        link.setAttribute('download', `usuarios_${formattedDate}.xlsx`);

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('Error al descargar el archivo Excel de usuarios', error);
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
            `https://loantechoficial.onrender.com/users/avatar/${guid}`,
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

        const response = await axios.put(`https://loantechoficial.onrender.com/users/${guid}`, payload, {
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
};

export const derechoAlOlvido = async (userGuid: string): Promise<void> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('Intento de actualizar usuario sin token.');
        throw new Error('No autenticado o token no encontrado.');
    }

    try {
        await axios.delete(`https://loantechoficial.onrender.com/users/derechoOlvido/${userGuid}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        console.log(`Derecho al Olvido ejecutado con éxito para el usuario: ${userGuid}`);

    } catch (error) {
        console.error(`Error al ejecutar Derecho al Olvido para el usuario ${userGuid}:`, error);

        const errorMessage = axios.isAxiosError(error) && error.response?.data?.error
            ? error.response.data.error
            : (axios.isAxiosError(error) ? error.message : 'Error desconocido al conectar con el servidor.');

        throw new Error(errorMessage);
    }
};

export const deleteUser = async (guid: string): Promise<void | null> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error("No autenticado");
        }

        const url = `https://loantechoficial.onrender.com/users/delete/${guid}`;
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