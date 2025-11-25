package com.example.smartLink.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        // Check if Authorization header is present and starts with "Bearer "
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7); // Remove "Bearer " prefix

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && jwtService.isTokenValid(token, username)) {
                // Extract role from token using JwtService
                String role = jwtService.extractRole(token);

                // Create authentication token
                var authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Invalid token - continue without authentication
        }

        chain.doFilter(request, response);
    }
}