package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EleccionResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;

    private Integer anio;
    private Integer semestre;
    private String estado;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinaliza;

    private Long tipoEleccionId;
    private Long tipoId;
    private Long programaId;
    private Long sedeId;
    private Long facultadId;
}
