package com.example.auth.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;

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

    @Mock
    private com.example.auth.config.JwtService jwtService;

    @Mock
    private com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository profileRepo;

    @Mock
    private com.example.auth.repository.CompanyProfileRepo companyRepo;

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
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(profileRepo.save(any())).thenReturn(new com.example.auth.entity.ProfileEntities.JobSeekerProfile());
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(new VerificationToken());

        // Act
        authService.register(validRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        verify(tokenRepository).save(tokenCaptor.capture());
        verify(emailService).sendVerificationEmail(anyString(), anyString());
        verify(profileRepo).save(any());

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

    // --- Login related tests ---
    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        String email = "john@example.com";
        String rawPassword = "Pass123!";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRole("USER");

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encoded")).thenReturn(true);

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken(email, "USER", "JOB_SEEKER")).thenReturn("jwt-token-123");

        // Act
        String token = authService.login(email, rawPassword);

        // Assert
        assertEquals("jwt-token-123", token);
    }

    @Test
    void login_WithInvalidPassword_ShouldThrow() {
        String email = "john@example.com";
        String rawPassword = "WrongPass";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encoded")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(email, rawPassword));
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void login_WithUnverifiedEmail_ShouldThrow() {
        String email = "john@example.com";
        String rawPassword = "Pass123!";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setEnabled(false);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(email, rawPassword));
        assertEquals("Please verify your email first", ex.getMessage());
    }

    // ========== GET USER ROLE TESTS ==========

    @Test
    void getUserRole_WithValidEmail_ShouldReturnUserRole() {
        // Arrange
        String email = "john@example.com";
        User user = new User();
        user.setEmail(email);
        user.setRole("USER");

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(java.util.Optional.of(user));

        // Act
        String role = authService.getUserRole(email);

        // Assert
        assertEquals("USER", role);
        verify(userRepository).findByEmail(email.toLowerCase());
    }

    @Test
    void getUserRole_WithAdminRole_ShouldReturnAdminRole() {
        // Arrange
        String email = "admin@example.com";
        User user = new User();
        user.setEmail(email);
        user.setRole("ADMIN");

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(java.util.Optional.of(user));

        // Act
        String role = authService.getUserRole(email);

        // Assert
        assertEquals("ADMIN", role);
    }

    @Test
    void getUserRole_WithNonexistentEmail_ShouldThrow() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.getUserRole(email));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserRole_ShouldBeCaseInsensitive() {
        // Arrange
        String email = "John@Example.com";
        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setRole("USER");

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(java.util.Optional.of(user));

        // Act
        String role = authService.getUserRole(email);

        // Assert
        assertEquals("USER", role);
        verify(userRepository).findByEmail(email.toLowerCase());
    }

    @Test
    void login_ShouldReturnTokenWithCorrectRole() {
        // Arrange
        String email = "alice@example.com";
        String rawPassword = "Pass123!";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRole("ADMIN");

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encoded")).thenReturn(true);

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken(email, "ADMIN", "JOB_SEEKER")).thenReturn("jwt-token-admin");

        // Act
        String token = authService.login(email, rawPassword);

        // Assert
        assertEquals("jwt-token-admin", token);
        verify(jwtService).generateToken(email, "ADMIN", "JOB_SEEKER");
    }
}