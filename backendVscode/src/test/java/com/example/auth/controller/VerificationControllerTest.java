package com.example.auth.controller;

import com.example.auth.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VerificationService verificationService;

    @Test
    @WithMockUser
    void verify_WithValidToken_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        String token = "valid-token-123";
        String expectedMessage = "Email verified. You can login now.";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser // NEW: Add mock user to bypass security
    void verify_WithInvalidToken_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        String token = "invalid-token";
        String expectedMessage = "Invalid token";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser // NEW: Add mock user to bypass security
    void verify_WithExpiredToken_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        String token = "expired-token";
        String expectedMessage = "Token expired";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser
    void verify_WithEmptyToken_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        String token = "";
        String expectedMessage = "Invalid token";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser
    void verify_WithNullTokenParameter_ShouldHandle() throws Exception {
        // Act & Assert - Test behavior with missing token parameter
        mockMvc.perform(get("/auth/verify"))
                .andExpect(result -> {
                    // Should handle gracefully - either 200 or 400, but not 500
                    int status = result.getResponse().getStatus();
                    assert status != 500 : "Should not return 500 Internal Server Error";
                });
    }

    @Test
    @WithMockUser
    void verify_WithSpecialCharactersInToken_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        String token = "token-with-!@#$%^&*()";
        String expectedMessage = "Invalid token";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser
    void verify_WithLongToken_ShouldReturnErrorMessage() throws Exception {
        // Arrange
        String token = "a".repeat(1000); // Very long token
        String expectedMessage = "Invalid token";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @WithMockUser
    void verify_ServiceCalledWithCorrectToken() throws Exception {
        // Arrange
        String token = "test-service-call";
        String expectedMessage = "Email verified. You can login now.";

        when(verificationService.verify(token)).thenReturn(expectedMessage);

        // Act
        mockMvc.perform(get("/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk());

        // Assert - Verify service was called with correct parameter
        verify(verificationService).verify(token);
        verify(verificationService, times(1)).verify(anyString());
    }
}