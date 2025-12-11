let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalProceso = new bootstrap.Modal(document.getElementById('modalProceso'));
    cargarProcesos();
    document.getElementById('modalProceso').addEventListener('hidden.bs.modal', () => {
        limpiarFormulario();
    });
});

async function cargarProcesos() {
    try {
        const procesos = await api.get('/procesos');
        mostrarProcesos(procesos);
    } catch (error) {
        showAlert('Error al cargar los procesos: ' + error.message, 'danger');
        document.getElementById('procesosTable').innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

function mostrarProcesos(procesos) {
    const tbody = document.getElementById('procesosTable');

    if (procesos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No hay procesos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = procesos.map(proceso => `
        <tr>
            <td>${proceso.id}</td>
            <td>${proceso.nombre || proceso.descripcion || '-'}</td>
            <td>${formatDate(proceso.fechaInicio)}</td>
            <td>${formatDate(proceso.fechaFin)}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="editarProceso(${proceso.id})">
                    <i class="bi bi-pencil"></i> Editar
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="eliminarProceso(${proceso.id}, '${proceso.nombre || proceso.descripcion || 'Proceso'}')">
                    <i class="bi bi-trash"></i> Eliminar
                </button>
            </td>
        </tr>
    `).join('');
}

async function editarProceso(id) {
    try {
        const proceso = await api.get(`/procesos/${id}`);
        document.getElementById('procesoId').value = proceso.id;
        document.getElementById('procesoNombre').value = proceso.nombre || proceso.descripcion || '';
        document.getElementById('procesoFechaInicio').value = proceso.fechaInicio ? proceso.fechaInicio.substring(0, 16) : '';
        document.getElementById('procesoFechaFin').value = proceso.fechaFin ? proceso.fechaFin.substring(0, 16) : '';
        editMode = true;
        document.getElementById('modalProcesoTitle').textContent = 'Editar Proceso';
        modalProceso.show();
    } catch (error) {
        showAlert('Error al cargar el proceso: ' + error.message, 'danger');
    }
}

function limpiarFormulario() {
    document.getElementById('formProceso').reset();
    document.getElementById('procesoId').value = '';
    editMode = false;
    document.getElementById('modalProcesoTitle').textContent = 'Nuevo Proceso';
}

async function guardarProceso() {
    const id = document.getElementById('procesoId').value;
    const nombre = document.getElementById('procesoNombre').value.trim();
    const fechaInicio = document.getElementById('procesoFechaInicio').value;
    const fechaFin = document.getElementById('procesoFechaFin').value;

    if (!nombre || !fechaInicio || !fechaFin) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    if (new Date(fechaInicio) >= new Date(fechaFin)) {
        showAlert('La fecha de inicio debe ser anterior a la fecha de fin', 'warning');
        return;
    }

    try {
        const data = { nombre, descripcion: nombre, fechaInicio, fechaFin };
        if (editMode && id) {
            await api.put(`/procesos/${id}`, data);
            showAlert('Proceso actualizado correctamente', 'success');
        } else {
            await api.post('/procesos', data);
            showAlert('Proceso creado correctamente', 'success');
        }
        modalProceso.hide();
        cargarProcesos();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarProceso(id, descripcion) {

    mostrarConfirmacion(
        `¿Está seguro de eliminar el proceso <b>"${descripcion}"</b>?`,
        async () => {

            try {
                await api.delete(`/procesos/${id}`);
                showAlert('Proceso eliminado correctamente', 'success');
                cargarProcesos();

            } catch (error) {
                console.error('[Procesos] Error al eliminar:', error);

                const msg = error.message || '';

                if (msg.includes('foreign key') || msg.includes('llave foránea') || msg.includes('eleccion')) {
                    showAlert('No se puede eliminar el proceso porque tiene elecciones asociadas.', 'warning');
                } else {
                    showAlert('Error al eliminar: ' + msg, 'danger');
                }
            }
        }
    );
}

