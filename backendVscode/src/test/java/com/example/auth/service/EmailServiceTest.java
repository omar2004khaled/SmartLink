package com.example.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Test
    void sendVerificationEmail_ShouldPrintToConsole() {
        // Arrange
        EmailService emailService = new EmailService();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        String email = "test@example.com";
        String token = "test-token-123";

        // Act
        emailService.sendVerificationEmail(email, token);

        // Assert
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("VERIFICATION EMAIL"));
        assertTrue(consoleOutput.contains(email));
        assertTrue(consoleOutput.contains(token));

        // Reset System.out
        System.setOut(System.out);
    }
}