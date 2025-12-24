package com.example.auth.unit;

import com.example.auth.config.JwtService;
import com.example.auth.entity.User;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.OAuth2Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class OAuth2ServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository; // ADDED: This was missing

    @Mock
    private JwtService jwtService;

    @Mock
    private OAuth2User oauth2User;

    @InjectMocks
    private OAuth2Service oauth2Service;

    @Test
    void processOAuth2User_NewUser_CreatesUserAndReturnsToken() {
        // Arrange
        when(oauth2User.getAttribute("email")).thenReturn("test@gmail.com");
        when(oauth2User.getAttribute("name")).thenReturn("Test User");
        when(oauth2User.getAttribute("sub")).thenReturn("google-id-123");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); // Simulate saved user with ID
            return user;
        });

        // Mock the profile repository save
        when(jobSeekerProfileRepository.save(any())).thenReturn(null);

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken("test@gmail.com", "USER", "JOB_SEEKER")).thenReturn("jwt-token");

        // Act
        String result = oauth2Service.processOAuth2User(oauth2User, "GOOGLE");

        // Assert
        assertEquals("jwt-token", result);
        verify(userRepository).save(any(User.class));
        verify(jobSeekerProfileRepository, times(2)).save(any()); // Saves twice: new user + existing check
        verify(jwtService).generateToken("test@gmail.com", "USER", "JOB_SEEKER");
    }

    @Test
    void processOAuth2User_ExistingUser_ReturnsToken() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Existing User");
        existingUser.setEmail("existing@gmail.com");
        existingUser.setRole("USER");
        existingUser.setProvider("GOOGLE");
        existingUser.setEnabled(true);

        when(oauth2User.getAttribute("email")).thenReturn("existing@gmail.com");
        when(oauth2User.getAttribute("name")).thenReturn("Existing User");
        when(oauth2User.getAttribute("sub")).thenReturn("google-id-456");
        when(userRepository.findByEmail("existing@gmail.com")).thenReturn(Optional.of(existingUser));

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken("existing@gmail.com", "USER", "JOB_SEEKER")).thenReturn("jwt-token");

        // Act
        String result = oauth2Service.processOAuth2User(oauth2User, "GOOGLE");

        // Assert
        assertEquals("jwt-token", result);
        verify(jwtService).generateToken("existing@gmail.com", "USER", "JOB_SEEKER");
        verify(userRepository, never()).save(any(User.class)); // Should not save existing user
    }

    @Test
    void processOAuth2User_WithEmptyName_ShouldUseEmailAsName() {
        // Arrange
        when(oauth2User.getAttribute("email")).thenReturn("test@gmail.com");
        when(oauth2User.getAttribute("name")).thenReturn(null);
        when(oauth2User.getAttribute("sub")).thenReturn("google-id-789");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jobSeekerProfileRepository.save(any())).thenReturn(null);

        // FIXED: Added third parameter "JOB_SEEKER"
        when(jwtService.generateToken("test@gmail.com", "USER", "JOB_SEEKER")).thenReturn("jwt-token");

        // Act
        String result = oauth2Service.processOAuth2User(oauth2User, "GOOGLE");

        // Assert
        assertEquals("jwt-token", result);
        verify(userRepository).save(any(User.class));
    }
}