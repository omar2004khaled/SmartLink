package com.example.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String userType) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> users;
        
        if (!search.isEmpty() || !userType.isEmpty()) {
            users = userRepository.findUsersWithFilters(search, userType, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("totalElements", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("currentPage", users.getNumber());
        response.put("size", users.getSize());
        
        return ResponseEntity.ok(response);
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

    @PostMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already an ADMIN");
        }

        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User must verify email before promotion");
        }

        user.setRole("ADMIN");
        userRepository.save(user);
        
        // Log the action
        System.out.println("AUDIT LOG: Super Admin " + auth.getName() + " promoted user " + user.getEmail() + " (ID: " + userId + ") to ADMIN at " + java.time.LocalDateTime.now());

        return ResponseEntity.ok("User " + user.getEmail() + " promoted to ADMIN successfully!");
    }

    @PostMapping("/demote/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        if ("BigBoss@example.com".equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Cannot demote super admin");
        }

        if ("USER".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already a regular USER");
        }

        user.setRole("USER");
        userRepository.save(user);
        
        // Log the action
        System.out.println("AUDIT LOG: Super Admin " + auth.getName() + " demoted user " + user.getEmail() + " (ID: " + userId + ") to USER at " + java.time.LocalDateTime.now());

        return ResponseEntity.ok("User " + user.getEmail() + " demoted to USER successfully!");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        return ResponseEntity.ok(userOptional.get());
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        
        if ("BigBoss@example.com".equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Cannot delete super admin");
        }

        // Log the action
        System.out.println("AUDIT LOG: Admin " + auth.getName() + " deleted user " + user.getEmail() + " (ID: " + userId + ") at " + java.time.LocalDateTime.now());
        
        userRepository.delete(user);
        return ResponseEntity.ok("User " + user.getEmail() + " deleted successfully!");
    }
}