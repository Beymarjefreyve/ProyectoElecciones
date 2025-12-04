let modalTipoSolicitud;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalTipoSolicitud = new bootstrap.Modal(document.getElementById('modalTipoSolicitud'));
    cargarTiposSolicitud();
    document.getElementById('modalTipoSolicitud').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formTipoSolicitud').reset();
        document.getElementById('tipoSolicitudId').value = '';
        editMode = false;
        document.getElementById('modalTipoSolicitudTitle').textContent = 'Nuevo Tipo de Solicitud';
    });
});

async function cargarTiposSolicitud() {
    try {
        const tipos = await api.get('/catalogos/tipos-solicitud');
        const tbody = document.getElementById('tiposSolicitudTable');
        if (tipos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay tipos de solicitud registrados</td></tr>';
            return;
        }
        tbody.innerHTML = tipos.map(tipo => `
            <tr><td>${tipo.id}</td><td>${tipo.nombre}</td>
            <td><button class="btn btn-sm btn-outline-primary" onclick="editarTipoSolicitud(${tipo.id})"><i class="bi bi-pencil"></i> Editar</button>
            <button class="btn btn-sm btn-outline-danger" onclick="eliminarTipoSolicitud(${tipo.id}, '${tipo.nombre}')"><i class="bi bi-trash"></i> Eliminar</button></td></tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar los tipos de solicitud: ' + error.message, 'danger');
        document.getElementById('tiposSolicitudTable').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarTipoSolicitud(id) {
    try {
        const tipo = await api.get(`/catalogos/tipos-solicitud/${id}`);
        document.getElementById('tipoSolicitudId').value = tipo.id;
        document.getElementById('tipoSolicitudNombre').value = tipo.nombre;
        editMode = true;
        document.getElementById('modalTipoSolicitudTitle').textContent = 'Editar Tipo de Solicitud';
        modalTipoSolicitud.show();
    } catch (error) {
        showAlert('Error al cargar el tipo de solicitud: ' + error.message, 'danger');
    }
}

async function guardarTipoSolicitud() {
    const id = document.getElementById('tipoSolicitudId').value;
    const nombre = document.getElementById('tipoSolicitudNombre').value.trim();
    if (!nombre) {
        showAlert('El nombre es obligatorio', 'warning');
        return;
    }
    try {
        if (editMode && id) {
            await api.put(`/catalogos/tipos-solicitud/${id}`, { nombre });
            showAlert('Tipo de solicitud actualizado correctamente', 'success');
        } else {
            await api.post('/catalogos/tipos-solicitud', { nombre });
            showAlert('Tipo de solicitud creado correctamente', 'success');
        }
        modalTipoSolicitud.hide();
        cargarTiposSolicitud();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarTipoSolicitud(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el tipo de solicitud "${nombre}"?`)) return;
    try {
        await api.delete(`/catalogos/tipos-solicitud/${id}`);
        showAlert('Tipo de solicitud eliminado correctamente', 'success');
        cargarTiposSolicitud();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

