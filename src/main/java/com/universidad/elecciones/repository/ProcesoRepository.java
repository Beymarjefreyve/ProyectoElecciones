package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcesoRepository extends JpaRepository<Proceso, Long> {
}
