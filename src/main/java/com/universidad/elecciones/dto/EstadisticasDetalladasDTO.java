package com.universidad.elecciones.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasDetalladasDTO {
    private Long eleccionId;
    private String eleccionNombre;
    private Long totalVotos;
    
    // Estadísticas por facultad
    private List<ConteoPorFacultadDTO> porFacultad;
    
    // Estadísticas por programa
    private List<ConteoPorProgramaDTO> porPrograma;
    
    // Estadísticas por sede
    private List<ConteoPorSedeDTO> porSede;
}

