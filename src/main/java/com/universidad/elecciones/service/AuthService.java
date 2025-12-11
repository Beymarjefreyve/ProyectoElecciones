package com.universidad.elecciones.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.AuthResponseDTO;
import com.universidad.elecciones.entity.Facultad;
import com.universidad.elecciones.entity.Votante;
import com.universidad.elecciones.repository.FacultadRepository;
import com.universidad.elecciones.repository.VotanteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

        @Autowired
        private final VotanteRepository votanteRepo;

        @Autowired
        private final FacultadRepository facultadRepo;

        @Autowired
        private final EmailService emailService;

        // ===============================================
        // REGISTRO DE ESTUDIANTE
        // ===============================================
        public AuthResponseDTO registrarEstudiante(String documento, String nombre, String email,
                        String password, Long facultadId) {
                // Validaciones
                if (documento == null || documento.trim().isEmpty()) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("El documento es requerido")
                                        .build();
                }

                if (email == null || email.trim().isEmpty()) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("El email es requerido")
                                        .build();
                }

                if (password == null || password.length() < 4) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("La contraseña debe tener al menos 4 caracteres")
                                        .build();
                }

                // Verificar si ya existe
                if (votanteRepo.existsByDocumento(documento)) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Ya existe un usuario con este documento")
                                        .build();
                }

                if (votanteRepo.existsByEmail(email)) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Ya existe un usuario con este email")
                                        .build();
                }

                // Buscar facultad si se proporciona
                Facultad facultad = null;
                if (facultadId != null) {
                        facultad = facultadRepo.findById(facultadId).orElse(null);
                }

                // Generar token de verificación
                String token = UUID.randomUUID().toString();

                // Crear votante
                Votante votante = Votante.builder()
                                .documento(documento.trim())
                                .nombre(nombre.trim())
                                .email(email.trim().toLowerCase())
                                .password(password) // En producción, usar BCrypt para encriptar
                                .rol("ESTUDIANTE")
                                .emailVerificado(false)
                                .tokenVerificacion(token)
                                .facultad(facultad)
                                .estado("PENDING_VALIDATION")
                                .build();

                votanteRepo.save(votante);

                // Enviar correo de verificación
                emailService.enviarCorreoVerificacion(email, nombre, token);

                log.info("Estudiante registrado: {} - Token: {}", email, token);

                return AuthResponseDTO.builder()
                                .success(true)
                                .mensaje("Registro exitoso. Por favor verifica tu correo electrónico.")
                                .email(email)
                                .nombre(nombre)
                                .build();
        }

        // ===============================================
        // VERIFICAR EMAIL
        // ===============================================
        public AuthResponseDTO verificarEmail(String token) {
                if (token == null || token.trim().isEmpty()) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Token inválido")
                                        .build();
                }

                Votante votante = votanteRepo.findByTokenVerificacion(token).orElse(null);

                if (votante == null) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Token de verificación inválido o expirado")
                                        .build();
                }

                if (Boolean.TRUE.equals(votante.getEmailVerificado())) {
                        return AuthResponseDTO.builder()
                                        .success(true)
                                        .mensaje("El correo ya fue verificado anteriormente")
                                        .build();
                }

                votante.setEmailVerificado(true);
                votante.setTokenVerificacion(null); // Limpiar token
                votante.setEstado("ACTIVO");
                votanteRepo.save(votante);

                log.info("Email verificado para: {}", votante.getEmail());

                return AuthResponseDTO.builder()
                                .success(true)
                                .mensaje("¡Correo verificado exitosamente! Ya puedes iniciar sesión.")
                                .email(votante.getEmail())
                                .nombre(votante.getNombre())
                                .build();
        }

        // ===============================================
        // LOGIN ESTUDIANTE
        // ===============================================
        public AuthResponseDTO loginEstudiante(String email, String password) {
                if (email == null || password == null) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Email y contraseña son requeridos")
                                        .build();
                }

                Votante votante = votanteRepo.findByEmail(email.trim().toLowerCase()).orElse(null);

                if (votante == null) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Credenciales incorrectas")
                                        .build();
                }

                // Verificar contraseña (comparación simple, en producción usar BCrypt)
                if (!password.equals(votante.getPassword())) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Credenciales incorrectas")
                                        .build();
                }

                // Verificar que el email esté verificado
                if (votante.getEmailVerificado() == null || !votante.getEmailVerificado()) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje(
                                                        "Debes verificar tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada.")
                                        .build();
                }

                // Verificar rol
                if (!"ESTUDIANTE".equals(votante.getRol())) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Esta cuenta no es de tipo estudiante")
                                        .build();
                }

                log.info("Login exitoso para estudiante: {}", email);

                return AuthResponseDTO.builder()
                                .success(true)
                                .mensaje("Login exitoso")
                                .id(votante.getId())
                                .documento(votante.getDocumento())
                                .nombre(votante.getNombre())
                                .email(votante.getEmail())
                                .rol(votante.getRol())
                                .facultadId(votante.getFacultad() != null ? votante.getFacultad().getId() : null)
                                .facultadNombre(votante.getFacultad() != null ? votante.getFacultad().getNombre()
                                                : null)
                                .build();
        }

        // ===============================================
        // LOGIN ADMINISTRADOR
        // ===============================================
        public AuthResponseDTO loginAdministrador(String email, String password) {
                if (email == null || password == null) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Email y contraseña son requeridos")
                                        .build();
                }

                Votante votante = votanteRepo.findByEmail(email.trim().toLowerCase()).orElse(null);

                if (votante == null) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Credenciales incorrectas")
                                        .build();
                }

                // Verificar contraseña
                if (!password.equals(votante.getPassword())) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Credenciales incorrectas")
                                        .build();
                }

                // Verificar rol de administrador
                if (!"ADMINISTRATIVO".equals(votante.getRol())) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("No tienes permisos de administrador")
                                        .build();
                }

                log.info("Login exitoso para administrador: {}", email);

                return AuthResponseDTO.builder()
                                .success(true)
                                .mensaje("Login exitoso")
                                .id(votante.getId())
                                .documento(votante.getDocumento())
                                .nombre(votante.getNombre())
                                .email(votante.getEmail())
                                .rol(votante.getRol())
                                .build();
        }

        // ===============================================
        // CREAR ADMIN (utilidad para setup inicial)
        // ===============================================
        public AuthResponseDTO crearAdministrador(String documento, String nombre, String email, String password) {
                if (votanteRepo.existsByEmail(email)) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Ya existe un usuario con este email")
                                        .build();
                }

                if (votanteRepo.existsByDocumento(documento)) {
                        return AuthResponseDTO.builder()
                                        .success(false)
                                        .mensaje("Ya existe un usuario con este documento")
                                        .build();
                }

                Votante admin = Votante.builder()
                                .documento(documento)
                                .nombre(nombre)
                                .email(email.toLowerCase())
                                .password(password)
                                .rol("ADMINISTRATIVO")
                                .emailVerificado(true) // Admin no necesita verificar email
                                .estado("VERIFIED")
                                .build();

                votanteRepo.save(admin);

                log.info("Administrador creado: {}", email);

                return AuthResponseDTO.builder()
                                .success(true)
                                .mensaje("Administrador creado exitosamente")
                                .email(email)
                                .nombre(nombre)
                                .rol("ADMINISTRATIVO")
                                .build();
        }
}
