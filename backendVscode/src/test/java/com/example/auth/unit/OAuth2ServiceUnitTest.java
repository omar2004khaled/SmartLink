package com.example.auth.unit;

import com.example.auth.config.JwtService;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.OAuth2Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    
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
        User savedUser = new User("Test User", "test@gmail.com", "GOOGLE", "google-id-123");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken("test@gmail.com", "USER")).thenReturn("jwt-token");

        // Act
        String result = oauth2Service.processOAuth2User(oauth2User, "GOOGLE");

        // Assert
        assertEquals("jwt-token", result);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("test@gmail.com", "USER");
    }

    @Test
    void processOAuth2User_ExistingUser_ReturnsToken() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("existing@gmail.com");
        existingUser.setRole("USER");
        existingUser.setProvider("GOOGLE");
        
        when(oauth2User.getAttribute("email")).thenReturn("existing@gmail.com");
        when(oauth2User.getAttribute("name")).thenReturn("Existing User");
        when(oauth2User.getAttribute("sub")).thenReturn("google-id-456");
        when(userRepository.findByEmail("existing@gmail.com")).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken("existing@gmail.com", "USER")).thenReturn("jwt-token");

        // Act
        String result = oauth2Service.processOAuth2User(oauth2User, "GOOGLE");

        // Assert
        assertEquals("jwt-token", result);
        verify(jwtService).generateToken("existing@gmail.com", "USER");
    }
}