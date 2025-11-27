package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRequestDTO {

    private Long programaId;
    private Long sedeId;
    private Long tipoSolicitudId;

    private String documento;
    private String nombre;

    private Integer anio;
    private Integer semestre;

    private String email;
}
