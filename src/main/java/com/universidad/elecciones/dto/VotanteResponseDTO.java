package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotanteResponseDTO {

    private Long id;
    private String documento;
    private String nombre;
}