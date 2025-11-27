package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEleccionResponseDTO {
    private Long id;
    private String nombre;
}