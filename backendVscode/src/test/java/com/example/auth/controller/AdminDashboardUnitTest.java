package com.example.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.example.auth.repository.UserRepository;
import com.example.auth.service.EmailService;

class AdminDashboardUnitTest {

    @Test
    void adminDashboard_DirectCall_ShouldReturnCorrectMessage() {
        // Arrange - Create mock repository and controller instance
        UserRepository mockRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        AdminController controller = new AdminController(mockRepository, mockEmailService);
        
        // Act - Call method directly
        String result = controller.adminDashboard();
        
        // Assert - Verify result
        assertEquals("Welcome to Admin Dashboard!", result);
    }

    @Test
    void adminController_Constructor_ShouldAcceptRepository() {
        // Arrange - Create mock repository
        UserRepository mockRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        
        // Act - Create controller with repository
        AdminController controller = new AdminController(mockRepository, mockEmailService);
        
        // Assert - Controller should be created successfully
        assertEquals(AdminController.class, controller.getClass());
    }

    @Test
    void adminDashboard_MultipleCallsShouldReturnSameMessage() {
        // Arrange - Create controller
        UserRepository mockRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        AdminController controller = new AdminController(mockRepository, mockEmailService);
        
        // Act - Call method multiple times
        String result1 = controller.adminDashboard();
        String result2 = controller.adminDashboard();
        
        // Assert - Both calls should return same message
        assertEquals("Welcome to Admin Dashboard!", result1);
        assertEquals("Welcome to Admin Dashboard!", result2);
        assertEquals(result1, result2);
    }
}