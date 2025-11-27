package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponseDTO {

    private Long id;
    private Long programaId;
    private Long sedeId;
    private Long tipoSolicitudId;

    private String documento;
    private String nombre;

    private Integer anio;
    private Integer semestre;

    private String email;
    private String estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
}

