package com.example.smartLink.controller;

import org.springframework.web.bind.annotation.*;
import com.example.smartLink.service.VerificationService;

@RestController
@RequestMapping("/auth")
public class VerificationController {
    private final VerificationService verificationService;
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        return verificationService.verify(token);
    }
}
