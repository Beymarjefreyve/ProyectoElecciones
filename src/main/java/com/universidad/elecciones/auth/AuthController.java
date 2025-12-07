package com.universidad.elecciones.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.dto.AuthResponseDTO;
import com.universidad.elecciones.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    // ===============================================
    // POST /auth/register - Registro de estudiantes
    // ===============================================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@RequestBody RegisterRequest request) {
        AuthResponseDTO response = authService.registrarEstudiante(
                request.getDocumento(),
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                request.getFacultadId());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===============================================
    // GET /auth/verificar - Verificación de email
    // ===============================================
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarEmail(@RequestParam String token) {
        AuthResponseDTO response = authService.verificarEmail(token);

        Map<String, Object> result = new HashMap<>();
        result.put("success", response.isSuccess());
        result.put("mensaje", response.getMensaje());

        if (response.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ===============================================
    // POST /auth/login/estudiante - Login de estudiante
    // ===============================================
    @PostMapping("/login/estudiante")
    public ResponseEntity<AuthResponseDTO> loginEstudiante(@RequestBody LoginRequest request) {
        AuthResponseDTO response = authService.loginEstudiante(
                request.getEmail(),
                request.getPassword());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===============================================
    // POST /auth/login/admin - Login de administrador
    // ===============================================
    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponseDTO> loginAdmin(@RequestBody LoginRequest request) {
        AuthResponseDTO response = authService.loginAdministrador(
                request.getEmail(),
                request.getPassword());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===============================================
    // POST /auth/crear-admin - Crear administrador (setup inicial)
    // ===============================================
    @PostMapping("/crear-admin")
    public ResponseEntity<AuthResponseDTO> crearAdmin(@RequestBody RegisterRequest request) {
        AuthResponseDTO response = authService.crearAdministrador(
                request.getDocumento(),
                request.getNombre(),
                request.getEmail(),
                request.getPassword());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
