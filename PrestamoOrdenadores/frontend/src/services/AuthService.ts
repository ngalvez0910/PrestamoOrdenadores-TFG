import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { reactive } from "vue";

export interface UserData {
    guid: string;
    email: string;
    nombre: string;
    avatarUrl: string;
    rol: string;
}

interface AuthState {
    user: UserData | null;
    token: string | null;
}

export interface UserPasswordResetRequest {
    currentPassword: string;
    newPassword: string;
}

const state = reactive<AuthState>({
    user: null,
    token: localStorage.getItem("token"),
});

export const authService = {
    async register(userData: any): Promise<string | null> {
        try {
            const response = await axios.post("http://localhost:8080/auth/signup", userData);
            const token = response.data.token;
            if (token) {
                this.setToken(token);
                await this.fetchUser();
                return token;
            }
            return null;
        } catch (error) {
            console.error("Error al registrar el usuario:", error);
            throw error;
        }
    },

    setToken(token: string): void {
        state.token = token;
        localStorage.setItem("token", token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    },

    async logout(): Promise<void> {
        state.user = null;
        state.token = null;
        localStorage.removeItem("token");
    },

    async fetchUser(): Promise<void> {
        if (state.token) {
            try {
                const decodedToken: any = jwtDecode(state.token);
                const email = decodedToken.sub;
                const response = await axios.get(`http://localhost:8080/users/email/${email}`);
                state.user = response.data;
            } catch (error) {
                console.error("Error al obtener datos del usuario:", error);
                await this.logout();
            }
        }
    },

    async changePassword(payload: UserPasswordResetRequest): Promise<void> {
        const token = state.token;

        if (!token) {
            console.error("Intento de cambiar contraseña sin token en el estado del servicio.");
            throw new Error("Acción no permitida. Debes iniciar sesión.");
        }

        if (!state.user || !state.user.guid) {
            console.warn("Datos de usuario (GUID) no disponibles en el estado. Intentando recuperarlos...");
            await this.fetchUser();
            if (!state.user || !state.user.guid) {
                console.error("No se pudieron obtener los datos del usuario (GUID) después del intento de recuperación.");
                throw new Error("No se pudo obtener la información del usuario para cambiar la contraseña.");
            }
        }

        const guid = state.user.guid;

        try {
            const response = await axios.patch(
                `http://localhost:8080/users/${guid}`,
                payload,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            console.log('Contraseña cambiada con éxito en el backend:', response.data);
        } catch (error) {
            console.error("Error en authService.changePassword (manual header):", error);
            throw error;
        }
    },

    get user(): UserData | null {
        return state.user;
    },

    get token(): string | null {
        return state.token;
    },

    isAuthenticated(): boolean {
        return !!state.token;
    }
};

if (authService.token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${authService.token}`;
    authService.fetchUser();
}