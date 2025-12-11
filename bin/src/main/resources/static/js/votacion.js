let candidatoSeleccionado = null;

document.addEventListener('DOMContentLoaded', () => {
    cargarEleccionesAbiertas();
});

async function cargarEleccionesAbiertas() {
    try {
        const elecciones = await api.get('/elecciones/abiertas');
        const select = document.getElementById('selectEleccionVoto');
        if (elecciones.length === 0) {
            select.innerHTML = '<option value="">No hay elecciones abiertas disponibles</option>';
            return;
        }
        select.innerHTML = '<option value="">Seleccione una elección</option>' +
            elecciones.map(e => `<option value="${e.id}">${e.id} - ${e.nombre || 'Sin nombre'}</option>`).join('');
    } catch (error) {
        console.error('Error cargando elecciones:', error);
        showAlert('Error al cargar las elecciones: ' + error.message, 'danger');
    }
}

async function cargarCandidatos() {
    const eleccionId = document.getElementById('selectEleccionVoto').value;
    if (!eleccionId) {
        document.getElementById('cardCandidatos').style.display = 'none';
        return;
    }

    try {
        const candidatos = await api.get(`/elecciones/${eleccionId}/candidatos`);
        const container = document.getElementById('candidatosContainer');
        
        if (candidatos.length === 0) {
            container.innerHTML = '<div class="col-12 text-center"><p>No hay candidatos inscritos en esta elección</p></div>';
            document.getElementById('cardCandidatos').style.display = 'block';
            document.getElementById('btnVotar').disabled = true;
            return;
        }

        container.innerHTML = candidatos.map(candidato => `
            <div class="col-md-4 mb-3">
                <div class="card candidato-card" onclick="seleccionarCandidato(${candidato.candidatoId}, this)">
                    <div class="card-body text-center">
                        ${candidato.candidatoImagen ? `<img src="${candidato.candidatoImagen}" alt="${candidato.candidatoNombre}" class="img-fluid mb-2" style="max-height: 150px;">` : ''}
                        <h5 class="card-title">${candidato.candidatoNombre || 'Sin nombre'}</h5>
                        <p class="card-text">Número: ${candidato.numero || '-'}</p>
                        <p class="card-text"><small class="text-muted">Estado: ${candidato.estado || '-'}</small></p>
                    </div>
                </div>
            </div>
        `).join('');

        document.getElementById('cardCandidatos').style.display = 'block';
        document.getElementById('btnVotar').disabled = true;
        candidatoSeleccionado = null;
    } catch (error) {
        showAlert('Error al cargar los candidatos: ' + error.message, 'danger');
    }
}

function seleccionarCandidato(candidatoId, element) {
    // Remover selección anterior
    document.querySelectorAll('.candidato-card').forEach(card => {
        card.classList.remove('border-primary', 'border-3');
        card.style.backgroundColor = '';
    });
    
    // Seleccionar nuevo candidato
    element.classList.add('border-primary', 'border-3');
    element.style.backgroundColor = '#e7f3ff';
    candidatoSeleccionado = candidatoId;
    document.getElementById('btnVotar').disabled = false;
}

async function votar() {
    const eleccionId = document.getElementById('selectEleccionVoto').value;
    const documento = document.getElementById('documentoVotante').value.trim();

    if (!eleccionId || !documento || !candidatoSeleccionado) {
        showAlert('Debe seleccionar una elección, ingresar su documento y seleccionar un candidato', 'warning');
        return;
    }

    if (!confirm('¿Está seguro de confirmar su voto? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const resultado = await api.post(`/elecciones/${eleccionId}/votar`, {
            candidatoId: candidatoSeleccionado,
            documento: documento
        });
        
        showAlert('¡Voto registrado correctamente!', 'success');
        
        // Limpiar formulario
        document.getElementById('selectEleccionVoto').value = '';
        document.getElementById('documentoVotante').value = '';
        document.getElementById('cardCandidatos').style.display = 'none';
        candidatoSeleccionado = null;
        
        // Recargar elecciones
        cargarEleccionesAbiertas();
    } catch (error) {
        showAlert('Error al votar: ' + error.message, 'danger');
    }
}

