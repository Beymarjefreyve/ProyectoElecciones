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