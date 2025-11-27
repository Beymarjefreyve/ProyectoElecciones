package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramaDTO {
    private Long id;
    private String nombre;
    private Long facultadId;
}
