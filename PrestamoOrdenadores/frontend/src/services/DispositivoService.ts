import axios from 'axios';

const API_URL = "/dispositivos";

export default {
    async actualizarDispositivo(guid: string, data: { componentes: string; estado: string; incidenciaGuid: string }) {
        try {
            const response = await axios.patch(`${API_URL}/${guid}`, data);
            return response.data;
        } catch (error) {
            console.error("Error al actualizar el dispositivo:", error);
            throw error;
        }
    }
};
