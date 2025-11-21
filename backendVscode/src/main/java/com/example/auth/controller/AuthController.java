package com.example.auth.controller;

import org.springframework.web.bind.annotation.*;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok().body("Registration successful. Check console for verification email.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ADD THIS LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());

            AuthResponse response = new AuthResponse(
                    token,
                    "USER",  // For now, all users are "USER" role
                    request.getEmail()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }
}