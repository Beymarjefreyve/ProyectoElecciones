package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.TipoEleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEleccionRepository extends JpaRepository<TipoEleccion, Long> {
}
