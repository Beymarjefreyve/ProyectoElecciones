/**
 * censo.js - Gestión del Censo Electoral
 * El censo vincula votantes (estudiantes) con elecciones específicas
 * Solo los votantes en el censo pueden votar en una elección
 */

// Lista de votantes seleccionados para agregar al censo
let votantesSeleccionados = [];
let timeoutBusqueda = null;

document.addEventListener('DOMContentLoaded', () => {
    cargarEleccionesParaCenso();
    cargarFacultadesParaCenso();
    configurarBusquedaVotantes();
});

/* =============================
   Cargar facultades para filtro
============================= */
async function cargarFacultadesParaCenso() {
    try {
        const facultades = await api.get('/catalogos/facultades');
        const select = document.getElementById('filtroFacultadId');

        if (select) {
            select.innerHTML = '<option value="">Todas las facultades</option>' +
                facultades.map(f =>
                    `<option value="${f.id}">${f.nombre}</option>`
                ).join('');
        }
    } catch (error) {
        console.error('Error al cargar facultades:', error);
    }
}

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

        const selectCenso = document.getElementById('selectEleccionCenso');
        const censoEleccionId = document.getElementById('censoEleccionId');

        if (selectCenso) selectCenso.innerHTML = options;
        if (censoEleccionId) censoEleccionId.innerHTML = options;

    } catch (error) {
        showAlert('Error al cargar elecciones', 'danger');
    }
}

/* =============================
   Configurar búsqueda de votantes
============================= */
function configurarBusquedaVotantes() {
    const input = document.getElementById('buscarVotanteInput');
    if (!input) return;

    input.addEventListener('input', function () {
        const texto = this.value.trim();

        // Limpiar timeout anterior
        if (timeoutBusqueda) {
            clearTimeout(timeoutBusqueda);
        }

        // Esperar 300ms después de que el usuario deje de escribir
        if (texto.length >= 3) {
            timeoutBusqueda = setTimeout(() => buscarVotantes(texto), 300);
        } else {
            const resultados = document.getElementById('resultadosBusqueda');
            if (resultados) resultados.style.display = 'none';
        }
    });

    // Limpiar al abrir el modal
    const modal = document.getElementById('modalCenso');
    if (modal) {
        modal.addEventListener('show.bs.modal', function () {
            limpiarFormularioCenso();
        });
    }

    // Escuchar cambios en la elección
    const censoEleccionId = document.getElementById('censoEleccionId');
    if (censoEleccionId) {
        censoEleccionId.addEventListener('change', actualizarBotonGuardar);
    }

    // Escuchar cambios en el filtro de facultad
    const filtroFacultad = document.getElementById('filtroFacultadId');
    if (filtroFacultad) {
        filtroFacultad.addEventListener('change', async function () {
            const facultadId = this.value;

            // Si se selecciona una facultad, cargar todos los votantes de esa facultad
            if (facultadId) {
                await cargarVotantesPorFacultad(facultadId);
            }

            // También refrescar los resultados de búsqueda si hay texto
            const input = document.getElementById('buscarVotanteInput');
            if (input && input.value.trim().length >= 3) {
                buscarVotantes(input.value.trim());
            }
        });
    }
}

