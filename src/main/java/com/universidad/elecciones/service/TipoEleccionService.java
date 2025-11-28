package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.TipoEleccionRequestDTO;
import com.universidad.elecciones.dto.TipoEleccionResponseDTO;
import com.universidad.elecciones.entity.TipoEleccion;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.TipoEleccionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoEleccionService {

	@Autowired
    private final TipoEleccionRepository repo;
	@Autowired
    private final EleccionRepository eleccionRepo;

    // ===============================================
    // LISTAR TIPOS DE ELECCIÓN
    // ===============================================
    public List<TipoEleccionResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR TIPO DE ELECCIÓN
    // ===============================================
    public TipoEleccionResponseDTO crear(TipoEleccionRequestDTO dto) {
        TipoEleccion tipoEleccion = TipoEleccion.builder()
                .nombre(dto.getNombre())
                .build();
        
        TipoEleccion saved = repo.save(tipoEleccion);
        return toDTO(saved);
    }

    // ===============================================
    // ACTUALIZAR TIPO DE ELECCIÓN
    // ===============================================
    public TipoEleccionResponseDTO actualizar(Long id, TipoEleccionRequestDTO dto) {
        TipoEleccion tipoEleccion = buscarPorIdEntity(id);
        tipoEleccion.setNombre(dto.getNombre());
        
        TipoEleccion updated = repo.save(tipoEleccion);
        return toDTO(updated);
    }

    // ===============================================
    // ELIMINAR TIPO DE ELECCIÓN
    // ===============================================
    public void eliminar(Long id) {
        TipoEleccion tipoEleccion = buscarPorIdEntity(id);
        
        // Validar integridad referencial: verificar si está en elecciones
        if (!eleccionRepo.findByTipoEleccionId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el tipo de elección porque está asociado a elecciones");
        }
        
        repo.delete(tipoEleccion);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public TipoEleccionResponseDTO buscarPorId(Long id) {
        TipoEleccion tipoEleccion = buscarPorIdEntity(id);
        return toDTO(tipoEleccion);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private TipoEleccion buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de elección no encontrado"));
    }

    private TipoEleccionResponseDTO toDTO(TipoEleccion t) {
        return TipoEleccionResponseDTO.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .build();
    }
}