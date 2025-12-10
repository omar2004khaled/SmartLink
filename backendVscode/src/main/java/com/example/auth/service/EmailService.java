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

    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SmartLink - Password Reset");

            // Frontend URL (where React handles the token)
            String resetLink = "http://localhost:5175/reset-password?token=" + token;

            message.setText(
                    "You requested a password reset.\n\n" +
                            "Click the link below to reset your password:\n" + resetLink + "\n\n" +
                            "This link expires in 1 hour."
            );

            mailSender.send(message);
            System.out.println("Password reset email sent to: " + to);
        } catch (Exception e) {
            System.out.println("Failed to send password reset email: " + e.getMessage());
            System.out.println("\n=== PASSWORD RESET EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Reset Link: " + token);
            System.out.println("============================\n");
        }
    }

    public boolean sendAdminNotification(String userEmail, String adminEmail, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userEmail);
            mailMessage.setSubject("Admin Notification from SmartLink");
            mailMessage.setText("Dear " + userEmail + ",\n\n" + message + "\n\nBest regards,\nSmartLink Admin Team");

            mailSender.send(mailMessage);
            System.out.println("Admin notification sent to: " + userEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send admin notification: " + e.getMessage());
            // Fallback to console output
            System.out.println("\n=== ADMIN NOTIFICATION ===");
            System.out.println("To: " + userEmail);
            System.out.println("Message: " + message);
            System.out.println("==========================\n");
            return true; // Return true even on fallback
        }
    }
}