package com.example.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.backend.base-url}")
    private String backendBaseUrl;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verificationLink = backendBaseUrl + "/auth/verify?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SmartLink - Email Verification");
            message.setText(
                    "Click the link below to verify your email:\n" +
                            verificationLink);

            mailSender.send(message);
        } catch (Exception e) {
            // Console fallback
            System.out.println("\n=== VERIFICATION EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Verification Link: " + verificationLink);
            System.out.println("==========================\n");
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SmartLink - Password Reset");
            message.setText(
                    "You requested a password reset.\n\n" +
                            "Click the link below to reset your password:\n" +
                            resetLink + "\n\n" +
                            "This link expires in 1 hour.");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("\n=== PASSWORD RESET EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Reset Link: " + resetLink);
            System.out.println("============================\n");
        }
    }

    public boolean sendAdminNotification(String userEmail, String adminEmail, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userEmail);
            mailMessage.setSubject("Admin Notification from SmartLink");
            mailMessage.setText(
                    "Dear " + userEmail + ",\n\n" +
                            message + "\n\n" +
                            "Best regards,\nSmartLink Admin Team");

            mailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            System.out.println("\n=== ADMIN NOTIFICATION ===");
            System.out.println("To: " + userEmail);
            System.out.println("Message: " + message);
            System.out.println("==========================\n");
            return true;
        }
    }
}
