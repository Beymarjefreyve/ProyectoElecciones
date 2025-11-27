package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoSolicitudResponseDTO {
    private Long id;
    private String nombre;
}