package com.universidad.elecciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Envía un correo de verificación al usuario
     */
    @Async
    public void enviarCorreoVerificacion(String email, String nombre, String token) {
        try {
            String urlVerificacion = baseUrl + "/verificacion-exitosa.html?token=" + token;

            String html = generarHtmlVerificacion(nombre, urlVerificacion);

            sendGridEmailService.enviarHtml(
                    email,
                    "Verifica tu correo - Sistema de Elecciones Universitarias",
                    html);

            log.info("Correo de verificación enviado a: {}", email);

        } catch (Exception e) {
            log.error("Error al enviar correo de verificación", e);
        }
    }

    /**
     * Envía un correo de confirmación de voto
     */
    public void enviarConfirmacionVoto(String email, String nombre, String eleccionNombre) {
        try {
            String html = String.format(
                    "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                            "<h2>Confirmación de Voto</h2>" +
                            "<p>Hola <strong>%s</strong>,</p>" +
                            "<p>Tu voto ha sido registrado exitosamente en la elección: <strong>%s</strong></p>" +
                            "<p>Gracias por participar en el proceso electoral.</p>" +
                            "<p>Saludos,<br>Sistema de Elecciones Universitarias</p>" +
                            "</div>",
                    nombre, eleccionNombre);

            sendGridEmailService.enviarHtml(
                    email,
                    "Confirmación de Voto - " + eleccionNombre,
                    html);

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
