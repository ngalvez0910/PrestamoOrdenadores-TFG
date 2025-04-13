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