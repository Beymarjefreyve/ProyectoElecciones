package com.universidad.elecciones.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.VotanteRequestDTO;
import com.universidad.elecciones.dto.VotanteResponseDTO;
import com.universidad.elecciones.entity.Censo;
import com.universidad.elecciones.entity.Eleccion;
import com.universidad.elecciones.entity.Votante;
import com.universidad.elecciones.repository.CensoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.ResultadoRepository;
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
    @Autowired
    private final ResultadoRepository resultadoRepo;

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
    public Map<String, Object> validarSiPuedeVotar(String documento, Long eleccionId) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("puedeVotar", false);

        try {
            // 1. Validar que la elección existe
            Eleccion eleccion = eleccionRepo.findById(eleccionId)
                    .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

            // 2. Validar que el votante existe
            Votante votante = buscarPorDocumentoEntity(documento);

            // 3. Validar estado del votante
            if (votante.getEstado() != null && !"ACTIVO".equalsIgnoreCase(votante.getEstado())) {
                resultado.put("mensaje", "El votante no está activo. Estado actual: " + votante.getEstado());
                return resultado;
            }

            // 4. Validar que el votante está en el censo
            Optional<Censo> censoOpt = censoRepo.findByVotanteDocumentoAndEleccionId(documento, eleccionId);
            if (censoOpt.isEmpty()) {
                resultado.put("mensaje", "El votante NO está habilitado en el censo para esta elección");
                return resultado;
            }

            // 5. Validar restricción de facultad
            if (eleccion.getFacultad() != null) {
                if (votante.getFacultad() == null ||
                        !votante.getFacultad().getId().equals(eleccion.getFacultad().getId())) {
                    resultado.put("mensaje", "El votante no pertenece a la facultad requerida: "
                            + eleccion.getFacultad().getNombre());
                    return resultado;
                }
            }

            // 6. Validar restricción de programa
            if (eleccion.getPrograma() != null) {
                if (votante.getPrograma() == null ||
                        !votante.getPrograma().getId().equals(eleccion.getPrograma().getId())) {
                    resultado.put("mensaje", "El votante no pertenece al programa requerido: "
                            + eleccion.getPrograma().getNombre());
                    return resultado;
                }
            }

            // 7. Verificar si ya votó
            Censo censo = censoOpt.get();
            if (resultadoRepo.existsByEleccionIdAndCensoId(eleccionId, censo.getId())) {
                resultado.put("mensaje", "El votante ya emitió su voto en esta elección");
                resultado.put("yaVoto", true);
                return resultado;
            }

            // Todas las validaciones pasaron
            resultado.put("puedeVotar", true);
            resultado.put("mensaje", "El votante está habilitado para votar en esta elección");
            return resultado;

        } catch (RuntimeException e) {
            resultado.put("mensaje", e.getMessage());
            return resultado;
        }
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