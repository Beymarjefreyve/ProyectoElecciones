let modalTipoEleccion;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalTipoEleccion = new bootstrap.Modal(document.getElementById('modalTipoEleccion'));
    cargarTiposEleccion();
    document.getElementById('modalTipoEleccion').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formTipoEleccion').reset();
        document.getElementById('tipoEleccionId').value = '';
        editMode = false;
        document.getElementById('modalTipoEleccionTitle').textContent = 'Nuevo Tipo de Elección';
    });
});

async function cargarTiposEleccion() {
    try {
        const tipos = await api.get('/catalogos/tipos-eleccion');
        const tbody = document.getElementById('tiposEleccionTable');
        if (tipos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay tipos de elección registrados</td></tr>';
            return;
        }
        tbody.innerHTML = tipos.map(tipo => `
            <tr><td>${tipo.id}</td><td>${tipo.nombre}</td>
            <td><button class="btn btn-sm btn-outline-primary" onclick="editarTipoEleccion(${tipo.id})"><i class="bi bi-pencil"></i> Editar</button>
            <button class="btn btn-sm btn-outline-danger" onclick="eliminarTipoEleccion(${tipo.id}, '${tipo.nombre}')"><i class="bi bi-trash"></i> Eliminar</button></td></tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar los tipos de elección: ' + error.message, 'danger');
        document.getElementById('tiposEleccionTable').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarTipoEleccion(id) {
    try {
        const tipo = await api.get(`/catalogos/tipos-eleccion/${id}`);
        document.getElementById('tipoEleccionId').value = tipo.id;
        document.getElementById('tipoEleccionNombre').value = tipo.nombre;
        editMode = true;
        document.getElementById('modalTipoEleccionTitle').textContent = 'Editar Tipo de Elección';
        modalTipoEleccion.show();
    } catch (error) {
        showAlert('Error al cargar el tipo de elección: ' + error.message, 'danger');
    }
}

async function guardarTipoEleccion() {
    const id = document.getElementById('tipoEleccionId').value;
    const nombre = document.getElementById('tipoEleccionNombre').value.trim();
    if (!nombre) {
        showAlert('El nombre es obligatorio', 'warning');
        return;
    }
    try {
        if (editMode && id) {
            await api.put(`/catalogos/tipos-eleccion/${id}`, { nombre });
            showAlert('Tipo de elección actualizado correctamente', 'success');
        } else {
            await api.post('/catalogos/tipos-eleccion', { nombre });
            showAlert('Tipo de elección creado correctamente', 'success');
        }
        modalTipoEleccion.hide();
        cargarTiposEleccion();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarTipoEleccion(id, nombre) {

    mostrarConfirmacion(
        `¿Está seguro de eliminar el tipo de elección <b>"${nombre}"</b>?`,
        async () => {
            try {
                await api.delete(`/catalogos/tipos-eleccion/${id}`);
                showAlert('Tipo de elección eliminado correctamente', 'success');
                cargarTiposEleccion();
            } catch (error) {
                showAlert('Error al eliminar: ' + error.message, 'danger');
            }
        }
    );
}


