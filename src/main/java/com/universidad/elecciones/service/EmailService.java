package com.universidad.elecciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * Envía un correo de verificación al usuario
     */
    public void enviarCorreoVerificacion(String email, String nombre, String token) {
        if (mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            log.warn("Email no configurado. Token de verificación para {}: {}", email, token);
            log.warn("URL de verificación: {}/verificacion-exitosa.html?token={}", baseUrl, token);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Verifica tu correo - Sistema de Elecciones Universitarias");

            String urlVerificacion = baseUrl + "/auth/verificar?token=" + token;

            String body = String.format(
                    "Hola %s,\n\n" +
                            "Gracias por registrarte en el Sistema de Elecciones Universitarias.\n\n" +
                            "Para verificar tu correo electrónico y activar tu cuenta, haz clic en el siguiente enlace:\n\n"
                            +
                            "%s\n\n" +
                            "Si no creaste esta cuenta, puedes ignorar este mensaje.\n\n" +
                            "Saludos,\n" +
                            "Sistema de Elecciones Universitarias",
                    nombre, urlVerificacion);

            message.setText(body);
            mailSender.send(message);
            log.info("Correo de verificación enviado a: {}", email);

        } catch (Exception e) {
            log.error("Error al enviar correo de verificación a {}: {}", email, e.getMessage());
            // No lanzamos excepción para no bloquear el registro
            log.warn("Token de verificación para {}: {}", email, token);
        }
    }

    /**
     * Envía un correo de confirmación de voto
     */
    public void enviarConfirmacionVoto(String email, String nombre, String eleccionNombre) {
        if (mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            log.warn("Email no configurado. No se envió confirmación de voto a: {}", email);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Confirmación de Voto - " + eleccionNombre);

            String body = String.format(
                    "Hola %s,\n\n" +
                            "Tu voto ha sido registrado exitosamente en la elección: %s\n\n" +
                            "Gracias por participar en el proceso electoral.\n\n" +
                            "Saludos,\n" +
                            "Sistema de Elecciones Universitarias",
                    nombre, eleccionNombre);

            message.setText(body);
            mailSender.send(message);
            log.info("Confirmación de voto enviada a: {}", email);

        } catch (Exception e) {
            log.error("Error al enviar confirmación de voto a {}: {}", email, e.getMessage());
        }
    }
}
