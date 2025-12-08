package com.example.auth.controller;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.PasswordResetRequest;
import com.example.auth.dto.ResetPasswordRequest;
import com.example.auth.dto.UserInfoResponse;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import com.example.auth.service.PasswordResetService;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, PasswordResetService passwordResetService, UserRepository userRepository) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Long userId = authService.register(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful. Check console for verification email.");
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            AuthResponse response = new AuthResponse(
                    token,
                    user.getRole(),
                    user.getEmail(),
                    user.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.createPasswordResetToken(request.getEmail());
            return ResponseEntity.ok("If your email exists, a reset link has been sent.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Server error. Try again.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successful.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Server error.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                UserInfoResponse response = new UserInfoResponse();
                response.setFullName(user.getFullName());
                response.setEmail(user.getEmail());
                response.setBirthDate(user.getBirthDate());
                response.setPhoneNumber(user.getPhoneNumber());
                response.setProvider(user.getProvider());
                response.setGender(user.getGender() != null ? user.getGender().toString() : null);
                response.setEnabled(user.isEnabled());
                response.setRole(user.getRole());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}