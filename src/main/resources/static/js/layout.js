/**
 * layout.js - Sistema Centralizado de Layout y Control de Acceso (RBAC)
 * 
 * ÚNICA fuente de verdad para:
 * 1. Control de permisos por rol (ADMIN vs ESTUDIANTE)
 * 2. Generación de menú lateral (Sidebar) 
 * 3. Protección de rutas
 * 4. Etiquetas de usuario
 */

// =====================================================
// 1. CONFIGURACIÓN DE ROLES Y RUTAS
// =====================================================

const ROLES = {
    ADMIN: ['ADMINISTRATIVO', 'ADMIN'],
    ESTUDIANTE: ['ESTUDIANTE']
};

const RUTAS = {
    PUBLICAS: [
        '/landing.html',
        '/login-admin.html',
        '/login-estudiante.html',
        '/registro-estudiante.html',
        '/verificacion-pendiente.html',
        '/verificacion-exitosa.html',
        '/test-connection.html'
    ],
    ADMIN: [
        '/index.html',
        '/candidatos.html',
        '/procesos.html',
        '/elecciones.html',
        '/inscripciones.html',
        '/votantes.html',
        '/censo.html',
        '/solicitudes.html',
        '/resultados.html',
        '/catalogos/facultades.html',
        '/catalogos/programas.html',
        '/catalogos/sedes.html',
        '/catalogos/tipos.html',
        '/catalogos/tipos-eleccion.html',
        '/catalogos/tipos-solicitud.html'
    ],
    ESTUDIANTE: [
        '/estudiante-votacion.html',
        '/votacion.html'
    ]
};

// MENÚ ÚNICO - El sidebar se genera desde aquí
const MENU_ITEMS = [
    { label: 'Dashboard', icon: 'bi-house-door', href: '/index.html', roles: ['ADMIN'] },
    { label: 'Facultades', icon: 'bi-building', href: '/catalogos/facultades.html', roles: ['ADMIN'] },
    { label: 'Programas', icon: 'bi-book', href: '/catalogos/programas.html', roles: ['ADMIN'] },
    { label: 'Sedes', icon: 'bi-geo-alt', href: '/catalogos/sedes.html', roles: ['ADMIN'] },
    { label: 'Tipos', icon: 'bi-tags', href: '/catalogos/tipos.html', roles: ['ADMIN'] },
    { label: 'Tipos Elección', icon: 'bi-ui-radios', href: '/catalogos/tipos-eleccion.html', roles: ['ADMIN'] },
    { label: 'Tipos Solicitud', icon: 'bi-file-text', href: '/catalogos/tipos-solicitud.html', roles: ['ADMIN'] },
    { label: 'Procesos', icon: 'bi-calendar-event', href: '/procesos.html', roles: ['ADMIN'] },
    { label: 'Elecciones', icon: 'bi-clipboard-check', href: '/elecciones.html', roles: ['ADMIN'] },
    { label: 'Inscripciones', icon: 'bi-person-check', href: '/inscripciones.html', roles: ['ADMIN'] },
    { label: 'Candidatos', icon: 'bi-person-badge', href: '/candidatos.html', roles: ['ADMIN'] },
    { label: 'Votantes', icon: 'bi-people', href: '/votantes.html', roles: ['ADMIN'] },
    { label: 'Censo', icon: 'bi-list-check', href: '/censo.html', roles: ['ADMIN'] },
    { label: 'Solicitudes', icon: 'bi-envelope-paper', href: '/solicitudes.html', roles: ['ADMIN'] },
    { label: 'Resultados', icon: 'bi-bar-chart', href: '/resultados.html', roles: ['ADMIN'] },
    // ESTUDIANTE - NUNCA visible para admin
    { label: 'Votar', icon: 'bi-check-circle', href: '/estudiante-votacion.html', roles: ['ESTUDIANTE'] }
];

// =====================================================
// 2. GESTIÓN DE SESIÓN Y ROLES
// =====================================================

function getUsuario() {
    const userStr = localStorage.getItem('usuario');
    return userStr ? JSON.parse(userStr) : null;
}

function getRol() {
    return localStorage.getItem('rol') || '';
}

function isAdmin() {
    const rol = getRol();
    return ROLES.ADMIN.includes(rol);
}

function isEstudiante() {
    const rol = getRol();
    return ROLES.ESTUDIANTE.includes(rol);
}

function getEtiquetaRol() {
    if (isAdmin()) return 'Administrador';
    if (isEstudiante()) return 'Estudiante';
    return 'Usuario';
}

function logout() {
    localStorage.clear();
    window.location.href = '/landing.html';
}

// =====================================================
// 3. PROTECCIÓN DE RUTAS
// =====================================================

