/**
 * resultados.js - Visualizacion de Resultados de Elecciones
 * Los resultados se calculan en tiempo real desde la tabla de votos (Resultado)
 */

let chartResultados = null;
let eleccionIdActual = null;

document.addEventListener('DOMContentLoaded', () => {
    console.log('[Resultados] Iniciando modulo...');
    cargarElecciones();

    // Si hay eleccion en URL, cargar sus resultados
    const urlParams = new URLSearchParams(window.location.search);
    const eleccionId = urlParams.get('eleccionId');
    if (eleccionId) {
        setTimeout(() => {
            const select = document.getElementById('selectEleccionResultado');
            if (select) {
                select.value = eleccionId;
                cargarResultados();
            }
        }, 500);
    }
});

/**
 * Cargar elecciones en el select
 */
async function cargarElecciones() {
    console.log('[Resultados] Cargando elecciones...');
    try {
        const elecciones = await api.get('/elecciones');
        console.log('[Resultados] Elecciones recibidas:', elecciones);

        const select = document.getElementById('selectEleccionResultado');
        if (!select) {
            console.error('[Resultados] Select no encontrado: selectEleccionResultado');
            return;
        }

        select.innerHTML = '<option value="">Seleccione una eleccion</option>' +
            elecciones.map(e => {
                const estado = e.estado || 'ACTIVA';
                const badge = estado === 'CERRADO' ? ' [CERRADO]' : (estado === 'ABIERTO' ? ' [ABIERTO]' : '');
                return `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'}${badge}</option>`;
            }).join('');

        console.log('[Resultados] Select poblado con', elecciones.length, 'elecciones');
    } catch (error) {
        console.error('[Resultados] Error cargando elecciones:', error);
        showAlert('Error al cargar elecciones: ' + error.message, 'danger');
    }
}

/**
 * Cargar resultados de la eleccion seleccionada
 */
async function cargarResultados() {
    const eleccionId = document.getElementById('selectEleccionResultado').value;
    const container = document.getElementById('resultadosContainer');
    const statsContainer = document.getElementById('statsContainer');
    const noResultados = document.getElementById('noResultados');
    const btnExportar = document.getElementById('btnExportar');
    const btnRefresh = document.getElementById('btnRefresh');

    console.log('[Resultados] Cargando resultados para eleccion:', eleccionId);

    if (!eleccionId) {
        if (container) container.style.display = 'none';
        if (statsContainer) statsContainer.style.display = 'none';
        if (noResultados) noResultados.style.display = 'none';
        if (btnExportar) btnExportar.disabled = true;
        if (btnRefresh) btnRefresh.disabled = true;
        return;
    }

    eleccionIdActual = eleccionId;
    if (btnRefresh) btnRefresh.disabled = false;

    try {
        // Cargar resultados y participacion
        console.log('[Resultados] Llamando a GET /elecciones/' + eleccionId + '/resultados');
        const resultados = await api.get(`/elecciones/${eleccionId}/resultados`);
        console.log('[Resultados] Resultados recibidos:', resultados);

        let participacion = null;
        try {
            participacion = await api.get(`/elecciones/${eleccionId}/participacion`);
            console.log('[Resultados] Participacion:', participacion);
        } catch (e) {
            console.warn('[Resultados] Error cargando participacion:', e.message);
        }

        // Mostrar participacion
        if (statsContainer) {
            statsContainer.style.display = 'flex';
            document.getElementById('totalVotos').textContent = participacion?.votantes || resultados?.length || 0;
            document.getElementById('totalCenso').textContent = participacion?.inscritos || '-';
            document.getElementById('participacion').textContent = (participacion?.participacion || 0) + '%';
        }

        // Verificar si hay resultados
        if (!resultados || resultados.length === 0) {
            console.log('[Resultados] No hay votos registrados');
            if (container) container.style.display = 'none';
            if (noResultados) noResultados.style.display = 'block';
            if (btnExportar) btnExportar.disabled = true;
            return;
        }

        // Hay resultados
        if (noResultados) noResultados.style.display = 'none';
        if (container) container.style.display = 'flex';
        if (btnExportar) btnExportar.disabled = false;

        // Mostrar tabla de resultados
        mostrarTablaResultados(resultados);

        // Crear grafico
        crearGrafico(resultados);

        showAlert('Resultados cargados correctamente', 'success');
    } catch (error) {
        console.error('[Resultados] Error al cargar resultados:', error);
        showAlert('Error al cargar los resultados: ' + error.message, 'danger');
        if (container) container.style.display = 'none';
        if (noResultados) {
            noResultados.style.display = 'block';
            noResultados.innerHTML = `<i class="bi bi-exclamation-triangle me-2"></i>Error al cargar resultados: ${error.message}`;
            noResultados.className = 'alert alert-danger';
        }
    }
}

