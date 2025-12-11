package com.universidad.elecciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
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
            log.warn("URL de verificación: {}/verificar?token={}", baseUrl, token);
            return;
        }

        try {
            String urlVerificacion = baseUrl + "/verificacion-exitosa.html?token=" + token;

            String html = generarHtmlVerificacion(nombre, urlVerificacion);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Verifica tu correo - Sistema de Elecciones Universitarias");
            helper.setText(html, true); // ← TRUE = HTML

            mailSender.send(mimeMessage);

            log.info("Correo de verificación enviado a: {}", email);

        } catch (Exception e) {
            log.error("Error al enviar correo de verificación a {}: {}", email, e.getMessage());
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

    private String generarHtmlVerificacion(String nombre, String url) {
        return """
                <div style="font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px;">
                    <div style="max-width: 520px; margin: auto; background: #fff; padding: 30px;
                                border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">

                        <h2 style="text-align:center; color:#2c3e50;">Verificación de Cuenta</h2>

                        <p style="font-size: 15px; color:#444;">
                            Hola <strong>%s</strong>,
                        </p>

                        <p style="font-size: 15px; color:#444;">
                            Gracias por registrarte en el <strong>Sistema de Elecciones Universitarias</strong>.
                            Para activar tu cuenta, haz clic en el siguiente botón:
                        </p>

                        <div style="text-align:center; margin: 30px 0;">
                            <a href="%s"
                               style="background: #0275d8; color:white; padding: 12px 25px;
                                      text-decoration:none; border-radius: 6px;
                                      font-size:16px; display:inline-block;">
                                Verificar Correo
                            </a>
                        </div>

                        <p style="font-size: 14px; color:#777;">
                            Si no solicitaste esta cuenta, puedes ignorar este mensaje.
                        </p>

                        <hr style="border:none; border-top:1px solid #eee; margin: 20px 0;">

                        <p style="font-size: 12px; color:#aaa; text-align:center;">
                            © 2025 Sistema de Elecciones Universitarias<br>
                            Universidad — Todos los derechos reservados
                        </p>

                    </div>
                </div>
                """.formatted(nombre, url);
    }

}
