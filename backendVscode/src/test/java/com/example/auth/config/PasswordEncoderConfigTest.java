package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

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
}