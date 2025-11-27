package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipacionDTO {
    private Long eleccionId;
    private Long inscritos;
    private Long votantes;
    private Double participacion; // porcentaje
}
