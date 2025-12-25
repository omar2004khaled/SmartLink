package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderConfigTest {

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Arrange
        PasswordEncoderConfig config = new PasswordEncoderConfig();

        // Act
        PasswordEncoder encoder = config.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);

        // Test that it actually encodes
        String rawPassword = "testPassword123!";
        String encoded = encoder.encode(rawPassword);
        assertNotNull(encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
    }

    @Test
    void passwordEncoder_ShouldNotMatchWrongPassword() {
        // Arrange
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        // Act
        String rawPassword = "testPassword123!";
        String wrongPassword = "wrongPassword456!";
        String encoded = encoder.encode(rawPassword);

        // Assert
        assertFalse(encoder.matches(wrongPassword, encoded));
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentHashes() {
        // Arrange
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        String rawPassword = "testPassword123!";

        // Act
        String encoded1 = encoder.encode(rawPassword);
        String encoded2 = encoder.encode(rawPassword);

        // Assert - BCrypt should generate different hashes each time
        assertNotEquals(encoded1, encoded2);

        // But both should match the original password
        assertTrue(encoder.matches(rawPassword, encoded1));
        assertTrue(encoder.matches(rawPassword, encoded2));
    }

    @Test
    void passwordEncoder_ShouldHandleNull() {
        // Arrange
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> encoder.encode(null));
        // BCrypt does not necessarily throw IllegalArgumentException for null match arguments
    }
}