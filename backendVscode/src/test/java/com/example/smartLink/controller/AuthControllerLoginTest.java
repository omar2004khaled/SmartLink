package com.example.smartLink.controller;

import com.example.smartLink.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void login_WithValidCredentials_Returns200AndToken() throws Exception {
        String email = "user@test.com";
        String password = "Pass123!";
        String token = "jwt-xyz";

        when(authService.login(email, password)).thenReturn(token);

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void login_WithInvalidCredentials_Returns401() throws Exception {
        String email = "user@test.com";
        String password = "BadPass";

        when(authService.login(email, password)).thenThrow(new IllegalArgumentException("Invalid password"));

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }
}
