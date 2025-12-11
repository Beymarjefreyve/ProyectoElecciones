/**
 * elecciones.js - Gestion completa de Elecciones
 * 
 * Campos OBLIGATORIOS segun EleccionRequestDTO:
 * - tipoEleccionId, tipoId, procesoId (Long)
 * - fechaInicio, fechaFinaliza (LocalDateTime)
 * 
 * Campos OPCIONALES:
 * - programaId, sedeId, facultadId (Long, nullable)
 * - nombre, descripcion (String)
 */

let modalEleccion;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    const modalEl = document.getElementById('modalEleccion');
    if (modalEl) {
        modalEleccion = new bootstrap.Modal(modalEl);
        modalEl.addEventListener('hidden.bs.modal', limpiarFormulario);
    }
    cargarCatalogos();
    cargarElecciones();
});

/**
 * Cargar catalogos para los selects
 */
async function cargarCatalogos() {
    try {
        const [tiposEleccion, tipos, procesos, sedes, facultades, programas] = await Promise.all([
            api.get('/catalogos/tipos-eleccion'),
            api.get('/catalogos/tipos'),
            api.get('/procesos'),
            api.get('/catalogos/sedes'),
            api.get('/catalogos/facultades'),
            api.get('/catalogos/programas')
        ]);

        llenarSelect('eleccionTipoEleccionId', tiposEleccion, 'Seleccione tipo eleccion');
        llenarSelect('eleccionTipoId', tipos, 'Seleccione tipo');
        llenarSelect('eleccionProcesoId', procesos, 'Seleccione proceso');
        llenarSelect('eleccionSedeId', sedes, 'Todas las sedes');
        llenarSelect('eleccionFacultadId', facultades, 'Todas las facultades');
        llenarSelect('eleccionProgramaId', programas, 'Todos los programas');

        console.log('[Elecciones] Catalogos cargados');
    } catch (error) {
        console.error('Error al cargar catalogos:', error);
        showAlert('Error al cargar catalogos: ' + error.message, 'danger');
    }
}

function llenarSelect(selectId, items, placeholder) {
    const select = document.getElementById(selectId);
    if (!select) {
        console.warn('Select no encontrado:', selectId);
        return;
    }

    const options = items.map(item => {
        const id = item.id;
        const nombre = item.nombre || item.descripcion || `Item ${id}`;
        return `<option value="${id}">${nombre}</option>`;
    }).join('');

    select.innerHTML = `<option value="">${placeholder}</option>` + options;
}

/**
 * Cargar listado de elecciones
 */
async function cargarElecciones() {
    console.log('[Elecciones] Cargando lista...');
    const tbody = document.getElementById('eleccionesTable');

    if (!tbody) {
        console.error('[Elecciones] Tabla no encontrada: eleccionesTable');
        return;
    }

    tbody.innerHTML = '<tr><td colspan="8" class="text-center"><div class="spinner-border spinner-border-sm"></div> Cargando...</td></tr>';

    try {
        const elecciones = await api.get('/elecciones');
        console.log('[Elecciones] Recibidas:', elecciones?.length || 0, 'elecciones');
        mostrarElecciones(elecciones || []);
    } catch (error) {
        console.error('[Elecciones] Error al cargar:', error);
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-danger">Error al cargar datos</td></tr>';
        showAlert('Error al cargar elecciones: ' + error.message, 'danger');
    }
}

