import axios from 'axios';

export interface BackupInfo {
    name: string;
    [key: string]: any;
}

const API_URL = "https://loantechoficial.onrender.com";


export const createBackup = async (): Promise<string | null> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error("No se encontró el token de autenticación.");
        return null;
    }

    try {
        console.log('SERVICE: Solicitando creación de backup...');
        const response = await axios.get(`${API_URL}/backup/create`, {
            headers: {
                Authorization: `Bearer ${token}`
            },
            responseType: 'blob'
        });
        if (!(response.data instanceof Blob) || response.data.size === 0) {
            console.error('SERVICE: La respuesta no es un archivo Blob válido o está vacío.');
            throw new Error('No se recibieron datos válidos para el archivo de backup.');
        }
        let filename = 'db_backup_' + Date.now() + '.sql';
        const disposition = response.headers['content-disposition'];
        if (disposition) {
            const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            const matches = filenameRegex.exec(disposition);
            if (matches != null && matches[1]) {
                filename = matches[1].replace(/['"]/g, '');
            }
        }
        console.log(`SERVICE: Descargando archivo como: ${filename}`);

        const url = window.URL.createObjectURL(response.data);

        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();

        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        return filename;
    } catch (error) {
        console.error('SERVICE: Error al crear/descargar la copia de seguridad:', error);
        if (axios.isAxiosError(error) && error.response?.data instanceof Blob && error.response?.headers['content-type']?.includes('application/json')) {
            try {
                const errorJson = JSON.parse(await error.response.data.text());
                throw new Error(`Error al crear backup (${error.response.status}): ${errorJson.message || errorJson.error || 'Error desconocido del servidor'}`);
            }catch (parseError) {
            }
        }
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(`Error al crear backup (${error.response.status}): ${error.message}`);
        } else if (error instanceof Error) {
            throw error;
        }
        throw new Error('Error inesperado al crear/descargar la copia de seguridad.');
    }
};

export const listBackups = async (): Promise<BackupInfo[] | null> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error("No se encontró el token de autenticación.");
        return null;
    }

    try {
        console.log('SERVICE: Solicitando lista de backups...');
        const response = await axios.get<BackupInfo[]>(`${API_URL}/backup/list`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        console.log('SERVICE: Backups recibidos:', response.data);
        return response.data || [];
    } catch (error) {
        console.error('SERVICE: Error al listar las copias de seguridad:', error);
        if (axios.isAxiosError(error) && error.response) {
            if (error.response.status === 422) {
                throw new Error('Error al obtener la lista de backups (422).');
            }
            throw new Error(`Error al listar backups: ${error.response.data || error.message}`);
        }
        throw new Error('Error al obtener la lista de copias de seguridad.');
    }
};

export const restoreBackup = async (fileName: string): Promise<string | null> => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error("No se encontró el token de autenticación.");
        return null;
    }

    if (!fileName) {
        console.error('SERVICE: Intento de restaurar sin nombre de archivo.');
        throw new Error('No se ha especificado ningún archivo para restaurar.');
    }
    try {
        console.log(`SERVICE: Solicitando restauración desde: ${fileName}`);
        const response = await axios.post<string>(
            `${API_URL}/backup/restore`,
            null,
            {
                params: { fileName },
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
        console.log('SERVICE: Respuesta de restauración:', response.data);
        return response.data || `Restauración desde ${fileName} realizada con éxito.`;
    } catch (error) {
        console.error(`SERVICE: Error al restaurar desde ${fileName}:`, error);
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(`Error al restaurar (${fileName}): ${error.response.data || error.message}`);
        }
        throw new Error(`Error al intentar restaurar desde ${fileName}.`);
    }
};