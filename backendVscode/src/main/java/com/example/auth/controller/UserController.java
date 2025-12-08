package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .map(user -> ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getFullName())))
            .orElse(ResponseEntity.notFound().build());
    }
    
    static class UserResponse {
        public Long id;
        public String email;
        public String fullName;
        
        public UserResponse(Long id, String email, String fullName) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
        }
    }
}
