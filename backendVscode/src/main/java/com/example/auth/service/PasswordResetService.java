package com.example.auth.service;

import com.example.auth.entity.PasswordResetToken;
import com.example.auth.entity.User;
import com.example.auth.repository.PasswordResetTokenRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        // Find user by email
        var userOptional = userRepository.findByEmail(email.toLowerCase());
        if (userOptional.isEmpty()) {
            // Don't reveal if email exists for security reasons
            // Just return success silently
            return;
        }

        User user = userOptional.get();

        // Delete any existing reset tokens for this user
        tokenRepository.deleteByUser(user);

        // Create new reset token (expires in 1 hour)
        PasswordResetToken resetToken = new PasswordResetToken(
                user,
                LocalDateTime.now().plusHours(1)
        );

        tokenRepository.save(resetToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Find token
        var tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        // Check if token is expired
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Check if token has already been used
        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }

        // Validate new password
        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$")) {
            throw new IllegalArgumentException("Password must contain at least one digit, one lowercase, one uppercase, one special character, and be 8-20 characters long");
        }

        // Get user and update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used and delete it
        resetToken.setUsed(true);
        tokenRepository.delete(resetToken);
    }
}
