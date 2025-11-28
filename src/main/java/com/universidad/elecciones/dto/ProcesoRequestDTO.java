package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesoRequestDTO {
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}

