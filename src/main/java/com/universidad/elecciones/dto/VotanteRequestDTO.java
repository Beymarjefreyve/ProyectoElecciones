package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotanteRequestDTO {

    private String documento;
    private String nombre;
}