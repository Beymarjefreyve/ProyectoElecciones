package com.universidad.elecciones.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.CandidatoRequestDTO;
import com.universidad.elecciones.dto.CandidatoResponseDTO;
import com.universidad.elecciones.entity.Candidato;
import com.universidad.elecciones.repository.CandidatoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidatoService {

	@Autowired
	CandidatoRepository repo;

    // ===============================================
    // CREAR CANDIDATO
    // ===============================================
    public CandidatoResponseDTO crear(CandidatoRequestDTO dto) {
        // Validar documento único al crear
        validarDocumentoUnico(dto.getDocumento(), null);
        
        Candidato candidato = Candidato.builder()
                .documento(dto.getDocumento())
                .nombre(dto.getNombre())
                .imagen(dto.getImagen())
                .build();
        
        Candidato saved = repo.save(candidato);
        return toResponseDTO(saved);
    }

    // ===============================================
    // LISTAR CANDIDATOS
    // ===============================================
    public List<CandidatoResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // ACTUALIZAR CANDIDATO
    // ===============================================
    public CandidatoResponseDTO actualizar(Long id, CandidatoRequestDTO dto) {
        Candidato candidatoExistente = buscarPorIdEntity(id);
        
        // Validar documento único si está cambiando
        if (!candidatoExistente.getDocumento().equals(dto.getDocumento())) {
            validarDocumentoUnico(dto.getDocumento(), id);
        }
        
        // Actualizar campos
        candidatoExistente.setDocumento(dto.getDocumento());
        candidatoExistente.setNombre(dto.getNombre());
        candidatoExistente.setImagen(dto.getImagen());
        
        Candidato updated = repo.save(candidatoExistente);
        return toResponseDTO(updated);
    }

    // ===============================================
    // ELIMINAR CANDIDATO
    // ===============================================
    public void eliminar(Long id) {
        Candidato candidato = buscarPorIdEntity(id);
        repo.delete(candidato);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public CandidatoResponseDTO buscarPorId(Long id) {
        Candidato candidato = buscarPorIdEntity(id);
        return toResponseDTO(candidato);
    }

    // ===============================================
    // BUSCAR POR DOCUMENTO
    // ===============================================
    public CandidatoResponseDTO buscarPorDocumento(String documento) {
        Candidato candidato = buscarPorDocumentoEntity(documento);
        return toResponseDTO(candidato);
    }

    // ===============================================
    // VALIDAR DOCUMENTO ÚNICO
    // ===============================================
    public void validarDocumentoUnico(String documento, Long idExcluir) {
        Optional<Candidato> candidatoExistente = repo.findByDocumento(documento);
        
        if (candidatoExistente.isPresent()) {
            // Si se está actualizando, permitir si es el mismo candidato
            if (idExcluir != null && candidatoExistente.get().getId().equals(idExcluir)) {
                return;
            }
            throw new RuntimeException("Ya existe un candidato con el documento: " + documento);
        }
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Candidato buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado"));
    }

    private Candidato buscarPorDocumentoEntity(String documento) {
        return repo.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con documento: " + documento));
    }

    private CandidatoResponseDTO toResponseDTO(Candidato candidato) {
        return CandidatoResponseDTO.builder()
                .id(candidato.getId())
                .documento(candidato.getDocumento())
                .nombre(candidato.getNombre())
                .imagen(candidato.getImagen())
                .build();
    }
}
