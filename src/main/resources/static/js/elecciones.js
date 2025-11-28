let modalEleccion;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalEleccion = new bootstrap.Modal(document.getElementById('modalEleccion'));
    cargarCatalogos();
    cargarElecciones();
    document.getElementById('modalEleccion').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formEleccion').reset();
        document.getElementById('eleccionId').value = '';
        editMode = false;
        document.getElementById('modalEleccionTitle').textContent = 'Nueva Elección';
    });
});

async function cargarCatalogos() {
    try {
        const [tiposEleccion, tipos, procesos, programas, sedes, facultades] = await Promise.all([
            api.get('/catalogos/tipos-eleccion'),
            api.get('/catalogos/tipos'),
            api.get('/procesos'),
            api.get('/catalogos/programas'),
            api.get('/catalogos/sedes'),
            api.get('/catalogos/facultades')
        ]);

        llenarSelect('eleccionTipoEleccionId', tiposEleccion, 'Seleccione tipo elección');
        llenarSelect('eleccionTipoId', tipos, 'Seleccione tipo');
        llenarSelect('eleccionProcesoId', procesos, 'Seleccione proceso');
        llenarSelect('eleccionProgramaId', programas, 'Seleccione programa');
        llenarSelect('eleccionSedeId', sedes, 'Seleccione sede');
        llenarSelect('eleccionFacultadId', facultades, 'Seleccione facultad');
    } catch (error) {
        console.error('Error cargando catálogos:', error);
    }
}

function llenarSelect(id, items, placeholder) {
    const select = document.getElementById(id);
    select.innerHTML = `<option value="">${placeholder}</option>` +
        items.map(item => `<option value="${item.id}">${item.nombre || item.descripcion || item.id}</option>`).join('');
}