function mostrarElecciones(elecciones) {
    const tbody = document.getElementById('eleccionesTable');

    if (!elecciones || elecciones.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No hay elecciones registradas</td></tr>';
        return;
    }

    console.log('[Elecciones] Renderizando', elecciones.length, 'elecciones');

    tbody.innerHTML = elecciones.map(e => {
        const estadoBadge = {
            'ACTIVA': 'bg-success',
            'ABIERTO': 'bg-primary',
            'CERRADO': 'bg-secondary'
        }[e.estado] || 'bg-secondary';

        const fechaInicio = e.fechaInicio ? new Date(e.fechaInicio).toLocaleString('es-ES') : '-';
        const fechaFin = e.fechaFinaliza ? new Date(e.fechaFinaliza).toLocaleString('es-ES') : '-';

        // Botones de control de votacion segun estado
        let botonesVotacion = '';
        if (e.estado === 'ACTIVA') {
            botonesVotacion = '<button class="btn btn-sm btn-success me-1" onclick="abrirVotacion(' + e.id + ')" title="Abrir Votacion"><i class="bi bi-unlock"></i></button>';
        } else if (e.estado === 'ABIERTO') {
            botonesVotacion = '<button class="btn btn-sm btn-danger me-1" onclick="cerrarVotacion(' + e.id + ')" title="Cerrar Votacion"><i class="bi bi-lock"></i></button>';
        }

        const nombreEscapado = (e.nombre || 'Eleccion').replace(/'/g, "\\'");

        return '<tr data-id="' + e.id + '">' +
            '<td>' + e.id + '</td>' +
            '<td>' + (e.nombre || '-') + '</td>' +
            '<td>' + (e.procesoId || '-') + '</td>' +
            '<td>' + (e.tipoEleccionId || '-') + '</td>' +
            '<td><span class="badge ' + estadoBadge + '">' + (e.estado || 'ACTIVA') + '</span></td>' +
            '<td>' + fechaInicio + '</td>' +
            '<td>' + fechaFin + '</td>' +
            '<td>' +
            botonesVotacion +
            '<button class="btn btn-sm btn-warning me-1" onclick="editarEleccion(' + e.id + ')" title="Editar"><i class="bi bi-pencil"></i></button>' +
            '<button class="btn btn-sm btn-danger" onclick="eliminarEleccion(' + e.id + ', \'' + nombreEscapado + '\')" title="Eliminar"><i class="bi bi-trash"></i></button>' +
            '</td>' +
            '</tr>';
    }).join('');
}

/**
 * Limpiar formulario
 */
function limpiarFormulario() {
    const form = document.getElementById('formEleccion');
    if (form) form.reset();

    document.getElementById('eleccionId').value = '';
    document.getElementById('modalEleccionTitle').textContent = 'Nueva Eleccion';
    editMode = false;

    // Fechas por defecto
    const now = new Date();
    const nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

    const formatForInput = (d) => d.toISOString().slice(0, 16);

    const fechaInicioEl = document.getElementById('eleccionFechaInicio');
    const fechaFinEl = document.getElementById('eleccionFechaFin');

    if (fechaInicioEl) fechaInicioEl.value = formatForInput(now);
    if (fechaFinEl) fechaFinEl.value = formatForInput(nextWeek);
}

/**
 * Editar eleccion existente
 */
async function editarEleccion(id) {
    try {
        const eleccion = await api.get('/elecciones/' + id);

        document.getElementById('eleccionId').value = eleccion.id;
        document.getElementById('eleccionNombre').value = eleccion.nombre || '';
        document.getElementById('eleccionProcesoId').value = eleccion.procesoId || '';
        document.getElementById('eleccionTipoEleccionId').value = eleccion.tipoEleccionId || '';
        document.getElementById('eleccionTipoId').value = eleccion.tipoId || '';
        document.getElementById('eleccionSedeId').value = eleccion.sedeId || '';
        document.getElementById('eleccionFacultadId').value = eleccion.facultadId || '';
        document.getElementById('eleccionProgramaId').value = eleccion.programaId || '';

        // Fechas
        if (eleccion.fechaInicio) {
            document.getElementById('eleccionFechaInicio').value = eleccion.fechaInicio.substring(0, 16);
        }
        if (eleccion.fechaFinaliza) {
            document.getElementById('eleccionFechaFin').value = eleccion.fechaFinaliza.substring(0, 16);
        }

        editMode = true;
        document.getElementById('modalEleccionTitle').textContent = 'Editar Eleccion';
        modalEleccion.show();
    } catch (error) {
        showAlert('Error al cargar eleccion: ' + error.message, 'danger');
    }
}

/**
 * Guardar eleccion (crear o actualizar)
 */
async function guardarEleccion() {
    const id = document.getElementById('eleccionId').value;
    const nombre = document.getElementById('eleccionNombre').value.trim();

    const procesoIdStr = document.getElementById('eleccionProcesoId').value;
    const tipoEleccionIdStr = document.getElementById('eleccionTipoEleccionId').value;
    const tipoIdStr = document.getElementById('eleccionTipoId').value;
    const fechaInicioStr = document.getElementById('eleccionFechaInicio').value;
    const fechaFinStr = document.getElementById('eleccionFechaFin').value;

    const sedeIdStr = document.getElementById('eleccionSedeId').value;
    const facultadIdStr = document.getElementById('eleccionFacultadId').value;
    const programaIdStr = document.getElementById('eleccionProgramaId').value;

    // Validaciones
    if (!nombre) return showAlert('El nombre es obligatorio', 'warning');
    if (!procesoIdStr) return showAlert('Debe seleccionar un Proceso', 'warning');
    if (!tipoEleccionIdStr) return showAlert('Debe seleccionar un Tipo de Eleccion', 'warning');
    if (!tipoIdStr) return showAlert('Debe seleccionar un Tipo', 'warning');
    if (!fechaInicioStr || !fechaFinStr) return showAlert('Las fechas son obligatorias', 'warning');

    if (new Date(fechaInicioStr) >= new Date(fechaFinStr)) {
        return showAlert('La fecha de inicio debe ser anterior a la fecha de fin', 'warning');
    }

    const data = {
        nombre: nombre,
        descripcion: nombre,
        procesoId: parseInt(procesoIdStr, 10),
        tipoEleccionId: parseInt(tipoEleccionIdStr, 10),
        tipoId: parseInt(tipoIdStr, 10),
        sedeId: sedeIdStr ? parseInt(sedeIdStr, 10) : null,
        facultadId: facultadIdStr ? parseInt(facultadIdStr, 10) : null,
        programaId: programaIdStr ? parseInt(programaIdStr, 10) : null,
        anio: new Date().getFullYear(),
        semestre: 1,
        fechaInicio: fechaInicioStr + ':00',
        fechaFinaliza: fechaFinStr + ':00'
    };

    try {
        if (editMode && id) {
            await api.put('/elecciones/' + id, data);
            showAlert('Eleccion actualizada correctamente', 'success');
        } else {
            await api.post('/elecciones', data);
            showAlert('Eleccion creada correctamente', 'success');
        }

        modalEleccion.hide();

        // Recargar lista
        await cargarElecciones();
    } catch (error) {
        console.error('Error al guardar:', error);
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

/**
 * Eliminar eleccion
 */
async function eliminarEleccion(id, nombre) {

    mostrarConfirmacion(
        `¿Está seguro de eliminar la elección <b>"${nombre}"</b>?`,
        async () => {

            try {
                await api.delete('/elecciones/' + id);
                showAlert('Elección eliminada correctamente', 'success');

                // Recargar tabla
                await cargarElecciones();

                // Si por error sigue en el DOM, eliminarla manualmente
                const fila = document.querySelector('tr[data-id="' + id + '"]');
                if (fila) fila.remove();

            } catch (error) {
                console.error('[Elecciones] Error al eliminar:', error);

                // Obtener mensaje real del backend
                let msg = error.message || '';
                if (error.response && error.response.data) {
                    msg = error.response.data.message || JSON.stringify(error.response.data);
                }

                // Casos especiales controlados
                if (msg.includes('foreign key') || msg.includes('censo')) {
                    showAlert('No se puede eliminar la votación porque tiene censos asociados.', 'warning');
                }
                else if (msg.includes('inscripcion')) {
                    showAlert('No se puede eliminar la votación porque tiene inscripciones asociadas.', 'warning');
                }
                else {
                    showAlert('Error al eliminar la elección: ' + msg, 'danger');
                }
            }
        }
    );
}





/**
 * Abrir votacion - cambia estado de ACTIVA a ABIERTO
 */
async function abrirVotacion(id) {
    if (!confirm('Esta seguro de ABRIR la votacion? Los estudiantes podran votar.')) return;

    try {
        await api.patch('/elecciones/' + id + '/estado', { estado: 'ABIERTO' });
        showAlert('Votacion ABIERTA correctamente', 'success');
        await cargarElecciones();
    } catch (error) {
        showAlert('Error al abrir votacion: ' + error.message, 'danger');
    }
}

/**
 * Cerrar votacion - cambia estado de ABIERTO a CERRADO
 */
async function cerrarVotacion(id) {
    if (!confirm('Esta seguro de CERRAR la votacion? Los estudiantes ya no podran votar.')) return;

    try {
        await api.patch('/elecciones/' + id + '/estado', { estado: 'CERRADO' });
        showAlert('Votacion CERRADA correctamente', 'success');
        await cargarElecciones();
    } catch (error) {
        showAlert('Error al cerrar votacion: ' + error.message, 'danger');
    }
}
