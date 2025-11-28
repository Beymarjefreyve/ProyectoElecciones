// Configuración de la API
const API_BASE_URL = 'http://localhost:8080';

// Utilidad para hacer peticiones HTTP
const api = {
    async get(url) {
        try {
            console.log(`[API] GET: ${API_BASE_URL}${url}`);
            const response = await fetch(`${API_BASE_URL}${url}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                }
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error(`[API] Error ${response.status}:`, errorText);
                throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
            }
            
            const data = await response.json();
            console.log(`[API] Response:`, data);
            return data;
        } catch (error) {
            console.error('[API] Error en GET:', error);
            if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté corriendo en http://localhost:8080');
            }
            throw error;
        }
    },
    async getText(url) {
        try {
            console.log(`[API] GET (text): ${API_BASE_URL}${url}`);
            const response = await fetch(`${API_BASE_URL}${url}`, {
                method: 'GET',
                headers: {
                    'Accept': 'text/plain, text/csv, */*'
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error(`[API] Error ${response.status}:`, errorText);
                throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
            }

            return await response.text();
        } catch (error) {
            console.error('[API] Error en GET (text):', error);
            if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté corriendo en http://localhost:8080');
            }
            throw error;
        }
    },

    async post(url, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${url}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error ${response.status}: ${errorText}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error en POST:', error);
            throw error;
        }
    },

    async put(url, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${url}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error ${response.status}: ${errorText}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error en PUT:', error);
            throw error;
        }
    },

    async delete(url, params = {}, body = null) {
        try {
            let fullUrl = url;
            if (Object.keys(params).length > 0) {
                const queryString = new URLSearchParams(params).toString();
                fullUrl = `${url}?${queryString}`;
            }
            const options = {
                method: 'DELETE'
            };
            if (body) {
                options.headers = {
                    'Content-Type': 'application/json'
                };
                options.body = JSON.stringify(body);
            }
            const response = await fetch(`${API_BASE_URL}${fullUrl}`, options);
            if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);
            return response.status === 204 ? null : await response.json();
        } catch (error) {
            console.error('Error en DELETE:', error);
            throw error;
        }
    },

    async patch(url, params = {}) {
        try {
            const queryString = new URLSearchParams(params).toString();
            const fullUrl = queryString ? `${url}?${queryString}` : url;
            const response = await fetch(`${API_BASE_URL}${fullUrl}`, {
                method: 'PATCH'
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error ${response.status}: ${errorText}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error en PATCH:', error);
            throw error;
        }
    }
};

// Utilidades para mostrar mensajes
const showAlert = (message, type = 'info') => {
    // Limpiar alertas anteriores del mismo tipo
    const existingAlerts = document.querySelectorAll('.alert.position-fixed');
    existingAlerts.forEach(alert => alert.remove());
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';
    alertDiv.style.maxWidth = '500px';
    alertDiv.innerHTML = `
        <strong>${type === 'danger' ? 'Error' : type === 'success' ? 'Éxito' : type === 'warning' ? 'Advertencia' : 'Información'}:</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(alertDiv);
    
    // Auto-remover después de 5 segundos
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
};

// Formatear fechas
const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
};

const formatDateTime = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
};

