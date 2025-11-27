package com.universidad.elecciones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.EleccionRequestDTO;
import com.universidad.elecciones.dto.EleccionResponseDTO;
import com.universidad.elecciones.entity.Eleccion;
import com.universidad.elecciones.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EleccionService {

	@Autowired
    private final EleccionRepository repo;
	@Autowired
    private final TipoEleccionRepository tipoEleccionRepo;
	@Autowired
    private final TipoRepository tipoRepo;
	@Autowired
    private final ProgramaRepository programaRepo;
	@Autowired
    private final SedeRepository sedeRepo;
	@Autowired
    private final ProcesoRepository procesoRepo;
	@Autowired
    private final FacultadRepository facultadRepo;

    // ========================================================
    // CREAR ELECCIÓN
    // ========================================================
    public EleccionResponseDTO crear(EleccionRequestDTO dto) {

        Eleccion e = new Eleccion();

        // FK obligatorias
        e.setTipoEleccion(
                tipoEleccionRepo.findById(dto.getTipoEleccionId())
                        .orElseThrow(() -> new RuntimeException("Tipo de elección no existe"))
        );

        e.setTipo(
                tipoRepo.findById(dto.getTipoId())
                        .orElseThrow(() -> new RuntimeException("Tipo no existe"))
        );

        e.setProceso(
                procesoRepo.findById(dto.getProcesoId())
                        .orElseThrow(() -> new RuntimeException("Proceso no existe"))
        );

        // FK opcionales
        if (dto.getProgramaId() != null)
            e.setPrograma(
                    programaRepo.findById(dto.getProgramaId())
                            .orElseThrow(() -> new RuntimeException("Programa no existe"))
            );

        if (dto.getSedeId() != null)
            e.setSede(
                    sedeRepo.findById(dto.getSedeId())
                            .orElseThrow(() -> new RuntimeException("Sede no existe"))
            );

        if (dto.getFacultadId() != null)
            e.setFacultad(
                    facultadRepo.findById(dto.getFacultadId())
                            .orElseThrow(() -> new RuntimeException("Facultad no existe"))
            );

        // Campos simples
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setAnio(dto.getAnio());
        e.setSemestre(dto.getSemestre());
        e.setFechaInicio(dto.getFechaInicio());
        e.setFechaFinaliza(dto.getFechaFinaliza());

        // Campos generados
        e.setFechaCreacion(LocalDateTime.now());
        e.setEstado("ACTIVA");
        e.setExtendido(false);

        Eleccion saved = repo.save(e);
        return buildDTO(saved);
    }
    
    
    
    
    public EleccionResponseDTO obtenerPorId(Long id) {
        Eleccion e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        return buildDTO(e);
    }
    
    
    

    // ========================================================
    // LISTAR ELECCIONES
    // ========================================================
    public List<EleccionResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // BUSCAR ELECCIÓN POR ID
    // ========================================================
    public EleccionResponseDTO buscarPorId(Long id) {
        Eleccion e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));
        return buildDTO(e);
    }

    // ========================================================
    // CAMBIAR ESTADO (ACTIVAR / CERRAR)
    // ========================================================
 // ========================================================
 // CAMBIAR ESTADO DE LA ELECCIÓN
 // ========================================================
 public EleccionResponseDTO cambiarEstado(Long id, String nuevoEstado) {

     Eleccion e = repo.findById(id)
             .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

     // Validar valores permitidos
     if (!nuevoEstado.equalsIgnoreCase("ABIERTO")
             && !nuevoEstado.equalsIgnoreCase("CERRADO")) {
         throw new RuntimeException("Estado inválido. Debe ser ABIERTO o CERRADO");
     }

     e.setEstado(nuevoEstado.toUpperCase());

     Eleccion saved = repo.save(e);
     return buildDTO(saved);
 }

    // ========================================================
    // CONVERTIR A DTO
    // ========================================================
    private EleccionResponseDTO buildDTO(Eleccion e) {
        return EleccionResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .anio(e.getAnio())
                .semestre(e.getSemestre())
                .estado(e.getEstado())
                .fechaInicio(e.getFechaInicio())
                .fechaFinaliza(e.getFechaFinaliza())
                .tipoEleccionId(e.getTipoEleccion().getId())
                .tipoId(e.getTipo().getId())
                .programaId(e.getPrograma() != null ? e.getPrograma().getId() : null)
                .sedeId(e.getSede() != null ? e.getSede().getId() : null)
                .facultadId(e.getFacultad() != null ? e.getFacultad().getId() : null)
                .build();
    }
}