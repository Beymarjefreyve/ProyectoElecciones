package com.universidad.elecciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CensoRequest {
    private Long votanteId;
    private Long eleccionId;
}