function mostrarTablaResultados(resultados) {
    const tbody = document.getElementById('tablaResultados');
    if (!tbody) return;

    // Calcular total de votos
    const totalVotos = resultados.reduce((sum, r) => sum + (r.votos || 0), 0);

    // Ordenar por votos descendente
    const ordenados = [...resultados].sort((a, b) => (b.votos || 0) - (a.votos || 0));

    tbody.innerHTML = ordenados.map((r, index) => {
        const porcentaje = totalVotos > 0 ? ((r.votos / totalVotos) * 100).toFixed(1) : 0;
        const esGanador = index === 0 && r.votos > 0;

        return `
            <tr class="${esGanador ? 'table-success' : ''}">
                <td>
                    ${esGanador ? '<i class="bi bi-trophy-fill text-warning me-1"></i>' : ''}
                    ${r.candidatoNombre || 'Sin nombre'}
                </td>
                <td class="text-end"><strong>${r.votos || 0}</strong></td>
                <td class="text-end">${porcentaje}%</td>
            </tr>
        `;
    }).join('');
}

function crearGrafico(resultados) {
    const canvas = document.getElementById('chartResultados');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    // Destruir grafico anterior si existe
    if (chartResultados) {
        chartResultados.destroy();
    }

    if (!resultados || resultados.length === 0) {
        return;
    }

    const labels = resultados.map(r => r.candidatoNombre || 'Sin nombre');
    const data = resultados.map(r => r.votos || 0);

    const colores = [
        'rgba(54, 162, 235, 0.8)',
        'rgba(255, 99, 132, 0.8)',
        'rgba(255, 206, 86, 0.8)',
        'rgba(75, 192, 192, 0.8)',
        'rgba(153, 102, 255, 0.8)',
        'rgba(255, 159, 64, 0.8)',
        'rgba(46, 204, 113, 0.8)',
        'rgba(155, 89, 182, 0.8)'
    ];

    chartResultados = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Votos',
                data: data,
                backgroundColor: colores.slice(0, labels.length),
                borderColor: colores.slice(0, labels.length).map(c => c.replace('0.8', '1')),
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 }
                }
            }
        }
    });
}

/**
 * Exportar resultados a CSV
 */
async function exportarCSV() {
    if (!eleccionIdActual) {
        showAlert('Debe seleccionar una eleccion', 'warning');
        return;
    }

    try {
        console.log('[Resultados] Exportando CSV para eleccion:', eleccionIdActual);
        const csv = await api.getText(`/elecciones/${eleccionIdActual}/exportar-csv`);

        // Crear blob y descargar
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `resultados_eleccion_${eleccionIdActual}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showAlert('CSV exportado correctamente', 'success');
    } catch (error) {
        console.error('[Resultados] Error exportando CSV:', error);
        showAlert('Error al exportar CSV: ' + error.message, 'danger');
    }
}

/**
 * Refrescar resultados (util durante votacion abierta)
 */
function refrescarResultados() {
    if (eleccionIdActual) {
        console.log('[Resultados] Refrescando...');
        cargarResultados();
    }
}
