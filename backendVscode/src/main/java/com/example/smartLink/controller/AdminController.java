package com.example.smartLink.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.smartLink.repository.UserRepository;
import com.example.smartLink.entity.User;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // All endpoints in this controller require ADMIN role
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard!";
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public String getStats() {
        long totalUsers = userRepository.count();
        long enabledUsers = userRepository.findAll().stream()
                .filter(User::isEnabled)
                .count();
        long adminUsers = userRepository.findAll().stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .count();

        return String.format("Total Users: %d, Enabled: %d, Admins: %d",
                totalUsers, enabledUsers, adminUsers);
    }

    // PROMOTION ENDPOINT - This is what you asked for!
    @PostMapping("/promote/{userId}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Check if user is already admin
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already an ADMIN");
        }

        // Check if user has verified email
        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User must verify email before promotion");
        }

        // Promote user to ADMIN
        user.setRole("ADMIN");
        userRepository.save(user);

        return ResponseEntity.ok("User " + user.getEmail() + " promoted to ADMIN successfully!");
    }

    // DEMOTE endpoint (optional)
    @PostMapping("/demote/{userId}")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Prevent demoting super admin
        if ("superadmin@example.com".equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Cannot demote super admin");
        }

        // Check if user is already regular user
        if ("USER".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already a regular USER");
        }

        // Demote user to USER
        user.setRole("USER");
        userRepository.save(user);

        return ResponseEntity.ok("User " + user.getEmail() + " demoted to USER successfully!");
    }

    // Get user by ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        return ResponseEntity.ok(userOptional.get());
    }
}