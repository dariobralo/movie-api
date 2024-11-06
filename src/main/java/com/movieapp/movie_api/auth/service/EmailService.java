package com.movieapp.movie_api.auth.service;

import com.movieapp.movie_api.auth.dto.response.MailBodyResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender javaMailSender,@Value("${spring.mail.username}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    /*
     *
     */
    public void sendMimeEmail(MailBodyResponse mailBodyResponse) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(mailBodyResponse.to());
            helper.setSubject(mailBodyResponse.subject());
            helper.setText(mailBodyResponse.text());
            helper.setFrom(fromEmail);

            javaMailSender.send(message);
        } catch (MessagingException e){
            throw new RuntimeException("Error en el envio de mail de validación. Reporte este fallo a soporte.");
        }
    }

    public void sendSimpleEmail(MailBodyResponse mailBodyResponse){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailBodyResponse.to());
            message.setFrom("fromEmail");
            message.setSubject(message.getSubject());
            message.setText(message.getText());

            javaMailSender.send(message);
    }

}
