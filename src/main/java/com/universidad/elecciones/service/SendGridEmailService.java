package com.universidad.elecciones.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendGridEmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Value("${MAIL_FROM}")
    private String mailFrom;

    /**
     * Envía un correo HTML usando la API de SendGrid
     * 
     * @param to      Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param html    Contenido HTML del correo
     */
    public void enviarHtml(String to, String subject, String html) {
        Email from = new Email(mailFrom);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", html);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            log.info("SendGrid Response - Status: {}, To: {}", response.getStatusCode(), to);

            if (response.getStatusCode() >= 400) {
                log.error("Error de SendGrid: {} - {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Error al enviar correo con SendGrid: " + response.getBody());
            }

        } catch (IOException e) {
            log.error("IOException al enviar correo con SendGrid a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error de IO al enviar correo con SendGrid", e);
        }
    }
}
