package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionUpdateRequest {
    private Integer numero;
    private String estado;
}

