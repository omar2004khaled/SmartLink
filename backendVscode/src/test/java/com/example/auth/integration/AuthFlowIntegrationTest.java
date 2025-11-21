package com.example.auth.integration;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.VerificationToken;
import com.example.auth.repository.VerificationTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @Autowired
        private VerificationTokenRepository tokenRepository;

    @Test
    void completeFlow_registerVerifyLoginAndAccessProtected() throws Exception {
        String unique = "user" + System.currentTimeMillis() + "@test.com";

        RegisterRequest req = new RegisterRequest();
        req.setFullName("Integration User");
        req.setEmail(unique);
        req.setPassword("Pass123!");
        req.setConfirmPassword("Pass123!");
        req.setBirthDate(LocalDate.of(1990,1,1));
        // ensure unique phone number to avoid collisions with existing test data
        req.setPhoneNumber("+1" + System.currentTimeMillis());
        req.setGender(com.example.auth.enums.Gender.MALE);

        // Register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Find verification token for created user
        var tokens = tokenRepository.findAll();
        VerificationToken found = tokens.stream()
                .filter(t -> t.getUser() != null && unique.equals(t.getUser().getEmail()))
                .findFirst().orElse(null);

        assertThat(found).isNotNull();

        // Verify
        mockMvc.perform(get("/auth/verify").param("token", found.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified. You can login now."));

        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail(unique);
        login.setPassword("Pass123!");

        var loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        // Access protected endpoint
        mockMvc.perform(get("/api/protected").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("You accessed protected data"));
    }
}
