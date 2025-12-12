let modalPrograma;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalPrograma = new bootstrap.Modal(document.getElementById('modalPrograma'));
    cargarFacultades();
    cargarProgramas();

    document.getElementById('modalPrograma').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formPrograma').reset();
        document.getElementById('programaId').value = '';
        editMode = false;
        document.getElementById('modalProgramaTitle').textContent = 'Nuevo Programa';
    });
});

async function cargarFacultades() {
    try {
        const facultades = await api.get('/catalogos/facultades');
        const select = document.getElementById('programaFacultadId');
        select.innerHTML = '<option value="">Seleccione una facultad</option>' +
            facultades.map(f => `<option value="${f.id}">${f.nombre}</option>`).join('');
    } catch (error) {
        console.error('Error cargando facultades:', error);
    }
}

async function cargarProgramas() {
    try {
        const programas = await api.get('/catalogos/programas');
        const tbody = document.getElementById('programasTable');
        
        if (programas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">No hay programas registrados</td></tr>';
            return;
        }

        tbody.innerHTML = programas.map(programa => `
            <tr>
                <td>${programa.id}</td>
                <td>${programa.nombre}</td>
                <td>${programa.facultadId || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarPrograma(${programa.id})">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarPrograma(${programa.id}, '${programa.nombre}')">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar los programas: ' + error.message, 'danger');
        document.getElementById('programasTable').innerHTML = '<tr><td colspan="4" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarPrograma(id) {
    try {
        const programa = await api.get(`/catalogos/programas/${id}`);
        document.getElementById('programaId').value = programa.id;
        document.getElementById('programaNombre').value = programa.nombre;
        document.getElementById('programaFacultadId').value = programa.facultadId;
        editMode = true;
        document.getElementById('modalProgramaTitle').textContent = 'Editar Programa';
        modalPrograma.show();
    } catch (error) {
        showAlert('Error al cargar el programa: ' + error.message, 'danger');
    }
}

async function guardarPrograma() {
    const id = document.getElementById('programaId').value;
    const nombre = document.getElementById('programaNombre').value.trim();
    const facultadId = document.getElementById('programaFacultadId').value;

    if (!nombre || !facultadId) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    try {
        if (editMode && id) {
            await api.put(`/catalogos/programas/${id}`, { nombre, facultadId: parseInt(facultadId) });
            showAlert('Programa actualizado correctamente', 'success');
        } else {
            await api.post('/catalogos/programas', { nombre, facultadId: parseInt(facultadId) });
            showAlert('Programa creado correctamente', 'success');
        }
        modalPrograma.hide();
        cargarProgramas();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}


async function eliminarPrograma(id, nombre) {

    mostrarConfirmacion(
        `¿Está seguro de eliminar el programa <b>"${nombre}"</b>?`,
        async () => {
            try {
                await api.delete(`/catalogos/programas/${id}`);
                showAlert('Programa eliminado correctamente', 'success');
                cargarProgramas();
            } catch (error) {
                showAlert('Error al eliminar: ' + error.message, 'danger');
            }
        }
    );
}


