package com.example.auth.unit;

import com.example.auth.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceUnitTest {

    @Mock
    private JavaMailSender mailSender;
    
    @InjectMocks
    private EmailService emailService;

    @Test
    void sendVerificationEmail_ValidInput_SendsEmail() {
        // Arrange
        String email = "test@example.com";
        String token = "test-token-123";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_MailSenderThrowsException_HandlesGracefully() {
        // Arrange
        String email = "test@example.com";
        String token = "test-token-123";
        doThrow(new MailException("SMTP Error") {}).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_ValidInput_CreatesCorrectMessage() {
        // Arrange
        String email = "user@test.com";
        String token = "verification-token";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        verify(mailSender).send(argThat((SimpleMailMessage message) -> {
            return message.getTo()[0].equals(email) &&
                   message.getSubject().equals("SmartLink - Email Verification") &&
                   message.getText().contains(token) &&
                   message.getText().contains("http://localhost:8080/auth/verify?token=");
        }));
    }
}