package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.FacultadDTO;
import com.universidad.elecciones.dto.FacultadRequestDTO;
import com.universidad.elecciones.entity.Facultad;
import com.universidad.elecciones.repository.FacultadRepository;
import com.universidad.elecciones.repository.ProgramaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultadService {

	@Autowired
    private final FacultadRepository repo;
	@Autowired
    private final ProgramaRepository programaRepo;

    // ===============================================
    // LISTAR FACULTADES
    // ===============================================
    public List<FacultadDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR FACULTAD
    // ===============================================
    public FacultadDTO crear(FacultadRequestDTO dto) {
        Facultad facultad = Facultad.builder()
                .nombre(dto.getNombre())
                .build();
        
        Facultad saved = repo.save(facultad);
        return toDTO(saved);
    }

    // ===============================================
    // ACTUALIZAR FACULTAD
    // ===============================================
    public FacultadDTO actualizar(Long id, FacultadRequestDTO dto) {
        Facultad facultad = buscarPorIdEntity(id);
        facultad.setNombre(dto.getNombre());
        
        Facultad updated = repo.save(facultad);
        return toDTO(updated);
    }

    // ===============================================
    // ELIMINAR FACULTAD
    // ===============================================
    public void eliminar(Long id) {
        Facultad facultad = buscarPorIdEntity(id);
        
        // Validar integridad referencial: verificar si tiene programas
        if (!programaRepo.findByFacultadId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar la facultad porque tiene programas asociados");
        }
        
        repo.delete(facultad);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public FacultadDTO buscarPorId(Long id) {
        Facultad facultad = buscarPorIdEntity(id);
        return toDTO(facultad);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Facultad buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
    }

    private FacultadDTO toDTO(Facultad f) {
        return FacultadDTO.builder()
                .id(f.getId())
                .nombre(f.getNombre())
                .build();
    }
}