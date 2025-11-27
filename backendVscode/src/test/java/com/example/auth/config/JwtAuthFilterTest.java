package com.example.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthFilterTest {

    @Test
    void filter_ShouldSetAuthentication_WhenTokenValid() throws ServletException, IOException {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "very_secret_long_key_replace_this_make_it_very_long_and_secure");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 1000L * 60L);

        String email = "filteruser@example.com";
        String role = "USER";
        String token = jwtService.generateToken(email, role);

        JwtAuthFilter filter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContextHolder.clearContext();

        final boolean[] reached = {false};

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
                // At this point the filter must have set authentication
                var auth = SecurityContextHolder.getContext().getAuthentication();
                assertNotNull(auth, "Authentication should be set in SecurityContext");
                assertEquals(email, auth.getPrincipal());
                reached[0] = true;
            }
        };

        filter.doFilterInternal(request, response, chain);

        assertTrue(reached[0], "Filter chain should be executed");
    }
}