async function cargarElecciones() {
    try {
        const elecciones = await api.get('/elecciones');
        mostrarElecciones(elecciones);
    } catch (error) {
        showAlert('Error al cargar las elecciones: ' + error.message, 'danger');
        document.getElementById('eleccionesTable').innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function cargarEleccionesActivas() {
    try {
        const elecciones = await api.get('/elecciones/activas');
        mostrarElecciones(elecciones);
    } catch (error) {
        showAlert('Error al cargar las elecciones activas: ' + error.message, 'danger');
    }
}

async function cargarEleccionesAbiertas() {
    try {
        const elecciones = await api.get('/elecciones/abiertas');
        mostrarElecciones(elecciones);
    } catch (error) {
        showAlert('Error al cargar las elecciones abiertas: ' + error.message, 'danger');
    }
}

async function filtrarPorEstado() {
    const estado = document.getElementById('filtroEstado').value;
    if (!estado) {
        cargarElecciones();
        return;
    }
    try {
        const elecciones = await api.get(`/elecciones/filtro/estado?estado=${estado}`);
        mostrarElecciones(elecciones);
    } catch (error) {
        showAlert('Error al filtrar: ' + error.message, 'danger');
    }
}

function mostrarElecciones(elecciones) {
    const tbody = document.getElementById('eleccionesTable');
    if (elecciones.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay elecciones registradas</td></tr>';
        return;
    }
    tbody.innerHTML = elecciones.map(eleccion => {
        const estadoBadge = {
            'ACTIVA': 'bg-success',
            'ABIERTO': 'bg-primary',
            'CERRADO': 'bg-secondary'
        }[eleccion.estado] || 'bg-secondary';
        
        return `
            <tr>
                <td>${eleccion.id}</td>
                <td>${eleccion.nombre || '-'}</td>
                <td><span class="badge ${estadoBadge}">${eleccion.estado || '-'}</span></td>
                <td>${formatDateTime(eleccion.fechaInicio)}</td>
                <td>${formatDateTime(eleccion.fechaFinaliza)}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarEleccion(${eleccion.id})">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarEleccion(${eleccion.id}, '${eleccion.nombre || 'Elección'}')">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                    <a href="/resultados.html?eleccionId=${eleccion.id}" class="btn btn-sm btn-outline-info">
                        <i class="bi bi-bar-chart"></i> Resultados
                    </a>
                </td>
            </tr>
        `;
    }).join('');
}

async function editarEleccion(id) {
    try {
        const eleccion = await api.get(`/elecciones/${id}`);
        document.getElementById('eleccionId').value = eleccion.id;
        document.getElementById('eleccionNombre').value = eleccion.nombre || '';
        document.getElementById('eleccionDescripcion').value = eleccion.descripcion || '';
        document.getElementById('eleccionAnio').value = eleccion.anio || '';
        document.getElementById('eleccionSemestre').value = eleccion.semestre || '';
        document.getElementById('eleccionTipoEleccionId').value = eleccion.tipoEleccionId || '';
        document.getElementById('eleccionTipoId').value = eleccion.tipoId || '';
        document.getElementById('eleccionProcesoId').value = eleccion.procesoId || '';
        document.getElementById('eleccionProgramaId').value = eleccion.programaId || '';
        document.getElementById('eleccionSedeId').value = eleccion.sedeId || '';
        document.getElementById('eleccionFacultadId').value = eleccion.facultadId || '';
        
        if (eleccion.fechaInicio) {
            const fechaInicio = new Date(eleccion.fechaInicio);
            document.getElementById('eleccionFechaInicio').value = fechaInicio.toISOString().slice(0, 16);
        }
        if (eleccion.fechaFinaliza) {
            const fechaFin = new Date(eleccion.fechaFinaliza);
            document.getElementById('eleccionFechaFin').value = fechaFin.toISOString().slice(0, 16);
        }
        
        editMode = true;
        document.getElementById('modalEleccionTitle').textContent = 'Editar Elección';
        modalEleccion.show();
    } catch (error) {
        showAlert('Error al cargar la elección: ' + error.message, 'danger');
    }
}

async function guardarEleccion() {
    const id = document.getElementById('eleccionId').value;
    const nombre = document.getElementById('eleccionNombre').value.trim();
    const descripcion = document.getElementById('eleccionDescripcion').value.trim();
    const anio = document.getElementById('eleccionAnio').value ? parseInt(document.getElementById('eleccionAnio').value) : null;
    const semestre = document.getElementById('eleccionSemestre').value ? parseInt(document.getElementById('eleccionSemestre').value) : null;
    const tipoEleccionId = document.getElementById('eleccionTipoEleccionId').value ? parseInt(document.getElementById('eleccionTipoEleccionId').value) : null;
    const tipoId = document.getElementById('eleccionTipoId').value ? parseInt(document.getElementById('eleccionTipoId').value) : null;
    const procesoId = document.getElementById('eleccionProcesoId').value ? parseInt(document.getElementById('eleccionProcesoId').value) : null;
    const programaId = document.getElementById('eleccionProgramaId').value ? parseInt(document.getElementById('eleccionProgramaId').value) : null;
    const sedeId = document.getElementById('eleccionSedeId').value ? parseInt(document.getElementById('eleccionSedeId').value) : null;
    const facultadId = document.getElementById('eleccionFacultadId').value ? parseInt(document.getElementById('eleccionFacultadId').value) : null;
    const fechaInicio = document.getElementById('eleccionFechaInicio').value;
    const fechaFinaliza = document.getElementById('eleccionFechaFin').value;

    if (!nombre || !fechaInicio || !fechaFinaliza) {
        showAlert('Nombre, fecha inicio y fecha fin son obligatorios', 'warning');
        return;
    }

    const data = {
        nombre,
        descripcion: descripcion || null,
        anio,
        semestre,
        tipoEleccionId,
        tipoId,
        procesoId,
        programaId,
        sedeId,
        facultadId,
        fechaInicio,
        fechaFinaliza
    };

    try {
        if (editMode && id) {
            await api.put(`/elecciones/${id}`, data);
            showAlert('Elección actualizada correctamente', 'success');
        } else {
            await api.post('/elecciones', data);
            showAlert('Elección creada correctamente', 'success');
        }
        modalEleccion.hide();
        cargarElecciones();
    } catch (error) {
        showAlert('Error al guardar: ' + error.message, 'danger');
    }
}

async function eliminarEleccion(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar/desactivar la elección "${nombre}"?`)) return;
    try {
        await api.delete(`/elecciones/${id}`);
        showAlert('Elección eliminada/desactivada correctamente', 'success');
        cargarElecciones();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

