package com.example.auth.config;

import com.example.auth.service.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2Service oAuth2Service;

    public OAuth2SuccessHandler(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String token = oAuth2Service.processOAuth2User(oauth2User, "GOOGLE");
       
        String redirectUrl = "http://localhost:5173/auth/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}