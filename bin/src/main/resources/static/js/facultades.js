let modalFacultad;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalFacultad = new bootstrap.Modal(document.getElementById('modalFacultad'));
    cargarFacultades();

    // Limpiar formulario al cerrar modal
    document.getElementById('modalFacultad').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formFacultad').reset();
        document.getElementById('facultadId').value = '';
        editMode = false;
        document.getElementById('modalFacultadTitle').textContent = 'Nueva Facultad';
    });
});

async function cargarFacultades() {
    try {
        const facultades = await api.get('/catalogos/facultades');
        const tbody = document.getElementById('facultadesTable');
        
        if (facultades.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay facultades registradas</td></tr>';
            return;
        }

        tbody.innerHTML = facultades.map(facultad => `
            <tr>
                <td>${facultad.id}</td>
                <td>${facultad.nombre}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarFacultad(${facultad.id})">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarFacultad(${facultad.id}, '${facultad.nombre}')">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar las facultades: ' + error.message, 'danger');
        document.getElementById('facultadesTable').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarFacultad(id) {
    try {
        const facultad = await api.get(`/catalogos/facultades/${id}`);
        document.getElementById('facultadId').value = facultad.id;
        document.getElementById('facultadNombre').value = facultad.nombre;
        editMode = true;
        document.getElementById('modalFacultadTitle').textContent = 'Editar Facultad';
        modalFacultad.show();
    } catch (error) {
        showAlert('Error al cargar la facultad: ' + error.message, 'danger');
    }
}

async function guardarFacultad() {
    const id = document.getElementById('facultadId').value;
    const nombre = document.getElementById('facultadNombre').value.trim();

    if (!nombre) {
        showAlert('El nombre es obligatorio', 'warning');
        return;
    }

    try {
        if (editMode && id) {
            await api.put(`/catalogos/facultades/${id}`, { nombre });
            showAlert('Facultad actualizada correctamente', 'success');
        } else {
            await api.post('/catalogos/facultades', { nombre });
            showAlert('Facultad creada correctamente', 'success');
        }
        modalFacultad.hide();
        cargarFacultades();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarFacultad(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar la facultad "${nombre}"?`)) {
        return;
    }

    try {
        await api.delete(`/catalogos/facultades/${id}`);
        showAlert('Facultad eliminada correctamente', 'success');
        cargarFacultades();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

