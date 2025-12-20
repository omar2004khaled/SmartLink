package com.example.auth.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private final JwtService jwtService;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final List<String> allowedOrigins;

        public SecurityConfig(
                        JwtService jwtService,
                        OAuth2SuccessHandler oAuth2SuccessHandler,
                        @Value("${app.cors.allowed-origins}") String allowedOriginsProperty) {
                this.jwtService = jwtService;
                this.oAuth2SuccessHandler = oAuth2SuccessHandler;
                this.allowedOrigins = Arrays.stream(allowedOriginsProperty.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isEmpty())
                                .toList();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService);

                http.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/auth/**", "/auth/company/**", "/api/public",
                                                                "/oauth2/**", "/login/oauth2/**",
                                                                "/api/profiles/**", "/api/locations/**",
                                                                "/api/company/**", "/api/users/**", "/Post/add/**",
                                                                "/Post/**", "/comment/**", "/jobs/**",
                                                                "/graphql", "/apply/**", "/reactions/**",
                                                                "/api/search/**", "/api/connections/**",
                                                                "/api/notifications/**",
                                                                "/error", "/login/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oAuth2SuccessHandler))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        if (request.getRequestURI().startsWith("/api/")) {
                                                                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                                                                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                                                        } else {
                                                                response.sendRedirect("/login/oauth2/code/google");
                                                        }
                                                }))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(allowedOrigins);
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}