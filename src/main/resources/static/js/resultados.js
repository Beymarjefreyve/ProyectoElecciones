let chartResultados = null;
let eleccionIdActual = null;

document.addEventListener('DOMContentLoaded', () => {
    cargarElecciones();
    const urlParams = new URLSearchParams(window.location.search);
    const eleccionId = urlParams.get('eleccionId');
    if (eleccionId) {
        document.getElementById('selectEleccionResultado').value = eleccionId;
        cargarResultados();
    }
});

async function cargarElecciones() {
    try {
        const elecciones = await api.get('/elecciones');
        const select = document.getElementById('selectEleccionResultado');
        select.innerHTML = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e => `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'}</option>`).join('');
    } catch (error) {
        console.error('Error cargando elecciones:', error);
    }
}

async function cargarResultados() {
    const eleccionId = document.getElementById('selectEleccionResultado').value;
    if (!eleccionId) {
        document.getElementById('resultadosContainer').style.display = 'none';
        document.getElementById('btnExportar').disabled = true;
        return;
    }

    eleccionIdActual = eleccionId;
    document.getElementById('resultadosContainer').style.display = 'block';
    document.getElementById('btnExportar').disabled = false;

    try {
        // Cargar resultados
        const [resultados, participacion, estadisticas] = await Promise.all([
            api.get(`/elecciones/${eleccionId}/resultados`),
            api.get(`/elecciones/${eleccionId}/participacion`),
            api.get(`/elecciones/${eleccionId}/estadisticas-detalladas`)
        ]);

        // Mostrar participación
        const totalVotantes = participacion?.votantes ?? 0;
        const totalInscritos = participacion?.inscritos ?? 0;
        document.getElementById('totalVotos').textContent = totalVotantes;
        document.getElementById('totalCenso').textContent = totalInscritos;
        const porcentaje = participacion?.participacion ?? (totalInscritos > 0
            ? ((totalVotantes / totalInscritos) * 100).toFixed(2)
            : 0);
        document.getElementById('participacion').textContent = `${porcentaje}%`;

        // Mostrar tabla de resultados
        const tbody = document.getElementById('tablaResultados');
        if (!resultados || resultados.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center">No hay votos registrados</td></tr>';
        } else {
            const totalVotos = resultados.reduce((sum, r) => sum + (r.votos || 0), 0);
            
            tbody.innerHTML = resultados.map(r => {
                const porcentajeFila = totalVotos > 0 ? ((r.votos / totalVotos) * 100).toFixed(2) : 0;
                return `
                    <tr>
                        <td>${r.candidatoNombre || 'Sin nombre'}</td>
                        <td>${r.votos || 0}</td>
                        <td>${porcentajeFila}%</td>
                    </tr>
                `;
            }).join('');
        }

        // Crear gráfico
        crearGrafico(resultados);

        // Mostrar estadísticas detalladas
        mostrarEstadisticas(estadisticas);
    } catch (error) {
        showAlert('Error al cargar los resultados: ' + error.message, 'danger');
    }
}

function crearGrafico(resultados) {
    const ctx = document.getElementById('chartResultados').getContext('2d');
    
    if (chartResultados) {
        chartResultados.destroy();
    }

    const labels = resultados.map(r => r.candidatoNombre || 'Sin nombre');
    const data = resultados.map(r => r.votos || 0);

    chartResultados = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Votos',
                data: data,
                backgroundColor: [
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)',
                    'rgba(255, 159, 64, 0.6)'
                ],
                borderColor: [
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 99, 132, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function mostrarEstadisticas(estadisticas) {
    if (!estadisticas) {
        document.getElementById('tablaFacultad').innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
        document.getElementById('tablaPrograma').innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
        document.getElementById('tablaSede').innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
        return;
    }

    // Por facultad
    const tbodyFacultad = document.getElementById('tablaFacultad');
    if (estadisticas.porFacultad && estadisticas.porFacultad.length > 0) {
        tbodyFacultad.innerHTML = estadisticas.porFacultad.map(f => `
            <tr><td>${f.facultadNombre || '-'}</td><td>${f.totalVotos || 0}</td></tr>
        `).join('');
    } else {
        tbodyFacultad.innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
    }

    // Por programa
    const tbodyPrograma = document.getElementById('tablaPrograma');
    if (estadisticas.porPrograma && estadisticas.porPrograma.length > 0) {
        tbodyPrograma.innerHTML = estadisticas.porPrograma.map(p => `
            <tr><td>${p.programaNombre || '-'}</td><td>${p.totalVotos || 0}</td></tr>
        `).join('');
    } else {
        tbodyPrograma.innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
    }

    // Por sede
    const tbodySede = document.getElementById('tablaSede');
    if (estadisticas.porSede && estadisticas.porSede.length > 0) {
        tbodySede.innerHTML = estadisticas.porSede.map(s => `
            <tr><td>${s.sedeNombre || '-'}</td><td>${s.totalVotos || 0}</td></tr>
        `).join('');
    } else {
        tbodySede.innerHTML = '<tr><td colspan="2" class="text-center">No hay datos</td></tr>';
    }
}

async function exportarCSV() {
    if (!eleccionIdActual) {
        showAlert('Debe seleccionar una elección', 'warning');
        return;
    }

    try {
        const csv = await api.getText(`/elecciones/${eleccionIdActual}/exportar-csv`);
        const blob = new Blob([csv], { type: 'text/csv' });
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
        showAlert('Error al exportar CSV: ' + error.message, 'danger');
    }
}

