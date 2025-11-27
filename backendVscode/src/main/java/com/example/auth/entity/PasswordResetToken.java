package com.example.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private boolean used = false;

    // Constructors
    public PasswordResetToken() {
        this.createdAt = LocalDateTime.now();
    }

    public PasswordResetToken(User user, LocalDateTime expiresAt) {
        this.user = user;
        this.expiresAt = expiresAt;
        this.token = java.util.UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}