package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.SedeRequestDTO;
import com.universidad.elecciones.dto.SedeResponseDTO;
import com.universidad.elecciones.entity.Sede;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.SedeRepository;
import com.universidad.elecciones.repository.SolicitudRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeService {

	@Autowired
    private final SedeRepository repo;
	@Autowired
    private final EleccionRepository eleccionRepo;
	@Autowired
    private final SolicitudRepository solicitudRepo;

    // ===============================================
    // LISTAR SEDES
    // ===============================================
    public List<SedeResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR SEDE
    // ===============================================
    public SedeResponseDTO crear(SedeRequestDTO dto) {
        Sede sede = Sede.builder()
                .nombre(dto.getNombre())
                .build();
        
        Sede saved = repo.save(sede);
        return toDTO(saved);
    }

    // ===============================================
    // ACTUALIZAR SEDE
    // ===============================================
    public SedeResponseDTO actualizar(Long id, SedeRequestDTO dto) {
        Sede sede = buscarPorIdEntity(id);
        sede.setNombre(dto.getNombre());
        
        Sede updated = repo.save(sede);
        return toDTO(updated);
    }

    // ===============================================
    // ELIMINAR SEDE
    // ===============================================
    public void eliminar(Long id) {
        Sede sede = buscarPorIdEntity(id);
        
        // Validar integridad referencial: verificar si está en elecciones o solicitudes
        if (!eleccionRepo.findBySedeId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar la sede porque está asociada a elecciones");
        }
        
        if (!solicitudRepo.findBySedeId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar la sede porque está asociada a solicitudes");
        }
        
        repo.delete(sede);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public SedeResponseDTO buscarPorId(Long id) {
        Sede sede = buscarPorIdEntity(id);
        return toDTO(sede);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Sede buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }

    private SedeResponseDTO toDTO(Sede s) {
        return SedeResponseDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .build();
    }
}