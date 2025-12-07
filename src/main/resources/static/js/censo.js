/**
 * censo.js - Gestión del Censo Electoral
 * El censo vincula votantes (estudiantes) con elecciones específicas
 * Solo los votantes en el censo pueden votar en una elección
 */

document.addEventListener('DOMContentLoaded', () => {
    cargarEleccionesParaCenso();
    cargarVotantesParaCenso();
});

/**
 * Cargar elecciones en ambos selects (filtro principal y modal)
 */
async function cargarEleccionesParaCenso() {
    try {
        const elecciones = await api.get('/elecciones');
        const options = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e => `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'} [${e.estado}]</option>`).join('');

        const selectFiltro = document.getElementById('selectEleccionCenso');
        if (selectFiltro) selectFiltro.innerHTML = options;

        const selectModal = document.getElementById('censoEleccionId');
        if (selectModal) selectModal.innerHTML = options;

        const selectCargaMasiva = document.getElementById('cargaMasivaEleccionId');
        if (selectCargaMasiva) selectCargaMasiva.innerHTML = options;
    } catch (error) {
        console.error('Error cargando elecciones:', error);
        showAlert('Error al cargar elecciones: ' + error.message, 'danger');
    }
}

/**
 * Cargar votantes para el select del modal (solo estudiantes)
 */
async function cargarVotantesParaCenso() {
    try {
        const votantes = await api.get('/votantes');
        // Filtrar solo estudiantes (excluir ADMIN y ADMINISTRATIVO)
        const estudiantes = votantes.filter(v => v.rol !== 'ADMIN' && v.rol !== 'ADMINISTRATIVO');

        const select = document.getElementById('censoVotanteId');
        if (select) {
            select.innerHTML = '<option value="">Seleccione un votante</option>' +
                estudiantes.map(v => `<option value="${v.id}">${v.documento} - ${v.nombre}</option>`).join('');
        }
    } catch (error) {
        console.error('Error cargando votantes:', error);
    }
}

/**
 * Cargar el censo de la elección seleccionada
 * Endpoint: GET /elecciones/{eleccionId}/censo
 */
async function cargarCenso() {
    const eleccionId = document.getElementById('selectEleccionCenso').value;
    const tbody = document.getElementById('censoTable');

    if (!eleccionId) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Seleccione una elección para ver su censo</td></tr>';
        return;
    }

    tbody.innerHTML = '<tr><td colspan="5" class="text-center"><div class="spinner-border spinner-border-sm"></div> Cargando...</td></tr>';

    try {
        // Endpoint correcto según CensoController: GET /elecciones/{eleccionId}/censo
        const censo = await api.get(`/elecciones/${eleccionId}/censo`);

        if (!censo || censo.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay votantes registrados en el censo de esta elección</td></tr>';
            return;
        }

        tbody.innerHTML = censo.map(item => `
            <tr>
                <td>${item.id || '-'}</td>
                <td>${item.votanteId || '-'}</td>
                <td>${item.votanteNombre || item.nombre || '-'}</td>
                <td>${item.votanteDocumento || item.documento || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarDelCenso(${item.votanteId}, ${eleccionId})">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');

        showAlert(`Censo cargado: ${censo.length} votantes`, 'info');
    } catch (error) {
        console.error('Error al cargar censo:', error);
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error al cargar datos</td></tr>';
        showAlert('Error al cargar el censo: ' + error.message, 'danger');
    }
}

/**
 * Abrir modal para agregar votante al censo
 */
function abrirModalCenso() {
    const eleccionId = document.getElementById('selectEleccionCenso').value;

    // Pre-seleccionar la elección actual en el modal
    if (eleccionId) {
        const selectModal = document.getElementById('censoEleccionId');
        if (selectModal) selectModal.value = eleccionId;
    }

    const modal = document.getElementById('modalCenso');
    if (modal) {
        new bootstrap.Modal(modal).show();
    }
}

/**
 * Guardar votante en el censo (agregar)
 * Endpoint: POST /api/censo
 */
async function guardarCenso() {
    const eleccionId = document.getElementById('censoEleccionId').value;
    const votanteId = document.getElementById('censoVotanteId').value;

    if (!eleccionId || !votanteId) {
        showAlert('Debe seleccionar elección y votante', 'warning');
        return;
    }

    try {
        // Endpoint: POST /api/censo
        await api.post('/api/censo', {
            eleccionId: parseInt(eleccionId),
            votanteId: parseInt(votanteId)
        });
        showAlert('Votante agregado al censo correctamente', 'success');

        // Cerrar modal
        const modal = document.getElementById('modalCenso');
        if (modal) {
            bootstrap.Modal.getInstance(modal)?.hide();
        }

        // Actualizar el select de filtro y recargar censo
        const selectFiltro = document.getElementById('selectEleccionCenso');
        if (selectFiltro) selectFiltro.value = eleccionId;
        cargarCenso();
    } catch (error) {
        showAlert('Error al agregar al censo: ' + error.message, 'danger');
    }
}

/**
 * Eliminar votante del censo
 * Endpoint: DELETE /api/censo?votanteId=X&eleccionId=Y
 */
async function eliminarDelCenso(votanteId, eleccionId) {
    if (!confirm('¿Está seguro de eliminar este votante del censo?')) return;

    try {
        await api.delete(`/api/censo?votanteId=${votanteId}&eleccionId=${eleccionId}`);
        showAlert('Votante eliminado del censo correctamente', 'success');
        cargarCenso();
    } catch (error) {
        showAlert('Error al eliminar del censo: ' + error.message, 'danger');
    }
}

/**
 * Carga masiva de votantes al censo
 * Endpoint: POST /api/censo/carga-masiva
 */
async function cargaMasiva() {
    const eleccionId = document.getElementById('cargaMasivaEleccionId').value;
    const votantesIdsStr = document.getElementById('cargaMasivaVotantesIds').value.trim();

    if (!eleccionId) {
        showAlert('Debe seleccionar una elección', 'warning');
        return;
    }

    if (!votantesIdsStr) {
        showAlert('Debe ingresar los IDs de votantes', 'warning');
        return;
    }

    // Parsear IDs separados por comas o saltos de línea
    const votanteIds = votantesIdsStr
        .split(/[,\n]/)
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id) && id > 0);

    if (votanteIds.length === 0) {
        showAlert('No se encontraron IDs de votantes válidos', 'warning');
        return;
    }

    try {
        const response = await api.post('/api/censo/carga-masiva', {
            eleccionId: parseInt(eleccionId),
            votanteIds: votanteIds
        });

        showAlert(`Carga masiva completada: ${response.exitosos || votanteIds.length} agregados, ${response.fallidos || 0} fallidos`, 'success');

        // Limpiar y recargar
        document.getElementById('cargaMasivaVotantesIds').value = '';
        const selectFiltro = document.getElementById('selectEleccionCenso');
        if (selectFiltro) selectFiltro.value = eleccionId;
        cargarCenso();
    } catch (error) {
        showAlert('Error en carga masiva: ' + error.message, 'danger');
    }
}

/**
 * Limpiar formulario del modal
 */
function limpiarFormularioCenso() {
    document.getElementById('censoEleccionId').value = '';
    document.getElementById('censoVotanteId').value = '';
}
