package com.example.smartLink.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.smartLink.repository.UserRepository;
import com.example.smartLink.repository.VerificationTokenRepository;
import com.example.smartLink.entity.User;
import com.example.smartLink.entity.VerificationToken;
import com.example.smartLink.dto.RegisterRequest;
import com.example.smartLink.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo,
                       VerificationTokenRepository tokenRepo,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        // 1. Check if email already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2. Check if phone number already exists
        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // 3. Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // 4. Check password complexity
        String password = request.getPassword();
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$")) {
            throw new IllegalArgumentException("Password must contain at least one digit, one lowercase, one uppercase, one special character, and be 8-20 characters long");
        }

        // 5. Check age (must be at least 13 years old)
        LocalDate today = LocalDate.now();
        int age = Period.between(request.getBirthDate(), today).getYears();
        if (age < 13) {
            throw new IllegalArgumentException("You must be at least 13 years old");
        }
        if (age > 120) {
            throw new IllegalArgumentException("Invalid birth date");
        }

        // 6. Create and save user
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setBirthDate(request.getBirthDate());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setEnabled(false); // User must verify email first
        user.setRole("USER");

        userRepo.save(user);

        // 7. Create verification token
        VerificationToken verificationToken = new VerificationToken(user, LocalDateTime.now().plusDays(1));
        tokenRepo.save(verificationToken);

        // 8. Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());


    }
    public String login(String email, String password) {
        // 1. Find user by email
        var userOptional = userRepo.findByEmail(email.toLowerCase());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        // 2. Check if email is verified
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Please verify your email first");
        }

        // 3. Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 4. Generate JWT token
        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    public String getUserRole(String email) {
        var userOptional = userRepo.findByEmail(email.toLowerCase());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userOptional.get().getRole();
    }
}