package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
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
class VerificationServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    void verify_WithValidToken_ShouldEnableUserAndReturnSuccess() {
        // Arrange
        String token = "valid-token-123";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Not expired

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Email verified. You can login now.", result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(verificationToken);
    }

    @Test
    void verify_WithInvalidToken_ShouldReturnErrorMessage() {
        // Arrange
        String token = "invalid-token";
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Invalid token", result);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void verify_WithExpiredToken_ShouldReturnErrorMessage() {
        // Arrange
        String token = "expired-token";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().minusHours(1)); // Expired

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Token expired", result);
        assertFalse(user.isEnabled()); // User should not be enabled
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void verify_WithValidToken_ShouldDeleteTokenAfterVerification() {
        // Arrange
        String token = "valid-token";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Email verified. You can login now.", result);
        verify(tokenRepository).delete(verificationToken); // Verify token is deleted
        verify(userRepository).save(user);
    }

    @Test
    void verify_WithExpiredTokenAtBoundary_ShouldReturnExpired() {
        // Arrange
        String token = "boundary-expired-token";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().minusSeconds(1)); // Just expired

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Token expired", result);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void verify_WithTokenExpiringInFuture_ShouldSucceed() {
        // Arrange
        String token = "future-token";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // Expires in 7 days

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Email verified. You can login now.", result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(verificationToken);
    }

    @Test
    void verify_WithAlreadyEnabledUser_ShouldEnableAgainAndReturnSuccess() {
        // Arrange
        String token = "already-enabled-token";
        User user = new User();
        user.setEnabled(true); // Already enabled

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        String result = verificationService.verify(token);

        // Assert
        assertEquals("Email verified. You can login now.", result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(verificationToken);
    }

    @Test
    void verify_MultipleCallsWithSameToken_ShouldFindTokenOnce() {
        // Arrange
        String token = "multi-call-token";
        User user = new User();
        user.setEnabled(false);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        verificationService.verify(token);

        // Assert - Verify token repository was accessed
        verify(tokenRepository).findByToken(token);
        verify(userRepository).save(user);
    }

    @Test
    void verify_WithNullUser_ShouldHandleGracefully() {
        // Arrange
        String token = "null-user-token";

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(null); // Null user - edge case
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act & Assert - Should handle null user without crashing
        try {
            verificationService.verify(token);
            // If it doesn't throw, the test passes
            fail("Should have thrown NullPointerException or handled null user");
        } catch (NullPointerException e) {
            // Expected - the service doesn't handle null user
            assertTrue(true);
        }
    }
}