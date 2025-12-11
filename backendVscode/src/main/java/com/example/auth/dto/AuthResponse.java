package com.example.auth.dto;

public record AuthResponse(String token, String role, String email, Long userId) {
    // Record automatically creates constructor, getters, equals, hashCode, toString
}