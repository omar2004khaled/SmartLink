package com.example.smartLink.service;

import org.springframework.stereotype.Service;
import com.example.smartLink.repository.VerificationTokenRepository;
import com.example.smartLink.repository.UserRepository;
import java.time.LocalDateTime;

@Service
public class VerificationService {
    private final VerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;

    public VerificationService(VerificationTokenRepository tokenRepo, UserRepository userRepo) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }

    public String verify(String token) {
        var opt = tokenRepo.findByToken(token);
        if (opt.isEmpty()) return "Invalid token";
        var vt = opt.get();
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) return "Token expired";
        var user = vt.getUser();
        user.setEnabled(true);
        userRepo.save(user);
        tokenRepo.delete(vt);
        return "Email verified. You can login now.";
    }
}
