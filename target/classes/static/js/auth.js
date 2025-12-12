/**
 * auth.js
 * Wrapper de compatibilidad para layout.js
 */

// Re-exportar funciones de layout.js para que otros scripts no se rompan
// Asegúrate de cargar layout.js ANTES que auth.js o cualquier otro script específico

// Estas funciones ya están disponibles globalmente por layout.js, 
// pero las definimos aquí por si algún script espera explícitamente auth.js

if (typeof isAdmin === 'undefined') {
    console.error('CRITICAL: layout.js must be loaded before auth.js');
}

// Funciones auxiliares específicas que podrían no estar en layout.js
function requireAdmin() {
    if (!isAdmin()) {
        window.location.href = '/estudiante-votacion.html';
        return false;
    }
    return true;
}

function requireEstudiante() {
    if (!isEstudiante()) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}
