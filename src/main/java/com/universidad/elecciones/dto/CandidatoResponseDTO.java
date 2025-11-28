package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatoResponseDTO {
    private Long id;
    private String documento;
    private String nombre;
    private String imagen;
}

