/**
 * layout.js - Sistema Centralizado de Layout y Control de Acceso (RBAC)
 * 
 * Este archivo es la ÚNICA fuente de verdad para:
 * 1. Generación del menú lateral (Sidebar) y superior (Navbar).
 * 2. Control de permisos por rol (ADMIN vs ESTUDIANTE).
 * 3. Protección de rutas y redirecciones.
 * 4. Etiquetas de usuario.
 */

// =====================================================
// 1. CONFIGURACIÓN
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

const MENU_ITEMS = [
    { label: 'Dashboard', icon: 'bi-house-door', href: '/index.html', roles: ['ADMIN'] },
    { label: 'Facultades', icon: 'bi-building', href: '/catalogos/facultades.html', roles: ['ADMIN'] },
    { label: 'Programas', icon: 'bi-book', href: '/catalogos/programas.html', roles: ['ADMIN'] },
    { label: 'Sedes', icon: 'bi-geo-alt', href: '/catalogos/sedes.html', roles: ['ADMIN'] },
    { label: 'Tipos', icon: 'bi-tags', href: '/catalogos/tipos.html', roles: ['ADMIN'] },
    { label: 'Tipos Elección', icon: 'bi-ballot-check', href: '/catalogos/tipos-eleccion.html', roles: ['ADMIN'] },
    { label: 'Tipos Solicitud', icon: 'bi-file-text', href: '/catalogos/tipos-solicitud.html', roles: ['ADMIN'] },
    { label: 'Procesos', icon: 'bi-calendar-event', href: '/procesos.html', roles: ['ADMIN'] },
    { label: 'Elecciones', icon: 'bi-clipboard-check', href: '/elecciones.html', roles: ['ADMIN'] },
    { label: 'Inscripciones', icon: 'bi-person-check', href: '/inscripciones.html', roles: ['ADMIN'] },
    { label: 'Candidatos', icon: 'bi-person-badge', href: '/candidatos.html', roles: ['ADMIN'] },
    { label: 'Votantes', icon: 'bi-people', href: '/votantes.html', roles: ['ADMIN'] },
    { label: 'Censo', icon: 'bi-list-check', href: '/censo.html', roles: ['ADMIN'] },
    { label: 'Solicitudes', icon: 'bi-envelope-paper', href: '/solicitudes.html', roles: ['ADMIN'] },
    { label: 'Votar', icon: 'bi-check-circle', href: '/estudiante-votacion.html', roles: ['ESTUDIANTE'] },
    { label: 'Resultados', icon: 'bi-bar-chart', href: '/resultados.html', roles: ['ADMIN'] }
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

    // Protección estricta: Admin no puede entrar a rutas de estudiante
    if (isAdmin()) {
        if (RUTAS.ESTUDIANTE.some(r => path.endsWith(r))) {
            console.warn('Acceso denegado: Admin intentando acceder a ruta de estudiante');
            window.location.href = '/index.html';
            return false;
        }
    }

    // Protección estricta: Estudiante no puede entrar a rutas de admin
    if (isEstudiante()) {
        if (RUTAS.ADMIN.some(r => path.endsWith(r))) {
            console.warn('Acceso denegado: Estudiante intentando acceder a ruta de admin');
            window.location.href = '/estudiante-votacion.html';
            return false;
        }
    }

    return true;
}

// =====================================================
// 4. GENERACIÓN DE UI (NAVBAR Y SIDEBAR)
// =====================================================

function generarNavbar() {
    const usuario = getUsuario();
    const nombre = usuario ? usuario.nombre : 'Usuario';
    const etiqueta = getEtiquetaRol();
    const badgeClass = isAdmin() ? 'bg-danger' : 'bg-success'; // Diferenciar visualmente

    return `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top navbar-top">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${isAdmin() ? '/index.html' : '/estudiante-votacion.html'}">
                <i class="bi bi-ballot"></i> Sistema de Elecciones
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="d-flex align-items-center d-none d-lg-flex">
                <span class="badge ${badgeClass} me-2">${etiqueta}</span>
                <span class="text-light me-3 small">${nombre}</span>
                <button class="btn btn-outline-light btn-sm" onclick="logout()">
                    <i class="bi bi-box-arrow-right"></i> Salir
                </button>
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
            // Fix para rutas relativas vs absolutas
            const href = item.href;
            return `
            <li class="nav-item">
                <a class="nav-link ${active}" href="${href}">
                    <i class="bi ${item.icon}"></i> ${item.label}
                </a>
            </li>`;
        }).join('');

    return `
    <nav id="sidebarMenu" class="col-lg-2 d-lg-block sidebar collapse bg-white">
        <div class="position-sticky pt-3">
            <div class="navbar-brand px-3 mb-3 text-muted text-uppercase small fw-bold">
                Menú Principal
            </div>
            <ul class="nav flex-column">
                ${itemsHtml}
            </ul>
            
            <!-- Botón de salir visible en móvil dentro del menú -->
            <div class="d-lg-none px-3 mt-4 border-top pt-3">
                <div class="d-flex align-items-center mb-3">
                    <span class="badge bg-secondary me-2">${getEtiquetaRol()}</span>
                    <small>${getUsuario()?.nombre}</small>
                </div>
                <button class="btn btn-outline-danger w-100 btn-sm" onclick="logout()">
                    <i class="bi bi-box-arrow-right"></i> Cerrar Sesión
                </button>
            </div>
        </div>
    </nav>`;
}

// =====================================================
// 5. INICIALIZACIÓN
// =====================================================

document.addEventListener('DOMContentLoaded', () => {
    if (!verificarAcceso()) return;

    // Inyectar Navbar
    const navContainer = document.getElementById('navbar-container');
    if (navContainer) navContainer.innerHTML = generarNavbar();

    // Inyectar Sidebar
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) sidebarContainer.innerHTML = generarSidebar();

    // Eliminar elementos "Votar" residuales si existen en el DOM por error
    if (isAdmin()) {
        document.querySelectorAll('.btn-votar, .link-votar, a[href*="votacion"]').forEach(el => el.remove());
    }
});
