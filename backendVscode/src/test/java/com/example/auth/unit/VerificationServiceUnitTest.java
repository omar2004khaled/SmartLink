package com.example.auth.unit;

import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
import com.example.auth.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceUnitTest {

    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private VerificationService verificationService;

    @Test
    void verify_ValidToken_EnablesUser() {
        // Arrange
        User user = new User();
        user.setEnabled(false);
        
        VerificationToken token = new VerificationToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        // Act
        String result = verificationService.verify("valid-token");

        // Assert
        assertEquals("Email verified. You can login now.", result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }

    @Test
    void verify_InvalidToken_ReturnsError() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        String result = verificationService.verify("invalid-token");

        // Assert
        assertEquals("Invalid token", result);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void verify_ExpiredToken_ReturnsError() {
        // Arrange
        VerificationToken token = new VerificationToken();
        token.setToken("expired-token");
        token.setExpiresAt(LocalDateTime.now().minusHours(1));
        
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        // Act
        String result = verificationService.verify("expired-token");

        // Assert
        assertEquals("Token expired", result);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}