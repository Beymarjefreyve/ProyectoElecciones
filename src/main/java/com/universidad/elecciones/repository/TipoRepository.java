package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoRepository extends JpaRepository<Tipo, Long> {
}
