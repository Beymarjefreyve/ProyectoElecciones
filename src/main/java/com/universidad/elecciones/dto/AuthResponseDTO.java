package com.universidad.elecciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private Long id;
    private String documento;
    private String nombre;
    private String email;
    private String rol;
    private Long facultadId;
    private String facultadNombre;
    private String mensaje;
    private boolean success;
}
