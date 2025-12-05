// Configuración de la API - ruta vacía para mismo origen
const API_BASE_URL = '';

// Función para hacer peticiones HTTP con manejo de errores
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    };

    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: { ...defaultOptions.headers, ...options.headers }
    };

    const response = await fetch(url, finalOptions);

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({ mensaje: 'Error del servidor' }));
        throw new Error(errorData.mensaje || `Error ${response.status}`);
    }

    return response.json();
}

// Utilidades de API simplificadas
const api = {
    async get(url) {
        return apiRequest(url, { method: 'GET' });
    },

    async post(url, data) {
        return apiRequest(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    async put(url, data) {
        return apiRequest(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    async delete(url) {
        const response = await fetch(url, { method: 'DELETE' });
        if (!response.ok) throw new Error(`Error ${response.status}`);
        return response.status === 204 ? null : response.json();
    },

    async patch(url, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        return apiRequest(fullUrl, { method: 'PATCH' });
    }
};

// Formatear fechas
const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('es-ES');
};

const formatDateTime = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('es-ES');
};
