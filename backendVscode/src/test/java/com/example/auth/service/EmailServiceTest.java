package com.example.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendVerificationEmail_WhenEmailSucceeds_ShouldSendEmail() {
        // Arrange
        String email = "test@example.com";
        String token = "test-token-456";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_WithEmptyEmail_ShouldHandleGracefully() {
        // Arrange
        String email = "";
        String token = "test-token-789";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert - should not throw exception
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_WithNullToken_ShouldHandleGracefully() {
        // Arrange
        String email = "test@example.com";
        String token = null;

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendVerificationEmail(email, token);
        });

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_WithNullEmail_ShouldHandleGracefully() {
        // Arrange
        String email = null;
        String token = "test-token-abc";

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendVerificationEmail(email, token);
        });

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}