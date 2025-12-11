let modalSede;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalSede = new bootstrap.Modal(document.getElementById('modalSede'));
    cargarSedes();
    document.getElementById('modalSede').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formSede').reset();
        document.getElementById('sedeId').value = '';
        editMode = false;
        document.getElementById('modalSedeTitle').textContent = 'Nueva Sede';
    });
});

async function cargarSedes() {
    try {
        const sedes = await api.get('/catalogos/sedes');
        const tbody = document.getElementById('sedesTable');
        if (sedes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay sedes registradas</td></tr>';
            return;
        }
        tbody.innerHTML = sedes.map(sede => `
            <tr><td>${sede.id}</td><td>${sede.nombre}</td>
            <td><button class="btn btn-sm btn-outline-primary" onclick="editarSede(${sede.id})"><i class="bi bi-pencil"></i> Editar</button>
            <button class="btn btn-sm btn-outline-danger" onclick="eliminarSede(${sede.id}, '${sede.nombre}')"><i class="bi bi-trash"></i> Eliminar</button></td></tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar las sedes: ' + error.message, 'danger');
        document.getElementById('sedesTable').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarSede(id) {
    try {
        const sede = await api.get(`/catalogos/sedes/${id}`);
        document.getElementById('sedeId').value = sede.id;
        document.getElementById('sedeNombre').value = sede.nombre;
        editMode = true;
        document.getElementById('modalSedeTitle').textContent = 'Editar Sede';
        modalSede.show();
    } catch (error) {
        showAlert('Error al cargar la sede: ' + error.message, 'danger');
    }
}

async function guardarSede() {
    const id = document.getElementById('sedeId').value;
    const nombre = document.getElementById('sedeNombre').value.trim();
    if (!nombre) {
        showAlert('El nombre es obligatorio', 'warning');
        return;
    }
    try {
        if (editMode && id) {
            await api.put(`/catalogos/sedes/${id}`, { nombre });
            showAlert('Sede actualizada correctamente', 'success');
        } else {
            await api.post('/catalogos/sedes', { nombre });
            showAlert('Sede creada correctamente', 'success');
        }
        modalSede.hide();
        cargarSedes();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarSede(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar la sede "${nombre}"?`)) return;
    try {
        await api.delete(`/catalogos/sedes/${id}`);
        showAlert('Sede eliminada correctamente', 'success');
        cargarSedes();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

