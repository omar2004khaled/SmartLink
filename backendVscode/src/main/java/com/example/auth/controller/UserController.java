package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/post/{postId}")
    public  ResponseEntity<?> getUserbyPostId(@PathVariable Long postId){
        Optional<User> user=userRepository.findByPostId(postId);
        if(user.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserResponse(user.get().getId(), user.get().getEmail(), user.get().getFullName(), user.get().getRole()));
    }
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #email")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .map(user -> ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole())))
            .orElse(ResponseEntity.notFound().build());
    }
    
    static class UserResponse {
        public Long id;
        public String email;
        public String fullName;
        public String role;
        
        public UserResponse(Long id, String email, String fullName, String role) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }
    }
}
