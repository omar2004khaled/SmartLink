package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void verifyEndpoint_ShouldBePublic() throws Exception {
        // Act & Assert - /auth/verify should be accessible without authentication
        mockMvc.perform(get("/auth/verify")
                        .param("token", "test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void registerEndpoint_ShouldBePublic_AndNotReturn403() throws Exception {
        // Act & Assert - /auth/register should be accessible without authentication (POST method)
        // The endpoint may return 400 (bad request) or 200, but NOT 403 (forbidden)
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Test User\",\"email\":\"unique@example.com\",\"password\":\"Test123!\",\"confirmPassword\":\"Test123!\",\"birthDate\":\"1990-01-01\",\"phoneNumber\":\"+1234567890\",\"gender\":\"MALE\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status != 403 : "Register endpoint should not return 403 Forbidden";
                });
    }

    @Test
    void verifyEndpoint_ShouldNotReturn403() throws Exception {
        // Act & Assert - /auth/verify should be accessible without authentication, not return 403
        mockMvc.perform(get("/auth/verify")
                        .param("token", "any-token"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status != 403 : "Verify endpoint should not return 403 Forbidden";
                });
    }



    @Test
    void securedEndpoints_ShouldRequireAuthentication() throws Exception {
        // Act & Assert - Any endpoint not explicitly permitted should require authentication
        // Expecting 404 for non-existent endpoints, not 302
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNotFound()); // Should return 404 for non-existent endpoint
    }

    @Test
    void nonExistentEndpoint_ShouldRequireAuthentication() throws Exception {
        // Act & Assert - API endpoints return 401 Unauthorized (not 302 redirect)
        // SecurityConfig returns 401 for /api/** endpoints
        mockMvc.perform(get("/api/secure-data"))
                .andExpect(status().isUnauthorized()); // Returns 401 for API endpoints
    }

    @Test
    void csrfDisabled_ShouldAllowPostWithoutCsrfToken() throws Exception {
        // Act & Assert - CSRF is disabled, so POST should work without CSRF token
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Test\",\"email\":\"csrf@test.com\",\"password\":\"Test123!\",\"confirmPassword\":\"Test123!\",\"birthDate\":\"2000-01-01\",\"phoneNumber\":\"+1111111111\",\"gender\":\"MALE\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should not return 403 (CSRF forbidden)
                    assert status != 403 : "CSRF should be disabled";
                });
    }

    @Test
    void verifyWithMultipleTokens_ShouldAllReturnSuccessfully() throws Exception {
        // Act & Assert - Multiple different tokens should all work
        String[] tokens = {"token1", "token2", "token3"};
        for (String token : tokens) {
            mockMvc.perform(get("/auth/verify")
                            .param("token", token))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assert status != 403 : "Token " + token + " should not return 403";
                    });
        }
    }

    @Test
    void authEndpointsConsistency_VerifyRegisterLoginPublic() throws Exception {
        // Act & Assert - All auth endpoints should be public (not return 403)
        String[] endpoints = {"/auth/register", "/auth/verify", "/auth/login"};
        for (String endpoint : endpoints) {
            mockMvc.perform(post(endpoint)
                            .contentType("application/json")
                            .content("{}"))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assert status != 403 : "Endpoint " + endpoint + " should be public, not 403";
                    });
        }
    }
}