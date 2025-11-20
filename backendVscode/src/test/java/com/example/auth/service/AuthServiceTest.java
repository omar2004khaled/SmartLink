package com.example.auth.service;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<VerificationToken> tokenCaptor;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setFullName("John Doe");
        validRequest.setEmail("john@example.com");
        validRequest.setPassword("Pass123!");
        validRequest.setConfirmPassword("Pass123!");
        validRequest.setBirthDate(LocalDate.of(1995, 5, 20));
        validRequest.setPhoneNumber("+201234567890");
        validRequest.setGender(Gender.MALE);
    }

    @Test
    void register_WithValidData_ShouldSaveUserAndSendVerification() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        authService.register(validRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        verify(tokenRepository).save(tokenCaptor.capture());
        verify(emailService).sendVerificationEmail(anyString(), anyString());

        User savedUser = userCaptor.getValue();
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.isEnabled());
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void register_WithExistingPhone_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        assertEquals("Phone number already exists", exception.getMessage());
    }

    @Test
    void register_WithPasswordMismatch_ShouldThrowException() {
        // Arrange
        validRequest.setConfirmPassword("DifferentPass123!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    void register_WithWeakPassword_ShouldThrowException() {
        // Arrange
        validRequest.setPassword("weak");
        validRequest.setConfirmPassword("weak");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        // Match whatever your actual service returns
        assertTrue(exception.getMessage().contains("Password"));
    }

    @Test
    void register_WithUnderageUser_ShouldThrowException() {
        // Arrange
        validRequest.setBirthDate(LocalDate.now().minusYears(12)); // 12 years old

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        // Match whatever your actual service returns
        assertTrue(exception.getMessage().contains("13") || exception.getMessage().contains("age"));
    }

    @Test
    void register_WithOverageUser_ShouldThrowException() {
        // Arrange
        validRequest.setBirthDate(LocalDate.now().minusYears(121)); // 121 years old

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(validRequest));
        // Match whatever your actual service returns
        assertTrue(exception.getMessage().contains("age") || exception.getMessage().contains("birth date"));
    }
}