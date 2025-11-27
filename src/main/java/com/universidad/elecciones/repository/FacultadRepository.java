package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultadRepository extends JpaRepository<Facultad, Long> {
}