/* =============================
   Cargar todos los votantes de una facultad
   y agregarlos a la lista de seleccionados
============================= */
async function cargarVotantesPorFacultad(facultadId) {
    if (!facultadId) return;

    try {
        showLoading('Cargando votantes de la facultad...');

        const votantes = await api.get('/votantes/por-facultad/' + facultadId);

        if (votantes.length === 0) {
            showAlert('No hay votantes registrados en esta facultad', 'info');
            return;
        }

        // Agregar cada votante que no esté ya en la lista
        let agregados = 0;
        votantes.forEach(v => {
            if (!votantesSeleccionados.some(vs => vs.id === v.id)) {
                votantesSeleccionados.push({
                    id: v.id,
                    documento: v.documento,
                    nombre: v.nombre
                });
                agregados++;
            }
        });

        // Actualizar UI
        renderizarVotantesSeleccionados();
        actualizarBotonGuardar();

        if (agregados > 0) {
            showAlert(`${agregados} votante(s) agregados a la lista. ${votantes.length - agregados} ya estaban seleccionados.`, 'success');
        } else {
            showAlert('Todos los votantes de esta facultad ya están en la lista', 'info');
        }

    } catch (error) {
        console.error('Error cargando votantes por facultad:', error);
        showAlert('Error al cargar votantes: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

/* =============================
   Helpers para loading
============================= */
function showLoading(mensaje) {
    const container = document.getElementById('votantesSeleccionadosContainer');
    if (container && votantesSeleccionados.length === 0) {
        container.innerHTML = `<div class="text-center text-muted"><span class="spinner-border spinner-border-sm me-2"></span>${mensaje}</div>`;
    }
}

function hideLoading() {
    if (votantesSeleccionados.length === 0) {
        renderizarVotantesSeleccionados();
    }
}

/* =============================
   Buscar votantes
============================= */
async function buscarVotantes(texto) {
    const container = document.getElementById('resultadosBusqueda');
    if (!container) return;

    try {
        container.innerHTML = '<div class="p-3 text-center text-muted"><i class="bi bi-hourglass-split"></i> Buscando...</div>';
        container.style.display = 'block';

        // Obtener filtro de facultad si está seleccionado
        const filtroFacultad = document.getElementById('filtroFacultadId');
        const facultadId = filtroFacultad ? filtroFacultad.value : '';

        let url = '/votantes/buscar?texto=' + encodeURIComponent(texto);
        if (facultadId) {
            url += '&facultadId=' + facultadId;
        }

        const votantes = await api.get(url);

        if (votantes.length === 0) {
            container.innerHTML = '<div class="p-3 text-center text-muted"><i class="bi bi-exclamation-circle"></i> No se encontraron votantes</div>';
            return;
        }

        container.innerHTML = votantes.map(v => {
            const yaSeleccionado = votantesSeleccionados.some(vs => vs.id === v.id);
            const nombreEscapado = (v.nombre || '').replace(/'/g, "\\'").replace(/"/g, '&quot;');
            return `
                <div class="search-result-item p-2 border-bottom ${yaSeleccionado ? 'disabled' : ''}" 
                     onclick="${yaSeleccionado ? '' : `agregarVotante(${v.id}, '${v.documento}', '${nombreEscapado}')`}">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="bi bi-person me-2"></i>
                            <strong>${v.documento}</strong> - ${v.nombre}
                        </div>
                        ${yaSeleccionado
                    ? '<span class="badge bg-success"><i class="bi bi-check"></i> Ya seleccionado</span>'
                    : '<small class="text-primary"><i class="bi bi-plus-circle"></i> Agregar</small>'}
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        container.innerHTML = '<div class="p-3 text-center text-danger"><i class="bi bi-exclamation-triangle"></i> Error al buscar</div>';
    }
}

/* =============================
   Agregar votante a la lista
============================= */
function agregarVotante(id, documento, nombre) {
    // Verificar si ya está en la lista
    if (votantesSeleccionados.some(v => v.id === id)) {
        return;
    }

    // Agregar a la lista
    votantesSeleccionados.push({ id, documento, nombre });

    // Actualizar UI inmediatamente
    renderizarVotantesSeleccionados();
    actualizarBotonGuardar();

    // Refrescar resultados de búsqueda para mostrar el votante como ya seleccionado
    const input = document.getElementById('buscarVotanteInput');
    if (input && input.value.trim().length >= 3) {
        buscarVotantes(input.value.trim());
    }
}

/* =============================
   Quitar votante de la lista
============================= */
function quitarVotante(id) {
    votantesSeleccionados = votantesSeleccionados.filter(v => v.id !== id);

    // Actualizar UI inmediatamente
    renderizarVotantesSeleccionados();
    actualizarBotonGuardar();

    // Refrescar resultados de búsqueda
    const input = document.getElementById('buscarVotanteInput');
    if (input && input.value.trim().length >= 3) {
        buscarVotantes(input.value.trim());
    }
}

/* =============================
   Renderizar votantes seleccionados
============================= */
function renderizarVotantesSeleccionados() {
    const container = document.getElementById('votantesSeleccionadosContainer');
    const contador = document.getElementById('contadorSeleccionados');

    // Actualizar contador si existe
    if (contador) {
        contador.textContent = votantesSeleccionados.length;
    }

    // Actualizar contenedor si existe
    if (!container) return;

    if (votantesSeleccionados.length === 0) {
        container.innerHTML = '<span class="text-muted"><i class="bi bi-info-circle me-1"></i>Busque y seleccione votantes arriba</span>';
        return;
    }

    container.innerHTML = votantesSeleccionados.map(v => `
        <span class="votante-chip">
            <i class="bi bi-person-check me-1"></i>
            ${v.documento} - ${v.nombre}
            <button type="button" class="btn-remove" onclick="quitarVotante(${v.id})" title="Quitar">
                <i class="bi bi-x-circle"></i>
            </button>
        </span>
    `).join('');
}

/* =============================
   Limpiar selección
============================= */
function limpiarSeleccion() {
    votantesSeleccionados = [];
    renderizarVotantesSeleccionados();
    actualizarBotonGuardar();

    const resultados = document.getElementById('resultadosBusqueda');
    if (resultados) resultados.style.display = 'none';

    const input = document.getElementById('buscarVotanteInput');
    if (input) input.value = '';
}

/* =============================
   Limpiar formulario completo
============================= */
function limpiarFormularioCenso() {
    votantesSeleccionados = [];

    const censoEleccionId = document.getElementById('censoEleccionId');
    if (censoEleccionId) censoEleccionId.value = '';

    const buscarInput = document.getElementById('buscarVotanteInput');
    if (buscarInput) buscarInput.value = '';

    const resultados = document.getElementById('resultadosBusqueda');
    if (resultados) resultados.style.display = 'none';

    renderizarVotantesSeleccionados();
    actualizarBotonGuardar();
}

/* =============================
   Actualizar estado del botón
============================= */
function actualizarBotonGuardar() {
    const censoEleccionId = document.getElementById('censoEleccionId');
    const btn = document.getElementById('btnGuardarCenso');
    const spanCantidad = document.getElementById('btnCantidad');

    const eleccionId = censoEleccionId ? censoEleccionId.value : '';
    const cantidad = votantesSeleccionados.length;

    if (btn) {
        btn.disabled = !(eleccionId && cantidad > 0);
    }

    if (spanCantidad) {
        spanCantidad.textContent = cantidad;
    }
}

/* =============================
   Cargar CENSO
============================= */
async function cargarCenso() {
    const selectEleccion = document.getElementById('selectEleccionCenso');
    const tbody = document.getElementById('censoTable');
    const cuenta = document.getElementById('censoCuenta');

    if (!selectEleccion || !tbody) return;

    const eleccionId = selectEleccion.value;

    if (!eleccionId) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Seleccione una elección</td></tr>';
        if (cuenta) cuenta.innerHTML = '';
        return;
    }

    tbody.innerHTML = '<tr><td colspan="4" class="text-center"><span class="spinner-border spinner-border-sm me-2"></span>Cargando...</td></tr>';

    try {
        const censo = await api.get(`/elecciones/${eleccionId}/censo`);

        if (cuenta) {
            cuenta.innerHTML = `<span class="badge bg-primary">${censo.length} votante(s) en el censo</span>`;
        }

        if (censo.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No hay votantes registrados en esta elección</td></tr>';
            return;
        }

        // Para cada item traemos su votante real
        const filas = await Promise.all(censo.map(async item => {
            try {
                const votante = await api.get(`/votantes/${item.votanteId}`);
                return `
                    <tr data-votante-id="${item.votanteId}">
                        <td>${item.id}</td>
                        <td>${votante.nombre || 'N/A'}</td>
                        <td>${votante.documento || 'N/A'}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" 
                                onclick="eliminarDelCenso(${item.votanteId}, ${eleccionId})">
                                <i class="bi bi-trash"></i> Eliminar
                            </button>
                        </td>
                    </tr>`;
            } catch (e) {
                return `
                    <tr>
                        <td>${item.id}</td>
                        <td colspan="2" class="text-muted">Error cargando votante</td>
                        <td>
                            <button class="btn btn-sm btn-outline-danger" 
                                onclick="eliminarDelCenso(${item.votanteId}, ${eleccionId})">
                                <i class="bi bi-trash"></i> Eliminar
                            </button>
                        </td>
                    </tr>`;
            }
        }));

        tbody.innerHTML = filas.join('');

    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Error al cargar el censo</td></tr>';
        showAlert('Error al cargar el censo: ' + error.message, 'danger');
    }
}

/* =============================
   Guardar MÚLTIPLES en el CENSO
============================= */
async function guardarCensoMasivo() {
    const censoEleccionId = document.getElementById('censoEleccionId');
    const eleccionId = censoEleccionId ? censoEleccionId.value : '';

    if (!eleccionId || votantesSeleccionados.length === 0) {
        showAlert('Debe seleccionar una elección y al menos un votante', 'warning');
        return;
    }

    const btn = document.getElementById('btnGuardarCenso');
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Agregando...';
    }

    try {
        const votanteIds = votantesSeleccionados.map(v => v.id);

        const resultado = await api.post('/api/censo/carga-masiva', {
            eleccionId: parseInt(eleccionId),
            votanteIds: votanteIds
        });

        // Mostrar resultado
        let mensaje = `Procesados: ${resultado.totalProcesados}`;
        if (resultado.agregados > 0) mensaje += `, Agregados: ${resultado.agregados}`;
        if (resultado.yaExistentes > 0) mensaje += `, Ya existentes: ${resultado.yaExistentes}`;
        if (resultado.errores > 0) mensaje += `, Errores: ${resultado.errores}`;

        showAlert(mensaje, resultado.errores > 0 ? 'warning' : 'success');

        // Cerrar modal
        const modal = document.getElementById('modalCenso');
        if (modal) {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) bsModal.hide();
        }

        limpiarFormularioCenso();

        // Recargar la lista del censo automáticamente
        const selectEleccionCenso = document.getElementById('selectEleccionCenso');
        if (selectEleccionCenso && selectEleccionCenso.value === eleccionId) {
            await cargarCenso();
        }

    } catch (error) {
        showAlert('Error al agregar al censo: ' + error.message, 'danger');
    } finally {
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = '<i class="bi bi-plus-circle me-1"></i>Agregar <span id="btnCantidad">' + votantesSeleccionados.length + '</span> al Censo';
        }
        actualizarBotonGuardar();
    }
}

/* =============================
   Eliminar del CENSO
============================= */
async function eliminarDelCenso(votanteId, eleccionId) {
    // Usar confirmación nativa si mostrarConfirmacion no existe
    if (typeof mostrarConfirmacion === 'function') {
        mostrarConfirmacion(
            '¿Está seguro de eliminar este votante del censo?',
            async () => {
                await ejecutarEliminacionCenso(votanteId, eleccionId);
            }
        );
    } else {
        if (confirm('¿Está seguro de eliminar este votante del censo?')) {
            await ejecutarEliminacionCenso(votanteId, eleccionId);
        }
    }
}

async function ejecutarEliminacionCenso(votanteId, eleccionId) {
    try {
        await api.delete(`/api/censo?votanteId=${votanteId}&eleccionId=${eleccionId}`);
        showAlert('Eliminado correctamente', 'success');

        // Actualizar la tabla inmediatamente sin recargar toda la página
        await cargarCenso();

    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}
