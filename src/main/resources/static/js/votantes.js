/**
 * votantes.js - Gestión de Votantes (solo estudiantes)
 */

document.addEventListener('DOMContentLoaded', () => {
    cargarVotantes();
});

async function cargarVotantes() {
    try {
        const response = await api.get('/votantes');

        // Filtrar administradores - solo mostrar estudiantes
        const votantes = response.filter(v => v.rol !== 'ADMIN' && v.rol !== 'ADMINISTRATIVO');

        const tbody = document.getElementById('votantesTable');
        if (!tbody) return;

        if (votantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay votantes registrados</td></tr>';
            return;
        }

        tbody.innerHTML = votantes.map(v => {
            const estadoBadge = v.estado === 'ACTIVO' ? 'bg-success' : 'bg-secondary';
            return `
                <tr>
                    <td>${v.id}</td>
                    <td>${v.documento || '-'}</td>
                    <td>${v.nombre || '-'}</td>
                    <td><span class="badge bg-info">${v.rol || 'ESTUDIANTE'}</span></td>
                    <td><span class="badge ${estadoBadge}">${v.estado || 'ACTIVO'}</span></td>
                    <td>
                        <button class="btn btn-sm btn-warning" onclick="editarVotante(${v.id})"><i class="bi bi-pencil"></i></button>
                        <button class="btn btn-sm btn-danger" onclick="eliminarVotante(${v.id}, '${v.nombre || ''}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Error cargando votantes:', error);
        const tbody = document.getElementById('votantesTable');
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar datos</td></tr>';
        }
        showAlert('Error al cargar votantes: ' + error.message, 'danger');
    }
}

async function editarVotante(id) {
    try {
        const votante = await api.get(`/votantes/${id}`);
        // Si hay modal de edición, llenar los campos
        const docField = document.getElementById('votanteDocumento');
        const nombreField = document.getElementById('votanteNombre');
        const idField = document.getElementById('votanteId');

        if (docField) docField.value = votante.documento || '';
        if (nombreField) nombreField.value = votante.nombre || '';
        if (idField) idField.value = votante.id;

        const modal = document.getElementById('modalVotante');
        if (modal) {
            const title = document.getElementById('modalVotanteTitle');
            if (title) title.textContent = 'Editar Votante';
            new bootstrap.Modal(modal).show();
        } else {
            showAlert('Votante ID: ' + id + ' - ' + votante.nombre, 'info');
        }
    } catch (error) {
        showAlert('Error al cargar el votante: ' + error.message, 'danger');
    }
}

async function eliminarVotante(id, nombre) {

    mostrarConfirmacion(
        `¿Está seguro de eliminar el votante <b>"${nombre}"</b>?`,
        async () => {

            try {
                await api.delete(`/votantes/${id}`);
                showAlert('Votante eliminado correctamente', 'success');
                cargarVotantes();

            } catch (error) {
                console.error('[Votantes] Error al eliminar:', error);

                const msg = error.message || '';

                if (
                    msg.includes('foreign key') ||
                    msg.includes('llave foránea') ||
                    msg.includes('voto') ||
                    msg.includes('vot')
                ) {
                    showAlert(
                        'No se puede eliminar el votante porque se encuentra asociado a un censo.',
                        'warning'
                    );
                } else {
                    showAlert('Error al eliminar: ' + msg, 'danger');
                }
            }

        }
    );

}

