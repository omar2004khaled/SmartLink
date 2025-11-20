package com.example.auth.controller;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.enums.Gender;
import com.example.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // NEW: Replaces @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Arrange
        RegisterRequest request = createValidRegisterRequest();
        doNothing().when(authService).register(any(RegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful. Check console for verification email."));
    }

    @Test
    void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = createValidRegisterRequest();
        doThrow(new IllegalArgumentException("Email already exists"))
                .when(authService).register(any(RegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Pass123!");
        request.setConfirmPassword("Pass123!");
        request.setBirthDate(LocalDate.of(1995, 5, 20));
        request.setPhoneNumber("+201234567890");
        request.setGender(Gender.MALE);
        return request;
    }
}