package com.universidad.elecciones.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionRequest {
    private Long candidatoId;
    private Integer numero;  // número en el tarjetón
}
