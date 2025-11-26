package com.example.auth.unit;

import com.example.auth.config.JwtService;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceExtendedUnitTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private AuthService authService;

    @Test
    void register_PasswordMismatch_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("DifferentPassword!");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register(request));
        assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    void register_WeakPassword_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPassword("weak");
        request.setConfirmPassword("weak");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register(request));
        assertTrue(exception.getMessage().contains("Password"));
    }

    @Test
    void register_UnderageUser_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        request.setBirthDate(LocalDate.now().minusYears(10)); // 10 years old
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register(request));
        assertTrue(exception.getMessage().contains("13"));
    }

    @Test
    void register_OverageUser_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        request.setBirthDate(LocalDate.now().minusYears(125)); // 125 years old
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register(request));
        assertTrue(exception.getMessage().contains("birth date"));
    }

    @Test
    void login_UserNotEnabled_ThrowsException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(false);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.login("test@example.com", "password"));
        assertEquals("Please verify your email first", exception.getMessage());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.login("test@example.com", "wrongPassword"));
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void getUserRole_ValidUser_ReturnsRole() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole("ADMIN");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        String role = authService.getUserRole("test@example.com");

        // Assert
        assertEquals("ADMIN", role);
    }

    @Test
    void getUserRole_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.getUserRole("notfound@example.com"));
        assertEquals("User not found", exception.getMessage());
    }
}