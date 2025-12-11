package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.ProgramaDTO;
import com.universidad.elecciones.dto.ProgramaRequestDTO;
import com.universidad.elecciones.entity.Programa;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.FacultadRepository;
import com.universidad.elecciones.repository.ProgramaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramaService {

    @Autowired
    private final ProgramaRepository repo;
    @Autowired
    private final FacultadRepository facultadRepo;
    @Autowired
    private final EleccionRepository eleccionRepo;

    // ===============================================
    // LISTAR PROGRAMAS
    // ===============================================
    public List<ProgramaDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // LISTAR PROGRAMAS por Facultad
    // ===============================================
    public List<ProgramaDTO> listarPorFacultad(Long facultadId) {
        return repo.findByFacultadId(facultadId)
                .stream()
                .map(p -> new ProgramaDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getFacultad().getId()))
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR PROGRAMA
    // ===============================================
    public ProgramaDTO crear(ProgramaRequestDTO dto) {
        // Validar que la facultad existe
        var facultad = facultadRepo.findById(dto.getFacultadId())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        Programa programa = Programa.builder()
                .nombre(dto.getNombre())
                .facultad(facultad)
                .build();

        Programa saved = repo.save(programa);
        return toDTO(saved);
    }

    // ===============================================
    // ACTUALIZAR PROGRAMA
    // ===============================================
    public ProgramaDTO actualizar(Long id, ProgramaRequestDTO dto) {
        Programa programa = buscarPorIdEntity(id);

        // Validar que la facultad existe si está cambiando
        if (!programa.getFacultad().getId().equals(dto.getFacultadId())) {
            var facultad = facultadRepo.findById(dto.getFacultadId())
                    .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
            programa.setFacultad(facultad);
        }

        programa.setNombre(dto.getNombre());

        Programa updated = repo.save(programa);
        return toDTO(updated);
    }

    // ===============================================
    // ELIMINAR PROGRAMA
    // ===============================================
    public void eliminar(Long id) {
        Programa programa = buscarPorIdEntity(id);

        // Validar integridad referencial: verificar si está en elecciones o solicitudes
        if (!eleccionRepo.findByProgramaId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el programa porque está asociado a elecciones");
        }

        repo.delete(programa);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public ProgramaDTO buscarPorId(Long id) {
        Programa programa = buscarPorIdEntity(id);
        return toDTO(programa);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================

    private Programa buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
    }

    private ProgramaDTO toDTO(Programa p) {
        return ProgramaDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .facultadId(p.getFacultad().getId())
                .build();
    }
}