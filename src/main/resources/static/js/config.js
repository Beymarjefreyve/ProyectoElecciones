/**
 * config.js - API Configuration and Utilities
 * Configuracion centralizada para peticiones HTTP
 */

const API_BASE_URL = '';

/**
 * Funcion base para peticiones HTTP con manejo robusto de errores
 * - Evita cache con cache: 'no-store'
 * - Maneja respuestas vacias (204 No Content)
 * - Parsea JSON o texto segun corresponda
 */
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        cache: 'no-store',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    };

    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: { ...defaultOptions.headers, ...(options.headers || {}) }
    };

    console.log(`[API] ${finalOptions.method} ${url}`);

    const response = await fetch(API_BASE_URL + url, finalOptions);

    // Leer respuesta como texto primero (para manejar vacio o no-JSON)
    const text = await response.text();
    let data = null;

    if (text && text.trim()) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text; // Si no es JSON, devolver como texto
        }
    }

    if (!response.ok) {
        const msg = (data && (data.mensaje || data.message || data.error)) || `Error ${response.status}`;
        console.error(`[API] Error: ${msg}`);
        throw new Error(msg);
    }

    console.log(`[API] OK:`, data);
    return data;
}

/**
 * Objeto API con metodos simplificados
 */
const api = {
    get(url) {
        return apiRequest(url, { method: 'GET' });
    },

    post(url, data) {
        return apiRequest(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    put(url, data) {
        return apiRequest(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    delete(url) {
        return apiRequest(url, { method: 'DELETE' });
    },

    patch(url, params = {}) {
        // PATCH usa query params, no body (segun el backend actual)
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        return apiRequest(fullUrl, { method: 'PATCH' });
    },

    getText(url) {
        return fetch(API_BASE_URL + url, {
            method: 'GET',
            cache: 'no-store',
            headers: { 'Accept': 'text/plain' }
        }).then(r => {
            if (!r.ok) throw new Error(`Error ${r.status}`);
            return r.text();
        });
    }
};

// =====================================================
// UTILIDADES DE FORMATO
// =====================================================

function formatDate(dateString) {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('es-ES');
}

function formatDateTime(dateString) {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('es-ES');
}

// =====================================================
// SISTEMA DE ALERTAS
// =====================================================

function showAlert(message, type = 'info') {
    let container = document.getElementById('alertContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'alertContainer';
        container.style.cssText = 'position: fixed; top: 70px; right: 20px; z-index: 9999; max-width: 400px;';
        document.body.appendChild(container);
    }

    const alertId = 'alert-' + Date.now();
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show shadow" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', alertHtml);

    // Auto-cerrar despues de 4 segundos
    setTimeout(() => {
        const alertEl = document.getElementById(alertId);
        if (alertEl) {
            alertEl.classList.remove('show');
            setTimeout(() => alertEl.remove(), 150);
        }
    }, 4000);
}

// =====================================================
// FUNCION BASE PARA LIMPIAR FORMULARIOS
// =====================================================

function limpiarFormulario() {
    // Sobrescrita por cada modulo especifico
    console.log('limpiarFormulario base llamada');
}
