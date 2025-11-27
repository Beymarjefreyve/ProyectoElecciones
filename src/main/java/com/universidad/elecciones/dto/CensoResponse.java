package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CensoResponse {
    private Long id;
    private Long votanteId;
    private Long eleccionId;
}
