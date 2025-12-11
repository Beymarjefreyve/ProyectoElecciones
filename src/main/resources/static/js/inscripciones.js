document.addEventListener('DOMContentLoaded', () => {
    cargarElecciones();
    cargarCandidatosSelect();
});

async function cargarElecciones() {
    try {
        const elecciones = await api.get('/elecciones/activas');
        const select = document.getElementById('selectEleccionInscripcion');
        const selectModal = document.getElementById('inscripcionEleccionId');

        const options = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e => `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'}</option>`).join('');

        select.innerHTML = options;
        selectModal.innerHTML = options;
    } catch (error) {
        console.error('Error cargando elecciones:', error);
    }
}

async function cargarCandidatosSelect() {
    const select = document.getElementById('inscripcionCandidatoId');
    if (!select) return;

    try {
        const candidatos = await api.get('/candidatos');

        if (!candidatos || candidatos.length === 0) {
            select.innerHTML = '<option value="">No hay candidatos registrados</option>';
            return;
        }

        select.innerHTML =
            '<option value="">Seleccione un candidato</option>' +
            candidatos.map(c => `<option value="${c.id}">${c.id} - ${c.nombre} (${c.documento})</option>`).join('');

    } catch (error) {
        console.error('Error cargando candidatos:', error);
        select.innerHTML = '<option value="">Error al cargar candidatos</option>';
    }
}

async function cargarInscripciones() {
    const eleccionId = document.getElementById('selectEleccionInscripcion').value;

    if (!eleccionId) {
        document.getElementById('inscripcionesTable').innerHTML =
            '<tr><td colspan="4" class="text-center">Seleccione una elección</td></tr>';
        return;
    }

    try {
        const inscripciones = await api.get(`/elecciones/${eleccionId}/candidatos`);
        const tbody = document.getElementById('inscripcionesTable');

        if (inscripciones.length === 0) {
            tbody.innerHTML =
                '<tr><td colspan="4" class="text-center">No hay inscripciones para esta elección</td></tr>';
            return;
        }

        tbody.innerHTML = inscripciones.map(inscripcion => `
            <tr>
                <td>${inscripcion.id}</td>
                <td>${inscripcion.numero || '-'}</td>
                <td>${inscripcion.candidatoNombre || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarInscripcion(${inscripcion.id})">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        showAlert('Error al cargar las inscripciones: ' + error.message, 'danger');
        document.getElementById('inscripcionesTable').innerHTML =
            '<tr><td colspan="4" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function guardarInscripcion() {
    const eleccionId = document.getElementById('inscripcionEleccionId').value;
    const candidatoId = document.getElementById('inscripcionCandidatoId').value;
    const numero = document.getElementById('inscripcionNumero').value;

    if (!eleccionId || !candidatoId || !numero) {
        showAlert('Todos los campos son obligatorios', 'warning');
        return;
    }

    try {
        await api.post(`/elecciones/${eleccionId}/inscripciones`, {
            candidatoId: parseInt(candidatoId),
            numero: parseInt(numero)
        });

        showAlert('Inscripción creada correctamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalInscripcion')).hide();
        document.getElementById('formInscripcion').reset();
        cargarInscripciones();

    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarInscripcion(id) {

    mostrarConfirmacion(
        '¿Está seguro de eliminar esta inscripción?',
        async () => {
            try {
                await api.delete(`/elecciones/inscripciones/${id}`);
                showAlert('Inscripción eliminada correctamente', 'success');
                cargarInscripciones();

            } catch (error) {
                showAlert('Error al eliminar: ' + error.message, 'danger');
            }
        }
    );
}
