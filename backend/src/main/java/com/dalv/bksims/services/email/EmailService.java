package com.dalv.bksims.services.email;

import com.dalv.bksims.exceptions.EmailException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            message.setContent(htmlContent, "text/html");

            // Send the email
            emailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("Email service failed to send email");
        }
    }
}
