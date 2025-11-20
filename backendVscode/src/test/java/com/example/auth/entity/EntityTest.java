package com.example.auth.entity;

import com.example.auth.enums.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void user_DefaultValues_ShouldBeSet() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertFalse(user.isEnabled());
        assertEquals("USER", user.getRole());
    }

    @Test
    void user_SettersAndGetters_ShouldWork() {
        // Arrange
        User user = new User();
        LocalDate birthDate = LocalDate.of(1995, 5, 20);

        // Act
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setBirthDate(birthDate);
        user.setPhoneNumber("+201234567890");
        user.setGender(Gender.MALE);
        user.setEnabled(true);
        user.setRole("ADMIN");

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals("+201234567890", user.getPhoneNumber());
        assertEquals(Gender.MALE, user.getGender());
        assertTrue(user.isEnabled());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void verificationToken_Constructor_ShouldGenerateToken() {
        // Arrange
        User user = new User();
        user.setId(1L);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // Act
        VerificationToken token = new VerificationToken(user, expiresAt);

        // Assert
        assertNotNull(token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(expiresAt, token.getExpiresAt());
    }

    @Test
    void verificationToken_SettersAndGetters_ShouldWork() {
        // Arrange
        VerificationToken token = new VerificationToken();
        User user = new User();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // Act
        token.setId(1L);
        token.setToken("test-token-123");
        token.setUser(user);
        token.setExpiresAt(expiresAt);

        // Assert
        assertEquals(1L, token.getId());
        assertEquals("test-token-123", token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(expiresAt, token.getExpiresAt());
    }
}