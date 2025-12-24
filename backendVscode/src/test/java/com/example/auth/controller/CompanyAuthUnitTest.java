package com.example.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.example.auth.dto.LoginRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.PasswordResetService;

class CompanyAuthUnitTest {

    @Test
    void companyLogin_ValidCompanyUser_ShouldReturnToken() {
        // Arrange
        AuthService mockAuthService = mock(AuthService.class);
        PasswordResetService mockPasswordService = mock(PasswordResetService.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        AuthController controller = new AuthController(mockAuthService, mockPasswordService, mockUserRepository);
        
        LoginRequest request = new LoginRequest();
        request.setEmail("company@test.com");
        request.setPassword("password");
        
        User companyUser = new User();
        companyUser.setUserType("COMPANY");
        companyUser.setEmail("company@test.com");
        companyUser.setRole("USER");
        companyUser.setId(1L);
        
        when(mockAuthService.login("company@test.com", "password")).thenReturn("token123");
        when(mockUserRepository.findByEmail("company@test.com")).thenReturn(Optional.of(companyUser));
        
        // Act
        ResponseEntity<?> response = controller.companyLogin(request);
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void companyLogin_NonCompanyUser_ShouldReturnForbidden() {
        // Arrange
        AuthService mockAuthService = mock(AuthService.class);
        PasswordResetService mockPasswordService = mock(PasswordResetService.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        AuthController controller = new AuthController(mockAuthService, mockPasswordService, mockUserRepository);
        
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");
        
        User regularUser = new User();
        regularUser.setUserType("JOB_SEEKER");
        
        when(mockAuthService.login("user@test.com", "password")).thenReturn("token123");
        when(mockUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        
        // Act
        ResponseEntity<?> response = controller.companyLogin(request);
        
        // Assert
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void regularLogin_CompanyUser_ShouldReturnForbidden() {
        // Arrange
        AuthService mockAuthService = mock(AuthService.class);
        PasswordResetService mockPasswordService = mock(PasswordResetService.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        AuthController controller = new AuthController(mockAuthService, mockPasswordService, mockUserRepository);
        
        LoginRequest request = new LoginRequest();
        request.setEmail("company@test.com");
        request.setPassword("password");
        
        User companyUser = new User();
        companyUser.setUserType("COMPANY");
        
        when(mockAuthService.login("company@test.com", "password")).thenReturn("token123");
        when(mockUserRepository.findByEmail("company@test.com")).thenReturn(Optional.of(companyUser));
        
        // Act
        ResponseEntity<?> response = controller.login(request);
        
        // Assert
        assertEquals(403, response.getStatusCodeValue());
    }
}