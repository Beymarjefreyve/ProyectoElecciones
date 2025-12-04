let modalCandidato;
let editMode = false;

document.addEventListener('DOMContentLoaded', () => {
    modalCandidato = new bootstrap.Modal(document.getElementById('modalCandidato'));
    cargarCandidatos();
    document.getElementById('modalCandidato').addEventListener('hidden.bs.modal', () => {
        document.getElementById('formCandidato').reset();
        document.getElementById('candidatoId').value = '';
        editMode = false;
        document.getElementById('modalCandidatoTitle').textContent = 'Nuevo Candidato';
    });
});

async function cargarCandidatos() {
    try {
        const candidatos = await api.get('/candidatos');
        const tbody = document.getElementById('candidatosTable');
        if (candidatos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No hay candidatos registrados</td></tr>';
            return;
        }
        tbody.innerHTML = candidatos.map(candidato => `
            <tr>
                <td>${candidato.id}</td>
                <td>${candidato.documento}</td>
                <td>${candidato.nombre}</td>
				
				
				
				
				
                <td>${candidato.imagen ? `<img src="${candidato.imagen}" alt="${candidato.nombre}" style="max-width: 50px; max-height: 50px;">` : '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarCandidato(${candidato.id})">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarCandidato(${candidato.id}, '${candidato.nombre}')">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Error al cargar los candidatos: ' + error.message, 'danger');
        document.getElementById('candidatosTable').innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error al cargar datos</td></tr>';
    }
}

async function editarCandidato(id) {
    try {
        const candidato = await api.get(`/candidatos/${id}`);
        document.getElementById('candidatoId').value = candidato.id;
        document.getElementById('candidatoDocumento').value = candidato.documento;
        document.getElementById('candidatoNombre').value = candidato.nombre;
        document.getElementById('candidatoImagen').value = candidato.imagen || '';
        editMode = true;
        document.getElementById('modalCandidatoTitle').textContent = 'Editar Candidato';
        modalCandidato.show();
    } catch (error) {
        showAlert('Error al cargar el candidato: ' + error.message, 'danger');
    }
}

async function guardarCandidato() {
    const id = document.getElementById('candidatoId').value;
    const documento = document.getElementById('candidatoDocumento').value.trim();
    const nombre = document.getElementById('candidatoNombre').value.trim();
    const imagen = document.getElementById('candidatoImagen').value.trim();

    if (!documento || !nombre) {
        showAlert('Documento y nombre son obligatorios', 'warning');
        return;
    }
	// ⭐ VALIDACIÓN DEL FORMATO DE LA IMAGEN ⭐
	    if (imagen && !imagen.match(/\.(jpeg|jpg|png|gif|webp)$/i)) {
	        showAlert('La URL debe ser una imagen válida (jpg, png, gif, webp)', 'warning');
	        return;
	    }	

    try {
        if (editMode && id) {
            await api.put(`/candidatos/${id}`, { documento, nombre, imagen: imagen || null });
            showAlert('Candidato actualizado correctamente', 'success');
        } else {
            await api.post('/candidatos', { documento, nombre, imagen: imagen || null });
            showAlert('Candidato creado correctamente', 'success');
        }
        modalCandidato.hide();
        cargarCandidatos();
		} catch (error) {
		       // Detecta si el mensaje viene del backend indicando que el documento ya existe
		       if (error.message && error.message.includes('Ya existe un candidato')) {
		           showAlert('El documento ya está registrado', 'warning');
		       } else {
		           showAlert('Error al guardar: ' + error.message, 'danger');
		       }
		   }
}


async function eliminarCandidato(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el candidato "${nombre}"?`)) return;
    try {
        await api.delete(`/candidatos/${id}`);
        showAlert('Candidato eliminado correctamente', 'success');
        cargarCandidatos();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'danger');
    }
}

