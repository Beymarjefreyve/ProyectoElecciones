package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoResponse {

    private Long id;
    private Long candidatoId;
    private String candidatoNombre;
    private Long eleccionId;
    private String documento;
    private LocalDateTime fecha;
}