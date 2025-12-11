/**
 * censo.js - Gestión del Censo Electoral
 * El censo vincula votantes (estudiantes) con elecciones específicas
 * Solo los votantes en el censo pueden votar en una elección
 */

document.addEventListener('DOMContentLoaded', () => {
    cargarEleccionesParaCenso();
    cargarVotantesParaCenso();
});

/* =============================
   Cargar elecciones
============================= */
async function cargarEleccionesParaCenso() {
    try {
        const elecciones = await api.get('/elecciones');
        const options = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e =>
                `<option value="${e.id}">${e.id} - ${e.nombre} [${e.estado}]</option>`
            ).join('');

        document.getElementById('selectEleccionCenso').innerHTML = options;
        document.getElementById('censoEleccionId').innerHTML = options;

    } catch (error) {
        showAlert('Error al cargar elecciones', 'danger');
    }
}

/* =============================
   Cargar votantes
============================= */
async function cargarVotantesParaCenso() {
    try {
        const votantes = await api.get('/votantes');

        // Solo estudiantes
        const estudiantes = votantes.filter(v => v.rol !== "ADMIN" && v.rol !== "ADMINISTRATIVO");

        document.getElementById('censoVotanteId').innerHTML =
            '<option value="">Seleccione un votante</option>' +
            estudiantes.map(v =>
                `<option value="${v.id}">${v.documento} - ${v.nombre}</option>`
            ).join('');

    } catch (error) {
        showAlert('Error al cargar votantes', 'danger');
    }
}

/* =============================
   Cargar CENSO
============================= */
async function cargarCenso() {
    const eleccionId = document.getElementById('selectEleccionCenso').value;
    const tbody = document.getElementById('censoTable');

    if (!eleccionId) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Seleccione una elección</td></tr>';
        return;
    }

    tbody.innerHTML = '<tr><td colspan="4" class="text-center">Cargando...</td></tr>';

    try {
        const censo = await api.get(`/elecciones/${eleccionId}/censo`);

        if (censo.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">No hay votantes registrados</td></tr>';
            return;
        }

        // Para cada item traemos su votante real
        const filas = await Promise.all(censo.map(async item => {
            const votante = await api.get(`/votantes/${item.votanteId}`);
            return `
                <tr>
                    <td>${item.id}</td>
                    <td>${votante.nombre}</td>
                    <td>${votante.documento}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" 
                            onclick="eliminarDelCenso(${item.votanteId}, ${eleccionId})">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
                    </td>
                </tr>`;
        }));

        tbody.innerHTML = filas.join('');

    } catch (error) {
        showAlert('Error al cargar el censo', 'danger');
    }
}

/* =============================
   Guardar en el CENSO
============================= */
async function guardarCenso() {
    const eleccionId = document.getElementById('censoEleccionId').value;
    const votanteId = document.getElementById('censoVotanteId').value;

    if (!eleccionId || !votanteId) {
        showAlert('Debe seleccionar elección y votante', 'warning');
        return;
    }

    try {
        await api.post('/api/censo', {
            eleccionId: parseInt(eleccionId),
            votanteId: parseInt(votanteId)
        });

        showAlert('Votante agregado correctamente', 'success');

        bootstrap.Modal.getInstance(document.getElementById('modalCenso')).hide();

        document.getElementById('formCenso').reset();
        cargarCenso();

    } catch (error) {
        showAlert('Error al agregar al censo: ' + error.message, 'danger');
    }
}

/* =============================
   Eliminar del CENSO
============================= */
async function eliminarDelCenso(votanteId, eleccionId) {

    mostrarConfirmacion(
        '¿Está seguro de eliminar este votante del censo?',
        async () => {

            try {
                await api.delete(`/api/censo?votanteId=${votanteId}&eleccionId=${eleccionId}`);
                showAlert('Eliminado correctamente', 'success');
                cargarCenso();

            } catch (error) {
                showAlert('Error al eliminar', 'danger');
            }

        }
    );

}

