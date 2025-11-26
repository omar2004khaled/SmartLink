package com.example.auth.unit;

import com.example.auth.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceUnitTest {

    private JwtService jwtService;
    private final String testSecret = "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm";
    private final long testExpiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", testExpiration);
    }

    @Test
    void generateToken_ValidEmailAndRole_ReturnsToken() {
        // Arrange
        String email = "test@example.com";
        String role = "USER";

        // Act
        String token = jwtService.generateToken(email, role);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void extractEmail_ValidToken_ReturnsEmail() {
        // Arrange
        String email = "user@test.com";
        String token = jwtService.generateToken(email, "USER");

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        String email = "valid@example.com";
        String token = jwtService.generateToken(email, "USER");

        // Act
        boolean isValid = jwtService.isTokenValid(token, email);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidEmail_ReturnsFalse() {
        // Arrange
        String email = "user@example.com";
        String differentEmail = "different@example.com";
        String token = jwtService.generateToken(email, "USER");

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractRole_ValidToken_ReturnsRole() {
        // Arrange
        String email = "test@example.com";
        String role = "ADMIN";
        String token = jwtService.generateToken(email, role);

        // Act
        String extractedRole = jwtService.extractRole(token);

        // Assert
        assertEquals(role, extractedRole);
    }
}