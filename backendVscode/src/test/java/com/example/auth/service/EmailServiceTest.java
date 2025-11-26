package com.example.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendVerificationEmail_WhenEmailFails_ShouldFallbackToConsole() {
        // Arrange
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        String email = "test@example.com";
        String token = "test-token-123";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("Failed to send email"));
        assertTrue(consoleOutput.contains("VERIFICATION EMAIL"));
        assertTrue(consoleOutput.contains(email));
        assertTrue(consoleOutput.contains(token));

        // Reset System.out
        System.setOut(System.out);
    }
}