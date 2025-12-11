package com.universidad.elecciones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.EleccionRequestDTO;
import com.universidad.elecciones.dto.EleccionResponseDTO;
import com.universidad.elecciones.entity.Eleccion;
import com.universidad.elecciones.entity.Votante;
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
    @Autowired
    private final VotanteRepository votanteRepo;
    @Autowired
    private final CensoRepository censoRepo;

    // ========================================================
    // CREAR ELECCIÓN
    // ========================================================
    public EleccionResponseDTO crear(EleccionRequestDTO dto) {
        // Validar fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFinaliza());

        Eleccion e = new Eleccion();

        // FK obligatorias
        e.setTipoEleccion(
                tipoEleccionRepo.findById(dto.getTipoEleccionId())
                        .orElseThrow(() -> new RuntimeException("Tipo de elección no existe")));

        e.setTipo(
                tipoRepo.findById(dto.getTipoId())
                        .orElseThrow(() -> new RuntimeException("Tipo no existe")));

        e.setProceso(
                procesoRepo.findById(dto.getProcesoId())
                        .orElseThrow(() -> new RuntimeException("Proceso no existe")));

        // FK opcionales
        if (dto.getProgramaId() != null)
            e.setPrograma(
                    programaRepo.findById(dto.getProgramaId())
                            .orElseThrow(() -> new RuntimeException("Programa no existe")));

        if (dto.getSedeId() != null)
            e.setSede(
                    sedeRepo.findById(dto.getSedeId())
                            .orElseThrow(() -> new RuntimeException("Sede no existe")));

        if (dto.getFacultadId() != null)
            e.setFacultad(
                    facultadRepo.findById(dto.getFacultadId())
                            .orElseThrow(() -> new RuntimeException("Facultad no existe")));

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
    // ACTUALIZAR ELECCIÓN
    // ========================================================
    public EleccionResponseDTO actualizar(Long id, EleccionRequestDTO dto) {
        Eleccion e = buscarPorIdEntity(id);

        // Validar fechas
        validarFechas(dto.getFechaInicio(), dto.getFechaFinaliza());

        // FK obligatorias
        e.setTipoEleccion(
                tipoEleccionRepo.findById(dto.getTipoEleccionId())
                        .orElseThrow(() -> new RuntimeException("Tipo de elección no existe")));

        e.setTipo(
                tipoRepo.findById(dto.getTipoId())
                        .orElseThrow(() -> new RuntimeException("Tipo no existe")));

        e.setProceso(
                procesoRepo.findById(dto.getProcesoId())
                        .orElseThrow(() -> new RuntimeException("Proceso no existe")));

        // FK opcionales
        if (dto.getProgramaId() != null) {
            e.setPrograma(
                    programaRepo.findById(dto.getProgramaId())
                            .orElseThrow(() -> new RuntimeException("Programa no existe")));
        } else {
            e.setPrograma(null);
        }

        if (dto.getSedeId() != null) {
            e.setSede(
                    sedeRepo.findById(dto.getSedeId())
                            .orElseThrow(() -> new RuntimeException("Sede no existe")));
        } else {
            e.setSede(null);
        }

        if (dto.getFacultadId() != null) {
            e.setFacultad(
                    facultadRepo.findById(dto.getFacultadId())
                            .orElseThrow(() -> new RuntimeException("Facultad no existe")));
        } else {
            e.setFacultad(null);
        }

        // Campos simples
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setAnio(dto.getAnio());
        e.setSemestre(dto.getSemestre());
        e.setFechaInicio(dto.getFechaInicio());
        e.setFechaFinaliza(dto.getFechaFinaliza());

        Eleccion updated = repo.save(e);
        return buildDTO(updated);
    }

    // ========================================================
    // ELIMINAR/DESACTIVAR ELECCIÓN
    // ========================================================
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Elección no encontrada");
        }
        repo.deleteById(id);
        repo.flush(); // opcional, pero útil para forzar ejecución inmediata
    }

    // ========================================================
    // CAMBIAR ESTADO DE LA ELECCIÓN
    // ========================================================
    public EleccionResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        Eleccion e = buscarPorIdEntity(id);

        // Validar valores permitidos
        if (!nuevoEstado.equalsIgnoreCase("ACTIVA")
                && !nuevoEstado.equalsIgnoreCase("ABIERTO")
                && !nuevoEstado.equalsIgnoreCase("CERRADO")) {
            throw new RuntimeException("Estado inválido. Debe ser ACTIVA, ABIERTO o CERRADO");
        }

        e.setEstado(nuevoEstado.toUpperCase());

        Eleccion saved = repo.save(e);
        return buildDTO(saved);
    }

    // ========================================================
    // FILTRAR POR ESTADO
    // ========================================================
    public List<EleccionResponseDTO> filtrarPorEstado(String estado) {
        return repo.findByEstado(estado.toUpperCase())
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR PROCESO
    // ========================================================
    public List<EleccionResponseDTO> filtrarPorProceso(Long procesoId) {
        procesoRepo.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        return repo.findByProcesoId(procesoId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR TIPO DE ELECCIÓN
    // ========================================================
    public List<EleccionResponseDTO> filtrarPorTipoEleccion(Long tipoEleccionId) {
        tipoEleccionRepo.findById(tipoEleccionId)
                .orElseThrow(() -> new RuntimeException("Tipo de elección no encontrado"));

        return repo.findByTipoEleccionId(tipoEleccionId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // VALIDAR FECHAS (inicio < fin)
    // ========================================================
    public void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFinaliza) {
        if (fechaInicio == null || fechaFinaliza == null) {
            throw new RuntimeException("Las fechas de inicio y fin son obligatorias");
        }

        if (!fechaInicio.isBefore(fechaFinaliza)) {
            throw new RuntimeException("La fecha de inicio debe ser anterior a la fecha de finalización");
        }
    }

    // ========================================================
    // EXTENDER FECHA DE ELECCIÓN
    // ========================================================
    public EleccionResponseDTO extenderFecha(Long id, LocalDateTime nuevaFechaFinaliza) {
        Eleccion e = buscarPorIdEntity(id);

        // Validar que la nueva fecha sea posterior a la fecha actual de finalización
        if (!nuevaFechaFinaliza.isAfter(e.getFechaFinaliza())) {
            throw new RuntimeException("La nueva fecha de finalización debe ser posterior a la fecha actual");
        }

        // Validar que la nueva fecha sea posterior a la fecha de inicio
        if (!nuevaFechaFinaliza.isAfter(e.getFechaInicio())) {
            throw new RuntimeException("La nueva fecha de finalización debe ser posterior a la fecha de inicio");
        }

        e.setFechaFinaliza(nuevaFechaFinaliza);
        e.setExtendido(true);

        Eleccion updated = repo.save(e);
        return buildDTO(updated);
    }

    // ========================================================
    // VALIDAR QUE LA ELECCIÓN ESTÉ ABIERTA PARA VOTAR
    // ========================================================
    public void validarEleccionAbierta(Long id) {
        Eleccion e = buscarPorIdEntity(id);

        LocalDateTime ahora = LocalDateTime.now();

        // Validar estado
        if (!"ABIERTO".equalsIgnoreCase(e.getEstado())) {
            throw new RuntimeException("La elección no está abierta para votar. Estado actual: " + e.getEstado());
        }

        // Validar fechas
        if (ahora.isBefore(e.getFechaInicio())) {
            throw new RuntimeException("La elección aún no ha comenzado. Fecha de inicio: " + e.getFechaInicio());
        }

        if (ahora.isAfter(e.getFechaFinaliza())) {
            throw new RuntimeException("La elección ya ha finalizado. Fecha de finalización: " + e.getFechaFinaliza());
        }
    }

    // ========================================================
    // LISTAR ELECCIONES ACTIVAS/ABIERTAS
    // ========================================================
    public List<EleccionResponseDTO> listarActivas() {
        return repo.findByEstado("ACTIVA")
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    public List<EleccionResponseDTO> listarAbiertas() {
        return repo.findByEstado("ABIERTO")
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // LISTAR ELECCIONES ABIERTAS PARA UN VOTANTE ESPECÍFICO
    // Filtra por: censo, facultad y programa
    // ========================================================
    public List<EleccionResponseDTO> listarAbiertasParaVotante(String documento) {
        // Buscar el votante
        Votante votante = votanteRepo.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado con documento: " + documento));

        System.out.println("[DEBUG] Votante encontrado: ID=" + votante.getId() + ", Documento=" + documento);
        System.out.println("[DEBUG] Facultad del votante: "
                + (votante.getFacultad() != null ? votante.getFacultad().getId() : "NULL"));
        System.out.println("[DEBUG] Programa del votante: "
                + (votante.getPrograma() != null ? votante.getPrograma().getId() : "NULL"));

        // Obtener todas las elecciones abiertas
        List<Eleccion> eleccionesAbiertas = repo.findByEstado("ABIERTO");
        System.out.println("[DEBUG] Total elecciones ABIERTAS: " + eleccionesAbiertas.size());

        // Filtrar según elegibilidad del votante
        List<EleccionResponseDTO> resultado = eleccionesAbiertas.stream()
                .filter(eleccion -> {
                    System.out.println(
                            "[DEBUG] Evaluando elección ID=" + eleccion.getId() + " (" + eleccion.getNombre() + ")");

                    // 1. Verificar que el votante esté en el censo de esta elección
                    // Usar votante.getId() en lugar de documento para evitar problemas con JPA
                    boolean enCenso = censoRepo.findByVotanteIdAndEleccionId(votante.getId(), eleccion.getId())
                            .isPresent();
                    System.out.println("[DEBUG]   - En censo: " + enCenso);
                    if (!enCenso) {
                        return false;
                    }

                    // 2. Verificar restricción de facultad (si la ELECCIÓN tiene restricción)
                    if (eleccion.getFacultad() != null) {
                        boolean facultadOk = votante.getFacultad() != null &&
                                votante.getFacultad().getId().equals(eleccion.getFacultad().getId());
                        System.out.println("[DEBUG]   - Elección requiere facultad " + eleccion.getFacultad().getId()
                                + ", votante tiene: "
                                + (votante.getFacultad() != null ? votante.getFacultad().getId() : "NULL") + " -> "
                                + facultadOk);
                        if (!facultadOk) {
                            return false;
                        }
                    } else {
                        System.out.println("[DEBUG]   - Elección NO tiene restricción de facultad");
                    }

                    System.out.println("[DEBUG]   => INCLUIDA");
                    return true;
                })
                .map(this::buildDTO)
                .collect(Collectors.toList());

        System.out.println("[DEBUG] Elecciones filtradas para votante: " + resultado.size());
        return resultado;
    }

    // ========================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ========================================================

    private Eleccion buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));
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
                .procesoId(e.getProceso().getId())
                .programaId(e.getPrograma() != null ? e.getPrograma().getId() : null)
                .sedeId(e.getSede() != null ? e.getSede().getId() : null)
                .facultadId(e.getFacultad() != null ? e.getFacultad().getId() : null)
                .build();
    }
}