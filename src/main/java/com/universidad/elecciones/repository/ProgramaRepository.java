package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
}
