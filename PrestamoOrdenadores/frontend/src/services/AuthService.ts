import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { reactive } from "vue";

export interface UserData {
    guid: string;
    email: string;
    nombre: string;
    avatar: string;
    rol: string;
}

interface AuthState {
    user: UserData | null;
    token: string | null;
    roleFromToken: string | null;
}

export interface UserPasswordResetRequest {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}

const savedUser = localStorage.getItem("user");
const parsedUser = savedUser ? JSON.parse(savedUser) : null;
const savedToken = localStorage.getItem("token");

const state = reactive<AuthState>({
    user: parsedUser,
    token: savedToken,
    roleFromToken: null
});

if (savedToken) {
    try {
        const decoded: any = jwtDecode(savedToken);
        state.roleFromToken = decoded.rol || null;
        console.log("[AuthService Init] Role from token set:", state.roleFromToken);
    } catch (e) {
        console.error("[AuthService Init] Failed to decode token:", e);
    }
}

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
        try {
            const decoded: any = jwtDecode(token);
            state.roleFromToken = decoded.rol || null;
            console.log("[AuthService] Role from token set in state:", state.roleFromToken);
        } catch (e) {
            console.error("[AuthService] Failed to decode token in setToken:", e);
            state.roleFromToken = null;
        }
    },

    async logout(): Promise<void> {
        state.user = null;
        state.token = null;
        state.roleFromToken = null;
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        delete axios.defaults.headers.common['Authorization'];
    },

    async fetchUser(): Promise<void> {
        if (state.token) {
            try {
                console.log("[AuthService:fetchUser] Intentando. Token (primeros 20 chars):", state.token ? state.token.substring(0, 20) + "..." : "TOKEN NULO/VACÍO");
                const decodedToken: any = jwtDecode(state.token);
                console.log("[AuthService:fetchUser] Token decodificado:", decodedToken);
                const email = decodedToken.sub;
                console.log("[AuthService:fetchUser] Email extraído:", email);

                if (!email) {
                    console.error("[AuthService:fetchUser] Email no encontrado en token (decodedToken.sub es nulo o undefined). Token completo:", state.token);
                    await this.logout();
                    throw new Error("Email no encontrado en el token.");
                }

                const response = await axios.get(`http://localhost:8080/users/email/${email}`);
                state.user = response.data;

                localStorage.setItem("user", JSON.stringify(state.user));

                console.log("[AuthService:fetchUser] Usuario obtenido y asignado:", state.user);

                if (state.user && state.user.rol) {
                    state.roleFromToken = state.user.rol;
                    console.log("[AuthService:fetchUser] Role sincronizado desde datos de usuario:", state.roleFromToken);
                }
            } catch (error: any) {
                console.error("Error DETALLADO al obtener datos del usuario (desde fetchUser):", error);
                if (error.response) {
                    console.error("[AuthService:fetchUser] Status de error:", error.response.status);
                    console.error("[AuthService:fetchUser] Data de error:", error.response.data);
                } else if (error.request) {
                    console.error("[AuthService:fetchUser] Error en la solicitud (sin respuesta):", error.request);
                } else {
                    console.error("[AuthService:fetchUser] Otro error (ej. jwtDecode):", error.message);
                }
                await this.logout();
                throw error;
            }
        } else {
            console.warn("[AuthService:fetchUser] No se ejecutó fetchUser porque state.token es nulo.");
        }
    },

    async changePassword(passwordData: UserPasswordResetRequest): Promise<void | null> {
        const tokenFromLocalStorage = localStorage.getItem('token');
        console.log("[AuthService:changePassword] Iniciando. Token leído de localStorage:", tokenFromLocalStorage);
        console.log("[AuthService:changePassword] Iniciando. Valor de state.token ANTES de posible sync:", state.token);

        if (tokenFromLocalStorage && state.token !== tokenFromLocalStorage) {
            console.warn("[AuthService:changePassword] Detectada desincronización. Sincronizando state.token desde localStorage.");
            this.setToken(tokenFromLocalStorage);
        }
        console.log("[AuthService:changePassword] Iniciando. Valor de state.token DESPUÉS de posible sync:", state.token);

        const token = state.token;
        if (!token) {
            console.error("No se encontró el token de autenticación (changePassword).");
            throw new Error("No hay sesión activa.");
        }

        if (!state.user) {
            try {
                await this.fetchUser();
            } catch (error) {
                console.error("Error al obtener usuario (desde changePassword):", error);
                throw new Error("No se pudo obtener información del usuario para el cambio de contraseña.");
            }
        }

        if (!state.user) {
            throw new Error("Información del usuario no disponible tras intento de carga.");
        }

        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

        try {
            const guid = state.user.guid;

            await axios.patch(
                `http://localhost:8080/users/password/${guid}`,
                passwordData
            );

            return;
        } catch (error: any) {
            console.error("Error al cambiar la contraseña (llamada PATCH):", error);
            if (error.response) {
                console.error("[AuthService:changePassword] PATCH Error Response Status:", error.response.status);
                console.error("[AuthService:changePassword] PATCH Error Response Data:", error.response.data);
                if (error.response.status === 403) {
                    throw new Error("Contraseña actual incorrecta o no tienes permisos suficientes.");
                } else if (error.response.status === 404) {
                    throw new Error("Usuario no encontrado.");
                } else {
                    const errorMsg = error.response.data?.message || error.response.data || "Error al cambiar la contraseña.";
                    throw new Error(typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg));
                }
            } else if (error.request) {
                console.error("[AuthService:changePassword] PATCH Error Request Data:", error.request);
                throw new Error("Error de red o el servidor no respondió al intentar cambiar la contraseña.");
            }
            else {
                console.error("[AuthService:changePassword] PATCH Otro error:", error.message);
                throw new Error("Error inesperado al cambiar la contraseña.");
            }
        }
    },

    async updateUserAvatar(avatarUrl: string): Promise<void> {
        if (!state.user) {
            console.error("[AuthService:updateUserAvatar] No hay usuario en el estado para actualizar el avatar");
            throw new Error("No hay usuario en el estado para actualizar el avatar");
        }

        try {
            state.user.avatar = avatarUrl;

            localStorage.setItem("user", JSON.stringify(state.user));

            console.log("[AuthService:updateUserAvatar] Avatar actualizado con éxito:", avatarUrl);
        } catch (error) {
            console.error("[AuthService:updateUserAvatar] Error al actualizar el avatar en el estado:", error);
            throw error;
        }
    },

    isTokenValid(): boolean {
        if (!state.token) return false;

        try {
            const decoded: any = jwtDecode(state.token);
            const currentTime = Date.now() / 1000;

            if (decoded.exp && decoded.exp < currentTime) {
                console.log("[AuthService] Token expirado");
                return false;
            }

            return true;
        } catch (error) {
            console.error("[AuthService] Error al verificar validez del token:", error);
            return false;
        }
    },

    get user(): UserData | null {
        return state.user;
    },

    get token(): string | null {
        return state.token;
    },

    get role(): string | null {
        return state.roleFromToken || (state.user ? state.user.rol : null);
    },

    isAuthenticated(): boolean {
        return !!state.token && this.isTokenValid();
    },

    isAdmin(): boolean {
        return this.role === 'ADMIN';
    },

    syncFromStorage(): void {
        const storedToken = localStorage.getItem("token");
        if (storedToken && storedToken !== state.token) {
            this.setToken(storedToken);
        }

        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            try {
                state.user = JSON.parse(storedUser);
                if (state.user && state.user.rol) {
                    state.roleFromToken = state.user.rol;
                }
            } catch (e) {
                console.error("[AuthService] Error parsing stored user:", e);
            }
        }
    },

    async refreshAuth(): Promise<boolean> {
        try {
            if (!this.isAuthenticated()) {
                return false;
            }

            if (!state.user && state.token) {
                await this.fetchUser();
            }

            return this.isAuthenticated();
        } catch (error) {
            console.error("[AuthService] Error refreshing auth:", error);
            await this.logout();
            return false;
        }
    }
};

if (authService.token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${authService.token}`;

    if (!state.user) {
        authService.fetchUser().catch(error => {
            console.error("Error inicializando el usuario:", error);
            authService.logout();
        });
    }
}