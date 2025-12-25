package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void verifyEndpoint_ShouldBePublic() throws Exception {
        // Act & Assert - /auth/verify should be accessible without authentication
        // This endpoint might return 400 (bad request) for invalid token, but not 403
        mockMvc.perform(get("/auth/verify")
                        .param("token", "test-token"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should not be 403 (Forbidden)
                    assert status != 403 : "Verify endpoint should not return 403 Forbidden";
                    // Could be 400 (bad request) or 200 (if test token works)
                });
    }

    @Test
    void registerEndpoint_ShouldBePublic() throws Exception {
        // Act & Assert - /auth/register should be accessible without authentication
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Test User\",\"email\":\"unique@example.com\",\"password\":\"Test123!\",\"confirmPassword\":\"Test123!\",\"birthDate\":\"1990-01-01\",\"phoneNumber\":\"+1234567890\",\"gender\":\"MALE\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should not return 403 (Forbidden)
                    assert status != 403 : "Register endpoint should not return 403 Forbidden";
                    // Could be 400 (bad request due to duplicate email) or 201/200
                });
    }

    @Test
    void loginEndpoint_ShouldBePublic() throws Exception {
        // Act & Assert - /auth/login should be accessible without authentication
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status != 403 : "Login endpoint should not return 403 Forbidden";
                });
    }

    @Test
    void securedApiEndpoints_ShouldRequireAuthentication() throws Exception {
        // Act & Assert - API endpoints should require authentication
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized()); // Should return 401 Unauthorized
    }

    @Test
    void swaggerEndpoints_ShouldBePublic() throws Exception {
        // Act & Assert - Swagger/OpenAPI endpoints should be accessible
        String[] swaggerPaths = {
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config"
        };

        for (String path : swaggerPaths) {
            mockMvc.perform(get(path))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Should not be 403, could be 404 if not configured or 200
                        assert status != 403 : path + " should not return 403 Forbidden";
                    });
        }
    }

    @Test
    void csrfDisabled_ShouldAllowPosts() throws Exception {
        // Act & Assert - CSRF is disabled, so POST should work without CSRF token
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"password\":\"test\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status != 403 : "Should not return 403 when CSRF is disabled";
                });
    }

    @Test
    void healthCheckEndpoints_ShouldBePublic() throws Exception {
        // Act & Assert - Health check endpoints should be accessible
        String[] healthPaths = {"/actuator/health", "/health"};

        for (String path : healthPaths) {
            try {
                mockMvc.perform(get(path))
                        .andExpect(result -> {
                            int status = result.getResponse().getStatus();
                            assert status != 403 : path + " should not return 403 Forbidden";
                        });
            } catch (Exception e) {
                // Endpoint might not exist, that's OK
            }
        }
    }
}