package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByEstado(String estado);
    List<Solicitud> findByTipoSolicitudId(Long tipoSolicitudId);
    List<Solicitud> findByProgramaId(Long programaId);
    List<Solicitud> findBySedeId(Long sedeId);
    List<Solicitud> findByEstadoAndTipoSolicitudId(String estado, Long tipoSolicitudId);
    List<Solicitud> findByProgramaIdAndSedeId(Long programaId, Long sedeId);
    List<Solicitud> findByEstadoAndProgramaId(String estado, Long programaId);
    List<Solicitud> findByEstadoAndSedeId(String estado, Long sedeId);
}
