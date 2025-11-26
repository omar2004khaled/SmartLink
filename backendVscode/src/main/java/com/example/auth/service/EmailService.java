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
}

/*
package com.example.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5175}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        try {
            System.out.println("Attempting to send email to: " + to);

            // Use frontend URL, not backend URL
            String verificationUrl = frontendUrl + "/email-verified?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("SmartLink - Verify Your Email");

            // HTML email with proper styling
            String htmlContent = buildVerificationEmail(verificationUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);

        } catch (Exception e) {
            System.out.println("Failed to send email to " + to + ": " + e.getMessage());
            // Fallback to console output
            System.out.println("\n=== VERIFICATION EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Verification Link: " + frontendUrl + "/email-verified?token=" + token);
            System.out.println("==========================\n");
        }
    }

    private String buildVerificationEmail(String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #007bff; color: white; 
                             text-decoration: none; border-radius: 5px; font-size: 16px; }
                    .footer { margin-top: 30px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Verify Your Email Address</h2>
                    </div>
                    <p>Hello,</p>
                    <p>Thank you for registering with Smart Link! Please verify your email address by clicking the button below:</p>
                    <p style="text-align: center;">
                        <a href="%s" class="button">Verify Email Address</a>
                    </p>
                    <p>If the button doesn't work, copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #007bff;">%s</p>
                    <p>This link will expire in 24 hours.</p>
                    <div class="footer">
                        <p>If you didn't create an account, please ignore this email.</p>
                        <p>&copy; 2024 Smart Link. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl);
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            // Use frontend URL for password reset as well
            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("SmartLink - Password Reset Request");

            String htmlContent = buildPasswordResetEmail(resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Password reset email sent to: " + to);
        } catch (Exception e) {
            System.out.println("Failed to send password reset email: " + e.getMessage());
            System.out.println("\n=== PASSWORD RESET EMAIL ===");
            System.out.println("To: " + to);
            System.out.println("Reset Link: " + frontendUrl + "/reset-password?token=" + token);
            System.out.println("============================\n");
        }
    }

    private String buildPasswordResetEmail(String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #dc3545; color: white; 
                             text-decoration: none; border-radius: 5px; font-size: 16px; }
                    .footer { margin-top: 30px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Reset Your Password</h2>
                    </div>
                    <p>Hello,</p>
                    <p>You requested to reset your password. Click the button below to create a new password:</p>
                    <p style="text-align: center;">
                        <a href="%s" class="button">Reset Password</a>
                    </p>
                    <p>If the button doesn't work, copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #007bff;">%s</p>
                    <p>This link will expire in 1 hour.</p>
                    <div class="footer">
                        <p>If you didn't request this, please ignore this email.</p>
                        <p>&copy; 2024 Smart Link. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetUrl, resetUrl);
    }
}
*/