package com.universidad.elecciones.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String documento;
    private String password;
    private String email;
    private String nombre;
    private Long facultadId;
}
