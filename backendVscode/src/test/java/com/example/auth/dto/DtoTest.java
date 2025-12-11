package com.example.auth.dto;

import com.example.auth.enums.Gender;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_ValidData_ShouldPassValidation() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Pass123!");
        request.setConfirmPassword("Pass123!");
        request.setBirthDate(LocalDate.of(1995, 5, 20));
        request.setPhoneNumber("+201234567890");
        request.setGender(Gender.MALE);

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequest_InvalidEmail_ShouldFailValidation() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("invalid-email");
        request.setPassword("Pass123!");
        request.setConfirmPassword("Pass123!");
        request.setBirthDate(LocalDate.of(1995, 5, 20));
        request.setPhoneNumber("+201234567890");
        request.setGender(Gender.MALE);

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void registerRequest_ShortFullName_ShouldFailValidation() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jo"); // Too short
        request.setEmail("john@example.com");
        request.setPassword("Pass123!");
        request.setConfirmPassword("Pass123!");
        request.setBirthDate(LocalDate.of(1995, 5, 20));
        request.setPhoneNumber("+201234567890");
        request.setGender(Gender.MALE);

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginRequest_ValidData_ShouldPassValidation() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Pass123!");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_BlankEmail_ShouldFailValidation() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("Pass123!");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void authResponse_ConstructorAndGetters_ShouldWork() {
        // Arrange & Act
        AuthResponse response = new AuthResponse("token123", "USER", "john@example.com", 1L);

        // Assert
        assertEquals("token123", response.token());
        assertEquals("USER", response.role());
        assertEquals("john@example.com", response.email());
        assertEquals(1L, response.userId());
    }
}