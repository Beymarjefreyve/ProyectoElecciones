package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Eleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EleccionRepository extends JpaRepository<Eleccion, Long> {

}
