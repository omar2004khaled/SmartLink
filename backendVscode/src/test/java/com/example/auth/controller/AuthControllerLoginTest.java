package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use WebMvcTest for controller layer testing
@WebMvcTest(AuthController.class) // Specify the controller to test
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository; // Mock repositories if needed

    @MockitoBean
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    // Remove @Autowired for repositories that aren't needed in controller tests
    // If you need integration tests, use @SpringBootTest instead

    @BeforeEach
    void setUp() {
        // Clear any mocked data
        // No database operations in unit tests
    }

    @Test
    void login_WithValidCredentials_Returns200AndToken() throws Exception {
        // Arrange
        String email = "user@test.com";
        String password = "Pass123!";
        String token = "jwt-xyz";

        when(authService.login(anyString(), anyString())).thenReturn(token);

        String body = """
            {
                "email": "%s",
                "password": "%s"
            }
            """.formatted(email, password);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void login_WithInvalidCredentials_Returns401() throws Exception {
        // Arrange
        String email = "user@test.com";
        String password = "BadPass";

        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        String body = """
            {
                "email": "%s",
                "password": "%s"
            }
            """.formatted(email, password);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void login_WithEmptyCredentials_ReturnsBadRequest() throws Exception {
        // Arrange
        String body = """
            {
                "email": "",
                "password": ""
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithMalformedJson_ReturnsBadRequest() throws Exception {
        // Arrange
        String malformedJson = "{email: \"test\", password: \"test\"}";

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}