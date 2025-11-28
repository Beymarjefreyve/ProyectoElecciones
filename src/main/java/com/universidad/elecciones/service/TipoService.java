package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.TipoRequestDTO;
import com.universidad.elecciones.dto.TipoResponseDTO;
import com.universidad.elecciones.entity.Tipo;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.TipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoService {

	@Autowired
    private final TipoRepository tipoRepository;
	@Autowired
    private final EleccionRepository eleccionRepo;

    // ===============================================
    // LISTAR
    // ===============================================
    public List<TipoResponseDTO> listar() {
        return tipoRepository.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR
    // ===============================================
    public TipoResponseDTO crear(TipoRequestDTO dto) {
        Tipo tipo = new Tipo();
        tipo.setNombre(dto.getNombre());

        Tipo saved = tipoRepository.save(tipo);
        return buildDTO(saved);
    }

    // ===============================================
    // ACTUALIZAR
    // ===============================================
    public TipoResponseDTO actualizar(Long id, TipoRequestDTO dto) {
        Tipo tipo = buscarPorIdEntity(id);
        tipo.setNombre(dto.getNombre());
        
        Tipo updated = tipoRepository.save(tipo);
        return buildDTO(updated);
    }

    // ===============================================
    // ELIMINAR
    // ===============================================
    public void eliminar(Long id) {
        Tipo tipo = buscarPorIdEntity(id);
        
        // Validar integridad referencial: verificar si está en elecciones
        if (!eleccionRepo.findByTipoId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el tipo porque está asociado a elecciones");
        }
        
        tipoRepository.delete(tipo);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public TipoResponseDTO buscarPorId(Long id) {
        Tipo tipo = buscarPorIdEntity(id);
        return buildDTO(tipo);
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Tipo buscarPorIdEntity(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
    }

    // ===============================================
    // MAPPER ENTITY → DTO
    // ===============================================
    private TipoResponseDTO buildDTO(Tipo tipo) {
        return TipoResponseDTO.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .build();
    }
}