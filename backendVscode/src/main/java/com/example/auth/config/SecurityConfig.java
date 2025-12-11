package com.example.auth.config;

import java.util.Arrays;

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

        public SecurityConfig(JwtService jwtService, OAuth2SuccessHandler oAuth2SuccessHandler) {
                this.jwtService = jwtService;
                this.oAuth2SuccessHandler = oAuth2SuccessHandler;
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
                                                                "/Post/**","/jobs/**"
                                                                 ,"/graphql","/apply/**",
                                                                "/Post/**","/comment/**"
                                                                 ,"/jobs/**"
                                                                 ,"/graphql","/apply/**",
                                                                 "/api/search/**", "/api/connections/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oAuth2SuccessHandler))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // ADD THIS BEAN
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5175", "http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}