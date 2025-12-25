package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Test
    void generateAndExtractClaims_shouldWork() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "very_secret_long_key_replace_this_make_it_very_long_and_secure");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 1000L * 60L);

        String email = "alice@example.com";
        String role = "USER";

        String token = jwtService.generateToken(email, role);
        assertNotNull(token);

        assertEquals(email, jwtService.extractEmail(token));
        assertEquals(email, jwtService.extractUsername(token));
        assertEquals(role, jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    void expiredToken_shouldBeInvalid() throws InterruptedException {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "very_secret_long_key_replace_this_make_it_very_long_and_secure");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 50L); // 50ms expiry

        String token = jwtService.generateToken("bob@example.com", "USER");
        assertNotNull(token);

        // wait for it to expire
        Thread.sleep(100);

        assertFalse(jwtService.isTokenValid(token, "bob@example.com"));
    }
}
