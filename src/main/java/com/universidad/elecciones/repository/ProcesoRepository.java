package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProcesoRepository extends JpaRepository<Proceso, Long> {
    List<Proceso> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(LocalDate fecha, LocalDate fecha2);
    List<Proceso> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Proceso> findByFechaFinBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
