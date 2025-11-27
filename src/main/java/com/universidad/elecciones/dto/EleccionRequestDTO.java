package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EleccionRequestDTO {

    private Long tipoEleccionId;
    private Long tipoId;
    private Long programaId;
    private Long sedeId;
    private Long procesoId;
    private Long facultadId;

    private String nombre;
    private String descripcion;

    private Integer anio;
    private Integer semestre;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinaliza;
}
