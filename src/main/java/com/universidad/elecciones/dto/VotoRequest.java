package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoRequest {

    private Long candidatoId;
    private String documento;   // Documento del votante
}