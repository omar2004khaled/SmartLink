package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.config.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
public class OAuth2Service {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2Service(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String processOAuth2User(OAuth2User oauth2User, String provider) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(name, email, provider, providerId);
                    return userRepository.save(newUser);
                });

        if (!user.getProvider().equals(provider)) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }
}