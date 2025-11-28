package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.VotanteRequestDTO;
import com.universidad.elecciones.dto.VotanteResponseDTO;
import com.universidad.elecciones.entity.Votante;
import com.universidad.elecciones.repository.CensoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.VotanteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotanteService {

	@Autowired
    private final VotanteRepository repo;
	@Autowired
    private final CensoRepository censoRepo;
	@Autowired
    private final EleccionRepository eleccionRepo;

    // ===============================================
    // REGISTRAR UN VOTANTE
    // ===============================================
    public VotanteResponseDTO registrar(VotanteRequestDTO dto) {

        if (repo.existsByDocumento(dto.getDocumento())) {
            throw new RuntimeException("Este documento ya existe");
        }

        Votante v = new Votante();
        v.setDocumento(dto.getDocumento());
        v.setNombre(dto.getNombre());

        Votante saved = repo.save(v);

        return buildDTO(saved);
    }

    // ===============================================
    // LISTAR
    // ===============================================
    public List<VotanteResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // ACTUALIZAR VOTANTE
    // ===============================================
    public VotanteResponseDTO actualizar(Long id, VotanteRequestDTO dto) {
        Votante votanteExistente = buscarPorIdEntity(id);
        
        // Validar documento único si está cambiando
        if (!votanteExistente.getDocumento().equals(dto.getDocumento())) {
            if (repo.existsByDocumento(dto.getDocumento())) {
                throw new RuntimeException("Ya existe un votante con el documento: " + dto.getDocumento());
            }
        }
        
        // Actualizar campos
        votanteExistente.setDocumento(dto.getDocumento());
        votanteExistente.setNombre(dto.getNombre());
        
        Votante updated = repo.save(votanteExistente);
        return buildDTO(updated);
    }

    // ===============================================
    // ELIMINAR VOTANTE
    // ===============================================
    public void eliminar(Long id) {
        Votante votante = buscarPorIdEntity(id);
        repo.delete(votante);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public VotanteResponseDTO buscarPorId(Long id) {
        Votante votante = buscarPorIdEntity(id);
        return buildDTO(votante);
    }

    // ===============================================
    // BUSCAR POR DOCUMENTO
    // ===============================================
    public VotanteResponseDTO buscarPorDocumento(String documento) {
        Votante votante = buscarPorDocumentoEntity(documento);
        return buildDTO(votante);
    }

    // ===============================================
    // VALIDAR SI PUEDE VOTAR EN UNA ELECCIÓN
    // ===============================================
    public boolean validarSiPuedeVotar(String documento, Long eleccionId) {
        // Validar que la elección existe
        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));
        
        // Validar que el votante existe (si no existe, lanza excepción)
        buscarPorDocumentoEntity(documento);
        
        // Validar que el votante está en el censo de la elección
        return censoRepo.findByVotanteDocumentoAndEleccionId(documento, eleccionId)
                .isPresent();
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Votante buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));
    }

    private Votante buscarPorDocumentoEntity(String documento) {
        return repo.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado con documento: " + documento));
    }

    // ===============================================
    // Mapper ENTITY → DTO
    // ===============================================
    private VotanteResponseDTO buildDTO(Votante v) {
        return VotanteResponseDTO.builder()
                .id(v.getId())
                .documento(v.getDocumento())
                .nombre(v.getNombre())
                .build();
    }
}