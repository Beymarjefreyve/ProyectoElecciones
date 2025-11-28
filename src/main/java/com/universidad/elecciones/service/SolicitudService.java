package com.universidad.elecciones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.SolicitudRequestDTO;
import com.universidad.elecciones.dto.SolicitudResponseDTO;
import com.universidad.elecciones.entity.Solicitud;
import com.universidad.elecciones.repository.ProgramaRepository;
import com.universidad.elecciones.repository.SedeRepository;
import com.universidad.elecciones.repository.SolicitudRepository;
import com.universidad.elecciones.repository.TipoSolicitudRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudService {

	@Autowired
    private final SolicitudRepository repo;
	@Autowired
    private final ProgramaRepository programaRepo;
	@Autowired
    private final SedeRepository sedeRepo;
	@Autowired
    private final TipoSolicitudRepository tipoRepo;

    // ========================================================
    // CREAR SOLICITUD
    // ========================================================
    public SolicitudResponseDTO crear(SolicitudRequestDTO dto) {

        Solicitud s = new Solicitud();

        // Asignar entidades reales desde la BD
        s.setPrograma(
                programaRepo.findById(dto.getProgramaId())
                        .orElseThrow(() -> new RuntimeException("Programa no existe"))
        );

        s.setSede(
                sedeRepo.findById(dto.getSedeId())
                        .orElseThrow(() -> new RuntimeException("Sede no existe"))
        );

        s.setTipoSolicitud(
                tipoRepo.findById(dto.getTipoSolicitudId())
                        .orElseThrow(() -> new RuntimeException("Tipo de solicitud no existe"))
        );

        // Asignar datos del DTO
        s.setDocumento(dto.getDocumento());
        s.setNombre(dto.getNombre());
        s.setAnio(dto.getAnio());
        s.setSemestre(dto.getSemestre());
        s.setEmail(dto.getEmail());

        // Datos del sistema
        s.setEstado("PENDIENTE");
        s.setFechaCreacion(LocalDateTime.now());

        Solicitud saved = repo.save(s);

        return buildDTO(saved);
    }

    // ========================================================
    // LISTAR SOLICITUDES
    // ========================================================
    public List<SolicitudResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // BUSCAR POR ID
    // ========================================================
    public SolicitudResponseDTO buscarPorId(Long id) {
        Solicitud solicitud = buscarPorIdEntity(id);
        return buildDTO(solicitud);
    }

    // ========================================================
    // ACTUALIZAR ESTADO DE SOLICITUD
    // ========================================================
    public SolicitudResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Solicitud solicitud = buscarPorIdEntity(id);
        
        validarEstado(nuevoEstado);
        solicitud.setEstado(nuevoEstado.toUpperCase());
        
        // Asignar fecha de respuesta automáticamente si el estado cambia a APROBADO o RECHAZADO
        if ("APROBADO".equalsIgnoreCase(nuevoEstado) || "RECHAZADO".equalsIgnoreCase(nuevoEstado)) {
            if (solicitud.getFechaRespuesta() == null) {
                solicitud.setFechaRespuesta(LocalDateTime.now());
            }
        }
        
        Solicitud updated = repo.save(solicitud);
        return buildDTO(updated);
    }

    // ========================================================
    // APROBAR SOLICITUD
    // ========================================================
    public SolicitudResponseDTO aprobar(Long id) {
        return actualizarEstado(id, "APROBADO");
    }

    // ========================================================
    // RECHAZAR SOLICITUD
    // ========================================================
    public SolicitudResponseDTO rechazar(Long id) {
        return actualizarEstado(id, "RECHAZADO");
    }

    // ========================================================
    // FILTRAR POR ESTADO
    // ========================================================
    public List<SolicitudResponseDTO> filtrarPorEstado(String estado) {
        return repo.findByEstado(estado.toUpperCase())
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR TIPO DE SOLICITUD
    // ========================================================
    public List<SolicitudResponseDTO> filtrarPorTipoSolicitud(Long tipoSolicitudId) {
        tipoRepo.findById(tipoSolicitudId)
                .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));
        
        return repo.findByTipoSolicitudId(tipoSolicitudId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR PROGRAMA
    // ========================================================
    public List<SolicitudResponseDTO> filtrarPorPrograma(Long programaId) {
        programaRepo.findById(programaId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        
        return repo.findByProgramaId(programaId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR SEDE
    // ========================================================
    public List<SolicitudResponseDTO> filtrarPorSede(Long sedeId) {
        sedeRepo.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        
        return repo.findBySedeId(sedeId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // FILTRAR POR PROGRAMA Y SEDE
    // ========================================================
    public List<SolicitudResponseDTO> filtrarPorProgramaYSede(Long programaId, Long sedeId) {
        programaRepo.findById(programaId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        sedeRepo.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        
        return repo.findByProgramaIdAndSedeId(programaId, sedeId)
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ========================================================
    
    private Solicitud buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new RuntimeException("El estado no puede estar vacío");
        }
        
        String estadoUpper = estado.toUpperCase();
        if (!estadoUpper.equals("PENDIENTE") 
                && !estadoUpper.equals("APROBADO") 
                && !estadoUpper.equals("RECHAZADO")
                && !estadoUpper.equals("EN_REVISION")
                && !estadoUpper.equals("CANCELADO")) {
            throw new RuntimeException("Estado inválido. Debe ser: PENDIENTE, APROBADO, RECHAZADO, EN_REVISION o CANCELADO");
        }
    }

    // ========================================================
    // CONVERTIR A DTO
    // ========================================================
    private SolicitudResponseDTO buildDTO(Solicitud s) {
        return SolicitudResponseDTO.builder()
                .id(s.getId())
                .programaId(s.getPrograma().getId())
                .sedeId(s.getSede().getId())
                .tipoSolicitudId(s.getTipoSolicitud().getId())
                .documento(s.getDocumento())
                .nombre(s.getNombre())
                .anio(s.getAnio())
                .semestre(s.getSemestre())
                .email(s.getEmail())
                .estado(s.getEstado())
                .fechaCreacion(s.getFechaCreacion())
                .fechaRespuesta(s.getFechaRespuesta())
                .build();
    }
}