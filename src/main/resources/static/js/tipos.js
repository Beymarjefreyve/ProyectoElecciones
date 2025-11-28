let modalTipo;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalTipo = new bootstrap.Modal(document.getElementById('modalTipo'));
    cargarTipos();
    document.getElementById('modalTipo').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formTipo').reset();
        document.getElementById('tipoId').value = '';
        editMode = false;
        document.getElementById('modalTipoTitle').textContent = 'Nuevo Tipo';
    });
});

async function cargarTipos() {
    try {
        const tipos = await api.get('/catalogos/tipos');
        const tbody = document.getElementById('tiposTable');
        if (tipos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay tipos registrados</td></tr>';
            return;
        }
        tbody.innerHTML = tipos.map(tipo => `
            <tr><td>${tipo.id}</td><td>${tipo.nombre}</td>
            <td><button class="btn btn-sm btn-outline-primary" onclick="editarTipo(${tipo.id})"><i class="bi bi-pencil"></i> Editar</button>
            <button class="btn btn-sm btn-outline-danger" onclick="eliminarTipo(${tipo.id}, '${tipo.nombre}')"><i class="bi bi-trash"></i> Eliminar</button></td></tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar los tipos: ' + error.message, 'danger');
        document.getElementById('tiposTable').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarTipo(id) {
    try {
        const tipo = await api.get(`/catalogos/tipos/${id}`);
        document.getElementById('tipoId').value = tipo.id;
        document.getElementById('tipoNombre').value = tipo.nombre;
        editMode = true;
        document.getElementById('modalTipoTitle').textContent = 'Editar Tipo';
        modalTipo.show();
    } catch (error) {
        showAlert('Error al cargar el tipo: ' + error.message, 'danger');
    }
}

async function guardarTipo() {
    const id = document.getElementById('tipoId').value;
    const nombre = document.getElementById('tipoNombre').value.trim();
    if (!nombre) {
        showAlert('El nombre es obligatorio', 'warning');
        return;
    }
    try {
        if (editMode && id) {
            await api.put(`/catalogos/tipos/${id}`, { nombre });
            showAlert('Tipo actualizado correctamente', 'success');
        } else {
            await api.post('/catalogos/tipos', { nombre });
            showAlert('Tipo creado correctamente', 'success');
        }
        modalTipo.hide();
        cargarTipos();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarTipo(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el tipo "${nombre}"?`)) return;
    try {
        await api.delete(`/catalogos/tipos/${id}`);
        showAlert('Tipo eliminado correctamente', 'success');
        cargarTipos();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

