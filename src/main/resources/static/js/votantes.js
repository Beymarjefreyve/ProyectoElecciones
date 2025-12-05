let modalVotante;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalVotante = new bootstrap.Modal(document.getElementById('modalVotante'));
    cargarVotantes();
    document.getElementById('modalVotante').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formVotante').reset();
        document.getElementById('votanteId').value = '';
        editMode = false;
        document.getElementById('modalVotanteTitle').textContent = 'Nuevo Votante';
    });
});

async function cargarVotantes() {
    try {
        const response = await fetch('/votantes');
        let votantes = await response.json();

        // Filtrar administradores (requisito del usuario)
        votantes = votantes.filter(v => v.rol !== 'ADMIN' && v.rol !== 'ADMINISTRATIVO');

        const tbody = document.getElementById('votantesTable');
        if (votantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay votantes registrados</td></tr>';
            return;
        }

        tbody.innerHTML = votantes.map(v => `
            <tr>
                <td>${v.id}</td>
                <td>${v.documento}</td>
                <td>${v.nombre}</td>
                <td>${v.email || '-'}</td>
                <td><span class="badge bg-info">${v.rol || 'ESTUDIANTE'}</span></td>
                <td><span class="badge bg-${v.estado === 'ACTIVO' ? 'success' : 'secondary'}">${v.estado || 'ACTIVO'}</span></td>
                <td>
                    <button class="btn btn-sm btn-warning" onclick="editarVotante(${v.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarVotante(${v.id}, '${v.nombre}')"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error(error);
        document.getElementById('votantesTable').innerHTML = '<tr><td colspan="7" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarVotante(id) {
    try {
        const votante = await api.get(`/votantes/${id}`);
        document.getElementById('votanteId').value = votante.id;
        document.getElementById('votanteDocumento').value = votante.documento;
        document.getElementById('votanteNombre').value = votante.nombre;
        editMode = true;
        document.getElementById('modalVotanteTitle').textContent = 'Editar Votante';
        modalVotante.show();
    } catch (error) {
        showAlert('Error al cargar el votante: ' + error.message, 'danger');
    }
}

async function guardarVotante() {
    const id = document.getElementById('votanteId').value;
    const documento = document.getElementById('votanteDocumento').value.trim();
    const nombre = document.getElementById('votanteNombre').value.trim();

    if (!documento || !nombre) {
        showAlert('Documento y nombre son obligatorios', 'warning');
        return;
    }

    try {
        if (editMode && id) {
            await api.put(`/votantes/${id}`, { documento, nombre });
            showAlert('Votante actualizado correctamente', 'success');
        } else {
            await api.post('/votantes', { documento, nombre });
            showAlert('Votante creado correctamente', 'success');
        }
        modalVotante.hide();
        cargarVotantes();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarVotante(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el votante "${nombre}"?`)) return;
    try {
        await api.delete(`/votantes/${id}`);
        showAlert('Votante eliminado correctamente', 'success');
        cargarVotantes();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

async function validarVoto() {
    const documento = document.getElementById('validarDocumento').value.trim();
    const eleccionId = document.getElementById('validarEleccionId').value;

    if (!documento || !eleccionId) {
        showAlert('Debe ingresar documento e ID de elección', 'warning');
        return;
    }

    try {
        const resultado = await api.get(`/votantes/validar-voto?documento=${documento}&eleccionId=${eleccionId}`);
        const mensaje = resultado.puedeVotar
            ? `✅ ${resultado.mensaje}`
            : `❌ ${resultado.mensaje}`;
        showAlert(mensaje, resultado.puedeVotar ? 'success' : 'warning');
    } catch (error) {
        showAlert('Error al validar: ' + error.message, 'danger');
    }
}

