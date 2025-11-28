package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Eleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EleccionRepository extends JpaRepository<Eleccion, Long> {
    List<Eleccion> findByEstado(String estado);
    List<Eleccion> findByProcesoId(Long procesoId);
    List<Eleccion> findByTipoEleccionId(Long tipoEleccionId);
    List<Eleccion> findByEstadoAndProcesoId(String estado, Long procesoId);
    List<Eleccion> findByEstadoAndTipoEleccionId(String estado, Long tipoEleccionId);
    List<Eleccion> findByProgramaId(Long programaId);
    List<Eleccion> findBySedeId(Long sedeId);
    List<Eleccion> findByFacultadId(Long facultadId);
    List<Eleccion> findByTipoId(Long tipoId);
}
