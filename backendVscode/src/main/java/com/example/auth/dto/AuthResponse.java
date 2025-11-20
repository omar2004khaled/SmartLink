package com.example.auth.dto;

public record AuthResponse(String token, String role, String email) {
    // Record automatically creates constructor, getters, equals, hashCode, toString
}