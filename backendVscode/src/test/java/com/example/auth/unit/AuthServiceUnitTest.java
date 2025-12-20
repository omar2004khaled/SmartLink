package com.example.auth.unit;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.example.auth.config.JwtService jwtService;

    @Mock
    private com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository profileRepo;

    @Mock
    private com.example.auth.repository.CompanyProfileRepo companyRepo;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ValidRequest_CreatesUserAndSendsEmail() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        request.setPhoneNumber("+1234567890");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.MALE);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(profileRepo.save(any())).thenReturn(new com.example.auth.entity.ProfileEntities.JobSeekerProfile());
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(new VerificationToken());

        // Act
        authService.register(request);

        // Assert
        verify(userRepository).save(any(User.class));
        verify(profileRepo).save(any());
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_ExistingEmail_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void register_ExistingPhoneNumber_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPhoneNumber("+1234567890");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+1234567890")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));
        assertEquals("Phone number already exists", exception.getMessage());
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole("USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken("test@example.com", "USER", "JOB_SEEKER")).thenReturn("mock-jwt-token");

        // Act
        String result = authService.login("test@example.com", "Password123!");

        // Assert
        assertEquals("mock-jwt-token", result);
        verify(jwtService).generateToken("test@example.com", "USER", "JOB_SEEKER");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login("notfound@example.com", "password"));
        assertEquals("User not found", exception.getMessage());
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

}