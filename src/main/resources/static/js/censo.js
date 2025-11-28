document.addEventListener('DOMContentLoaded', () => {
    cargarElecciones();
});

async function cargarElecciones() {
    try {
        const elecciones = await api.get('/elecciones');
        const select = document.getElementById('selectEleccion');
        const selectModal = document.getElementById('censoEleccionId');
        const options = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e => `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'}</option>`).join('');
        select.innerHTML = options;
        selectModal.innerHTML = options;
    } catch (error) {
        console.error('Error cargando elecciones:', error);
    }
}

async function cargarCenso() {
    const eleccionId = document.getElementById('selectEleccion').value;
    if (!eleccionId) {
        document.getElementById('censoTable').innerHTML = '<tr><td colspan="5" class="text-center">Seleccione una elección</td></tr>';
        return;
    }

    try {
        const censo = await api.get(`/elecciones/${eleccionId}/censo`);
        const tbody = document.getElementById('censoTable');
        if (censo.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No hay votantes en el censo para esta elección</td></tr>';
            return;
        }
        tbody.innerHTML = censo.map(item => `
            <tr>
                <td>${item.id}</td>
                <td>${item.votanteId}</td>
                <td>${item.votanteNombre || '-'}</td>
                <td>${item.votanteDocumento || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarDelCenso(${item.votanteId}, ${eleccionId})">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar el censo: ' + error.message, 'danger');
        document.getElementById('censoTable').innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function agregarAlCenso() {
    const eleccionId = document.getElementById('censoEleccionId').value;
    const votanteId = document.getElementById('censoVotanteId').value;

    if (!eleccionId || !votanteId) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    try {
        await api.post('/api/censo', { eleccionId: parseInt(eleccionId), votanteId: parseInt(votanteId) });
        showAlert('Votante agregado al censo correctamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalCenso')).hide();
        cargarCenso();
    } catch (error) {
        showAlert('Error al agregar: ' + error.message, 'danger');
    }
}

async function cargaMasiva() {
    const eleccionId = document.getElementById('cargaMasivaEleccionId').value;
    const votantesIdsStr = document.getElementById('cargaMasivaVotantesIds').value.trim();

    if (!eleccionId || !votantesIdsStr) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    const votantesIds = votantesIdsStr.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));

    if (votantesIds.length === 0) {
        showAlert('Debe ingresar al menos un ID de votante válido', 'warning');
        return;
    }

    try {
        const resultado = await api.post('/api/censo/carga-masiva', {
            eleccionId: parseInt(eleccionId),
            votantesIds
        });
        showAlert(`Carga masiva completada: ${resultado.exitosos || 0} exitosos, ${resultado.fallidos || 0} fallidos`, 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalCargaMasiva')).hide();
        cargarCenso();
    } catch (error) {
        showAlert('Error en carga masiva: ' + error.message, 'danger');
    }
}

async function eliminacionMasiva() {
    const eleccionId = document.getElementById('eliminacionMasivaEleccionId').value;
    const votantesIdsStr = document.getElementById('eliminacionMasivaVotantesIds').value.trim();

    if (!eleccionId || !votantesIdsStr) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    if (!confirm('¿Está seguro de eliminar estos votantes del censo?')) return;

    const votantesIds = votantesIdsStr.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));

    if (votantesIds.length === 0) {
        showAlert('Debe ingresar al menos un ID de votante válido', 'warning');
        return;
    }

    try {
        const resultado = await api.delete('/api/censo/eliminacion-masiva', {}, {
            eleccionId: parseInt(eleccionId),
            votantesIds
        });
        showAlert(`Eliminación masiva completada: ${resultado.exitosos || 0} exitosos, ${resultado.fallidos || 0} fallidos`, 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalEliminacionMasiva')).hide();
        cargarCenso();
    } catch (error) {
        showAlert('Error en eliminación masiva: ' + error.message, 'danger');
    }
}

async function eliminarDelCenso(votanteId, eleccionId) {
    if (!confirm('¿Está seguro de eliminar este votante del censo?')) return;
    try {
        await api.delete(`/api/censo?votanteId=${votanteId}&eleccionId=${eleccionId}`);
        showAlert('Votante eliminado del censo correctamente', 'success');
        cargarCenso();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

