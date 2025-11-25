package com.example.smartLink.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/auth/verify?token=" + token;

        System.out.println("\n=== VERIFICATION EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Verification Link: " + link);
        System.out.println("Token: " + token);
        System.out.println("==========================\n");
    }
}