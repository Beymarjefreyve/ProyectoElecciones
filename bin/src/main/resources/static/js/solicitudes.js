document.addEventListener('DOMContentLoaded', () => {
    cargarCatalogos();
    cargarSolicitudes();
});

async function cargarCatalogos() {
    try {
        const [tiposSolicitud, programas, sedes] = await Promise.all([
            api.get('/catalogos/tipos-solicitud'),
            api.get('/catalogos/programas'),
            api.get('/catalogos/sedes')
        ]);

        llenarSelect('solicitudTipoSolicitudId', tiposSolicitud, 'Seleccione tipo');
        llenarSelect('solicitudProgramaId', programas, 'Seleccione programa');
        llenarSelect('solicitudSedeId', sedes, 'Seleccione sede');
    } catch (error) {
        console.error('Error cargando catálogos:', error);
    }
}

function llenarSelect(id, items, placeholder) {
    const select = document.getElementById(id);
    select.innerHTML = `<option value="">${placeholder}</option>` +
        items.map(item => `<option value="${item.id}">${item.nombre}</option>`).join('');
}

async function cargarSolicitudes() {
    try {
        const solicitudes = await api.get('/solicitudes');
        mostrarSolicitudes(solicitudes);
    } catch (error) {
        showAlert('Error al cargar las solicitudes: ' + error.message, 'danger');
        document.getElementById('solicitudesTable').innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function filtrarPorEstado() {
    const estado = document.getElementById('filtroEstado').value;
    if (!estado) {
        cargarSolicitudes();
        return;
    }
    try {
        const solicitudes = await api.get(`/solicitudes/filtro/estado?estado=${estado}`);
        mostrarSolicitudes(solicitudes);
    } catch (error) {
        showAlert('Error al filtrar: ' + error.message, 'danger');
    }
}

function mostrarSolicitudes(solicitudes) {
    const tbody = document.getElementById('solicitudesTable');
    if (solicitudes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay solicitudes registradas</td></tr>';
        return;
    }
    tbody.innerHTML = solicitudes.map(solicitud => {
        const estadoBadge = {
            'PENDIENTE': 'bg-warning',
            'APROBADO': 'bg-success',
            'RECHAZADO': 'bg-danger'
        }[solicitud.estado] || 'bg-secondary';
        
        return `
            <tr>
                <td>${solicitud.id}</td>
                <td>${solicitud.documento || '-'}</td>
                <td>${solicitud.nombre || '-'}</td>
                <td><span class="badge ${estadoBadge}">${solicitud.estado || '-'}</span></td>
                <td>${formatDate(solicitud.fechaSolicitud)}</td>
                <td>
                    <button class="btn btn-sm btn-outline-success" onclick="aprobarSolicitud(${solicitud.id})" ${solicitud.estado === 'APROBADO' ? 'disabled' : ''}>
                        <i class="bi bi-check"></i> Aprobar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="rechazarSolicitud(${solicitud.id})" ${solicitud.estado === 'RECHAZADO' ? 'disabled' : ''}>
                        <i class="bi bi-x"></i> Rechazar
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

async function guardarSolicitud() {
    const documento = document.getElementById('solicitudDocumento').value.trim();
    const nombre = document.getElementById('solicitudNombre').value.trim();
    const tipoSolicitudId = document.getElementById('solicitudTipoSolicitudId').value ? parseInt(document.getElementById('solicitudTipoSolicitudId').value) : null;
    const programaId = document.getElementById('solicitudProgramaId').value ? parseInt(document.getElementById('solicitudProgramaId').value) : null;
    const sedeId = document.getElementById('solicitudSedeId').value ? parseInt(document.getElementById('solicitudSedeId').value) : null;
    const anio = document.getElementById('solicitudAnio').value ? parseInt(document.getElementById('solicitudAnio').value) : null;
    const semestre = document.getElementById('solicitudSemestre').value ? parseInt(document.getElementById('solicitudSemestre').value) : null;
    const email = document.getElementById('solicitudEmail').value.trim();

    if (!documento || !nombre || !tipoSolicitudId) {
        showAlert('Documento, nombre y tipo de solicitud son obligatorios', 'warning');
        return;
    }

    try {
        await api.post('/solicitudes', {
            documento,
            nombre,
            tipoSolicitudId,
            programaId,
            sedeId,
            anio,
            semestre,
            email: email || null
        });
        showAlert('Solicitud creada correctamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalSolicitud')).hide();
        document.getElementById('formSolicitud').reset();
        cargarSolicitudes();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function aprobarSolicitud(id) {
    if (!confirm('¿Está seguro de aprobar esta solicitud?')) return;
    try {
        await api.patch(`/solicitudes/${id}/aprobar`);
        showAlert('Solicitud aprobada correctamente', 'success');
        cargarSolicitudes();
    } catch (error) {
        showAlert('Error al aprobar: ' + error.message, 'danger');
    }
}

async function rechazarSolicitud(id) {
    if (!confirm('¿Está seguro de rechazar esta solicitud?')) return;
    try {
        await api.patch(`/solicitudes/${id}/rechazar`);
        showAlert('Solicitud rechazada correctamente', 'success');
        cargarSolicitudes();
    } catch (error) {
        showAlert('Error al rechazar: ' + error.message, 'danger');
    }
}

