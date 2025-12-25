package com.example.auth.unit;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.UserProfileDto;
import com.example.auth.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class DtoUnitTest {

    @Test
    void loginRequest_SettersAndGetters_WorkCorrectly() {
        // Arrange
        LoginRequest request = new LoginRequest();
        
        // Act
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        // Assert
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void authResponse_Constructor_WorksCorrectly() {
        // Act
        AuthResponse response = new AuthResponse("jwt-token", "USER", "test@example.com", 1L);
        
        // Assert
        assertEquals("jwt-token", response.token());
        assertEquals("USER", response.role());
        assertEquals("test@example.com", response.email());
        assertEquals(1L, response.userId());
    }

    @Test
    void registerRequest_AllFields_WorkCorrectly() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        
        // Act
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        request.setPhoneNumber("+1234567890");
        request.setBirthDate(birthDate);
        request.setGender(Gender.MALE);
        
        // Assert
        assertEquals("John Doe", request.getFullName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("Password123!", request.getPassword());
        assertEquals("Password123!", request.getConfirmPassword());
        assertEquals("+1234567890", request.getPhoneNumber());
        assertEquals(birthDate, request.getBirthDate());
        assertEquals(Gender.MALE, request.getGender());
    }

    @Test
    void registerRequest_DefaultConstructor_CreatesEmptyObject() {
        // Act
        RegisterRequest request = new RegisterRequest();
        
        // Assert
        assertNull(request.getFullName());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getConfirmPassword());
        assertNull(request.getPhoneNumber());
        assertNull(request.getBirthDate());
        assertNull(request.getGender());
    }

    @Test
    void loginRequest_DefaultConstructor_CreatesEmptyObject() {
        // Act
        LoginRequest request = new LoginRequest();
        
        // Assert
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void authResponse_WithNullValues_WorksCorrectly() {
        // Act
        AuthResponse response = new AuthResponse(null, null, null, null);
        
        // Assert
        assertNull(response.token());
        assertNull(response.role());
        assertNull(response.email());
        assertNull(response.userId());
    }

    @Test
    void userProfileDto_SettersAndGetters_WorkCorrectly() {
        // Arrange
        UserProfileDto dto = new UserProfileDto();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        
        // Act
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("+1234567890");
        dto.setBio("Software Developer");
        dto.setDateOfBirth(birthDate);
        
        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("+1234567890", dto.getPhone());
        assertEquals("Software Developer", dto.getBio());
        assertEquals(birthDate, dto.getDateOfBirth());
    }

    @Test
    void userProfileDto_Builder_WorksCorrectly() {
        // Arrange
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        
        // Act
        UserProfileDto dto = UserProfileDto.builder()
            .id(1L)
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .phone("+0987654321")
            .bio("Designer")
            .dateOfBirth(birthDate)
            .build();
        
        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals("+0987654321", dto.getPhone());
        assertEquals("Designer", dto.getBio());
        assertEquals(birthDate, dto.getDateOfBirth());
    }
}