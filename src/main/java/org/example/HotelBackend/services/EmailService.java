package org.example.HotelBackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendMail(String to, String subject, String text) {
        try {
            System.out.println("Отправка email на: " + to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("artur.dzhenbaz@gmail.com");

            mailSender.send(message);

            System.out.println("Email успешно отправлен на: " + to);
        } catch (MailException e) {
            System.err.println("Ошибка при отправке email на " + to + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при отправке email: " + e.getMessage());
        }
    }
}