function verificarAcceso() {
    const path = window.location.pathname;

    // Permitir rutas públicas
    if (RUTAS.PUBLICAS.some(r => path.endsWith(r)) || path === '/' || path === '') return true;

    const usuario = getUsuario();
    if (!usuario) {
        window.location.href = '/landing.html';
        return false;
    }

    // ADMIN NO PUEDE VOTAR - Regla #1
    if (isAdmin()) {
        if (RUTAS.ESTUDIANTE.some(r => path.endsWith(r))) {
            console.warn('Acceso denegado: Admin intentando acceder a ruta de estudiante');
            window.location.href = '/index.html';
            return false;
        }
    }

    // Estudiante no puede entrar a rutas de admin
    if (isEstudiante()) {
        if (RUTAS.ADMIN.some(r => path.endsWith(r))) {
            console.warn('Acceso denegado: Estudiante intentando acceder a ruta de admin');
            window.location.href = '/estudiante-votacion.html';
            return false;
        }
    }

    return true;
}

function requireAdmin() {
    if (!getUsuario()) {
        window.location.href = '/landing.html';
        return false;
    }
    if (!isAdmin()) {
        window.location.href = '/estudiante-votacion.html';
        return false;
    }
    return true;
}

function requireEstudiante() {
    if (!getUsuario()) {
        window.location.href = '/landing.html';
        return false;
    }
    if (!isEstudiante()) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// =====================================================
// 4. GENERACIÓN DE UI
// =====================================================

function generarNavbar() {
    const usuario = getUsuario();
    const nombre = usuario ? usuario.nombre : 'Usuario';
    const etiqueta = getEtiquetaRol();
    const badgeClass = isAdmin() ? 'bg-danger' : 'bg-success';

    return `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${isAdmin() ? '/index.html' : '/estudiante-votacion.html'}">
                <i class="bi bi-ballot"></i> Sistema de Elecciones
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                        <span class="badge ${badgeClass} me-2">${etiqueta}</span>
                    </li>
                    <li class="nav-item">
                        <span class="text-light me-3">${nombre}</span>
                    </li>
                    <li class="nav-item">
                        <button class="btn btn-outline-light btn-sm" onclick="logout()">
                            <i class="bi bi-box-arrow-right"></i> Salir
                        </button>
                    </li>
                </ul>
            </div>
        </div>
    </nav>`;
}

function generarSidebar() {
    const path = window.location.pathname;
    const rolActual = isAdmin() ? 'ADMIN' : (isEstudiante() ? 'ESTUDIANTE' : '');

    const itemsHtml = MENU_ITEMS
        .filter(item => item.roles.includes(rolActual))
        .map(item => {
            const active = path.endsWith(item.href) ? 'active' : '';
            return `
            <li class="nav-item">
                <a class="nav-link ${active}" href="${item.href}">
                    <i class="bi ${item.icon} me-2"></i>${item.label}
                </a>
            </li>`;
        }).join('');

    return `
    <nav id="sidebarMenu" class="sidebar d-lg-block collapse">
        <div class="position-sticky pt-3">
            <div class="sidebar-heading px-3 mb-2 text-uppercase fw-bold small">
                <i class="bi bi-menu-button-wide me-1"></i> Menú
            </div>
            <ul class="nav flex-column">
                ${itemsHtml}
            </ul>
        </div>
    </nav>`;
}

// =====================================================
// 5. APLICAR VISIBILIDAD POR ROL
// =====================================================

function aplicarVisibilidadRol() {
    const esAdmin = isAdmin();

    // Ocultar elementos admin-only si no es admin
    document.querySelectorAll('.admin-only').forEach(el => {
        el.style.display = esAdmin ? '' : 'none';
    });

    // Ocultar elementos estudiante-only si es admin
    document.querySelectorAll('.estudiante-only').forEach(el => {
        el.style.display = esAdmin ? 'none' : '';
    });

    // REGLA #1: Eliminar cualquier enlace de votación para admins
    if (esAdmin) {
        document.querySelectorAll('a[href*="votacion"], a[href*="votar"], .btn-votar, .link-votar').forEach(el => {
            el.remove();
        });
    }
}

// =====================================================
// 6. INICIALIZACIÓN
// =====================================================

document.addEventListener('DOMContentLoaded', () => {
    // Verificar acceso
    if (!verificarAcceso()) return;

    // Inyectar Navbar
    const navContainer = document.getElementById('navbar-container');
    if (navContainer) {
        navContainer.innerHTML = generarNavbar();
    }

    // Inyectar Sidebar
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
        sidebarContainer.innerHTML = generarSidebar();
    }

    // Aplicar visibilidad por rol
    aplicarVisibilidadRol();
});
