package com.universidad.elecciones.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.ProcesoRequestDTO;
import com.universidad.elecciones.dto.ProcesoResponseDTO;
import com.universidad.elecciones.entity.Proceso;
import com.universidad.elecciones.repository.ProcesoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesoService {

	@Autowired
    private final ProcesoRepository repo;

    // ===============================================
    // CREAR PROCESO
    // ===============================================
    public ProcesoResponseDTO crear(ProcesoRequestDTO dto) {
        // Validar fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());
        
        Proceso proceso = Proceso.builder()
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .build();
        
        Proceso saved = repo.save(proceso);
        return toResponseDTO(saved);
    }

    // ===============================================
    // LISTAR PROCESOS
    // ===============================================
    public List<ProcesoResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // ACTUALIZAR PROCESO
    // ===============================================
    public ProcesoResponseDTO actualizar(Long id, ProcesoRequestDTO dto) {
        Proceso proceso = buscarPorIdEntity(id);
        
        // Validar fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());
        
        proceso.setDescripcion(dto.getDescripcion());
        proceso.setFechaInicio(dto.getFechaInicio());
        proceso.setFechaFin(dto.getFechaFin());
        
        Proceso updated = repo.save(proceso);
        return toResponseDTO(updated);
    }

    // ===============================================
    // ELIMINAR PROCESO
    // ===============================================
    public void eliminar(Long id) {
        Proceso proceso = buscarPorIdEntity(id);
        repo.delete(proceso);
    }

    // ===============================================
    // BUSCAR POR ID
    // ===============================================
    public ProcesoResponseDTO buscarPorId(Long id) {
        Proceso proceso = buscarPorIdEntity(id);
        return toResponseDTO(proceso);
    }

    // ===============================================
    // VALIDAR FECHAS (inicio < fin)
    // ===============================================
    public void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas de inicio y fin son obligatorias");
        }
        
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new RuntimeException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    // ===============================================
    // LISTAR PROCESOS ACTIVOS
    // ===============================================
    public List<ProcesoResponseDTO> listarActivos() {
        LocalDate hoy = LocalDate.now();
        
        return repo.findAll()
                .stream()
                .filter(p -> !hoy.isBefore(p.getFechaInicio()) && !hoy.isAfter(p.getFechaFin()))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // FILTRAR POR RANGO DE FECHAS
    // ===============================================
    public List<ProcesoResponseDTO> filtrarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas de inicio y fin son obligatorias para el filtro");
        }
        
        if (!fechaInicio.isBefore(fechaFin) && !fechaInicio.isEqual(fechaFin)) {
            throw new RuntimeException("La fecha de inicio del rango debe ser anterior o igual a la fecha de fin");
        }
        
        // Buscar procesos que se solapen con el rango de fechas
        List<Proceso> procesos = repo.findAll()
                .stream()
                .filter(p -> {
                    // Un proceso se solapa si:
                    // - Su fecha de inicio está dentro del rango, o
                    // - Su fecha de fin está dentro del rango, o
                    // - El proceso contiene completamente el rango
                    return (!p.getFechaInicio().isAfter(fechaFin) && !p.getFechaFin().isBefore(fechaInicio));
                })
                .collect(Collectors.toList());
        
        return procesos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================================
    
    private Proceso buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }

    private ProcesoResponseDTO toResponseDTO(Proceso proceso) {
        return ProcesoResponseDTO.builder()
                .id(proceso.getId())
                .descripcion(proceso.getDescripcion())
                .fechaInicio(proceso.getFechaInicio())
                .fechaFin(proceso.getFechaFin())
                .build();
    }
}
