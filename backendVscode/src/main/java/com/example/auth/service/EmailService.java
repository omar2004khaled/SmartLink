package com.example.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        try {
            System.out.println("Attempting to send email to: " + to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SmartLink - Email Verification");
            message.setText("Click the link to verify your email: http://localhost:8080/auth/verify?token=" + token);

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.out.println("Failed to send email to " + to + ": " + e.getMessage());
            // Fallback to console output
            System.out.println("\n=== VERIFICATION EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Verification Link: http://localhost:8080/auth/verify?token=" + token);
            System.out.println("==========================\n");
        }
    }
}