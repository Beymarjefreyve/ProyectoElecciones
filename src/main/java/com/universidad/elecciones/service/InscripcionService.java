package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.InscripcionRequest;
import com.universidad.elecciones.dto.InscripcionResponse;
import com.universidad.elecciones.dto.InscripcionUpdateRequest;
import com.universidad.elecciones.entity.Candidato;
import com.universidad.elecciones.entity.Eleccion;
import com.universidad.elecciones.entity.Inscripcion;
import com.universidad.elecciones.repository.CandidatoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.InscripcionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

	@Autowired
    private final InscripcionRepository repo;
	@Autowired
    private final EleccionRepository eleccionRepo;
	@Autowired
    private final CandidatoRepository candidatoRepo;

    // ===============================================
    // INSCRIBIR CANDIDATO EN ELECCIÓN
    // ===============================================
    public InscripcionResponse inscribir(Long eleccionId, InscripcionRequest request) {

        Eleccion eleccion = eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        // Validar que la elección esté activa para inscribir candidatos
        String estado = eleccion.getEstado() != null ? eleccion.getEstado().toUpperCase() : "";
        if (!estado.equals("ACTIVA") && !estado.equals("ABIERTO")) {
            throw new RuntimeException("No se pueden inscribir candidatos en una elección con estado " + eleccion.getEstado());
        }

        Candidato candidato = candidatoRepo.findById(request.getCandidatoId())
                .orElseThrow(() -> new RuntimeException("Candidato no existe"));

        // validar si ya se inscribió antes
        if (repo.existsByCandidatoIdAndEleccionId(request.getCandidatoId(), eleccionId)) {
            throw new RuntimeException("Este candidato ya está inscrito en esta elección");
        }

        // Validar número único en la elección
        validarNumeroUnicoEnEleccion(request.getNumero(), eleccionId, null);

        Inscripcion ins = Inscripcion.builder()
                .eleccion(eleccion)
                .candidato(candidato)
                .numero(request.getNumero())
                .estado("ACTIVO")
                .fecha(LocalDateTime.now())
                .build();

        Inscripcion saved = repo.save(ins);

        return buildResponseDTO(saved);
    }

    // ===============================================
    // LISTAR CANDIDATOS DE UNA ELECCIÓN
    // ===============================================
    public List<InscripcionResponse> listarCandidatos(Long eleccionId) {

        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        // Solo devolver inscripciones activas para que en la votación
        // solo aparezcan candidatos habilitados
        return repo.findByEleccionId(eleccionId).stream()
                .filter(ins -> "ACTIVO".equalsIgnoreCase(ins.getEstado()))
                .map(this::buildResponseDTO)
                .toList();
    }

    // ===============================================
    // ACTUALIZAR INSCRIPCIÓN
    // ===============================================
    public InscripcionResponse actualizar(Long id, InscripcionUpdateRequest request) {
        Inscripcion inscripcion = buscarPorIdEntity(id);
        
        // Validar número único si está cambiando
        if (request.getNumero() != null && !request.getNumero().equals(inscripcion.getNumero())) {
            validarNumeroUnicoEnEleccion(request.getNumero(), inscripcion.getEleccion().getId(), id);
            inscripcion.setNumero(request.getNumero());
        }
        
        // Actualizar estado si se proporciona
        if (request.getEstado() != null && !request.getEstado().trim().isEmpty()) {
            validarEstado(request.getEstado());
            inscripcion.setEstado(request.getEstado().toUpperCase());
        }
        
        Inscripcion updated = repo.save(inscripcion);
        return buildResponseDTO(updated);
    }

    // ===============================================
    // ELIMINAR INSCRIPCIÓN
    // ===============================================
    public void eliminar(Long id) {
        Inscripcion inscripcion = buscarPorIdEntity(id);
        repo.delete(inscripcion);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public InscripcionResponse buscarPorId(Long id) {
        Inscripcion inscripcion = buscarPorIdEntity(id);
        return buildResponseDTO(inscripcion);
    }

    // ===============================================
    // VALIDAR NÚMERO ÚNICO EN ELECCIÓN
    // ===============================================
    public void validarNumeroUnicoEnEleccion(Integer numero, Long eleccionId, Long idExcluir) {
        if (numero == null) {
            throw new RuntimeException("El número es obligatorio");
        }
        
        // Validar que la elección existe
        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));
        
        // Verificar si el número ya existe en la elección
        if (repo.existsByNumeroAndEleccionId(numero, eleccionId)) {
            // Si se está actualizando, permitir si es la misma inscripción
            if (idExcluir != null) {
                Inscripcion inscripcionExistente = repo.findByNumeroAndEleccionId(numero, eleccionId)
                        .orElse(null);
                if (inscripcionExistente != null && inscripcionExistente.getId().equals(idExcluir)) {
                    return; // Es la misma inscripción, permitir
                }
            }
            throw new RuntimeException("El número " + numero + " ya está asignado a otro candidato en esta elección");
        }
    }

    // ===============================================
    // CAMBIAR ESTADO DE INSCRIPCIÓN
    // ===============================================
    public InscripcionResponse cambiarEstado(Long id, String nuevoEstado) {
        Inscripcion inscripcion = buscarPorIdEntity(id);
        
        validarEstado(nuevoEstado);
        inscripcion.setEstado(nuevoEstado.toUpperCase());
        
        Inscripcion updated = repo.save(inscripcion);
        return buildResponseDTO(updated);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Inscripcion buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new RuntimeException("El estado no puede estar vacío");
        }
        
        String estadoUpper = estado.toUpperCase();
        if (!estadoUpper.equals("ACTIVO") 
                && !estadoUpper.equals("RETIRADO") 
                && !estadoUpper.equals("SUSPENDIDO")
                && !estadoUpper.equals("INACTIVO")) {
            throw new RuntimeException("Estado inválido. Debe ser: ACTIVO, RETIRADO, SUSPENDIDO o INACTIVO");
        }
    }

    private InscripcionResponse buildResponseDTO(Inscripcion ins) {
        return InscripcionResponse.builder()
                .id(ins.getId())
                .candidatoId(ins.getCandidato().getId())
                .candidatoNombre(ins.getCandidato().getNombre())
                .candidatoImagen(ins.getCandidato().getImagen())
                .numero(ins.getNumero())
                .fecha(ins.getFecha())
                .estado(ins.getEstado())
                .build();
    }
}