// Cargar estadísticas del dashboard
document.addEventListener('DOMContentLoaded', async () => {
    console.log('[Dashboard] Iniciando carga de datos...');
    
    try {
        // Cargar elecciones activas
        try {
            const eleccionesActivas = await api.get('/elecciones/activas');
            document.getElementById('eleccionesActivas').textContent = eleccionesActivas ? eleccionesActivas.length : 0;
        } catch (error) {
            console.warn('Error cargando elecciones activas:', error);
            document.getElementById('eleccionesActivas').textContent = 'Error';
        }

        // Cargar procesos activos
        try {
            const procesosActivos = await api.get('/procesos/activos');
            document.getElementById('procesosActivos').textContent = procesosActivos ? procesosActivos.length : 0;
        } catch (error) {
            console.warn('Error cargando procesos activos:', error);
            document.getElementById('procesosActivos').textContent = 'Error';
        }

        // Cargar total votantes
        try {
            const votantes = await api.get('/votantes');
            document.getElementById('totalVotantes').textContent = votantes ? votantes.length : 0;
        } catch (error) {
            console.warn('Error cargando votantes:', error);
            document.getElementById('totalVotantes').textContent = 'Error';
        }

        // Cargar total candidatos
        try {
            const candidatos = await api.get('/candidatos');
            document.getElementById('totalCandidatos').textContent = candidatos ? candidatos.length : 0;
        } catch (error) {
            console.warn('Error cargando candidatos:', error);
            document.getElementById('totalCandidatos').textContent = 'Error';
        }

        // Cargar elecciones recientes (últimas 5)
        try {
            const todasElecciones = await api.get('/elecciones');
            const eleccionesRecientes = todasElecciones ? todasElecciones.slice(0, 5) : [];
            
            const tbody = document.querySelector('#eleccionesTable tbody');
            if (eleccionesRecientes.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay elecciones registradas</td></tr>';
            } else {
                tbody.innerHTML = eleccionesRecientes.map(eleccion => {
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
                                <a href="/elecciones.html?id=${eleccion.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-eye"></i> Ver
                                </a>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        } catch (error) {
            console.warn('Error cargando elecciones:', error);
            const tbody = document.querySelector('#eleccionesTable tbody');
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar elecciones</td></tr>';
        }
        
        console.log('[Dashboard] Carga completada');
    } catch (error) {
        console.error('[Dashboard] Error general:', error);
        showAlert('Error al cargar las estadísticas del dashboard: ' + error.message, 'danger');
    }
});

