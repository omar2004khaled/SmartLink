package com.example.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.CommentRepo;
import com.example.auth.service.EmailService;

class AdminDashboardUnitTest {

    @Test
    void adminDashboard_DirectCall_ShouldReturnCorrectMessage() {
        // Arrange
        UserRepository mockUserRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        JobSeekerProfileRepository mockJobRepo = mock(JobSeekerProfileRepository.class);
        PostRepository mockPostRepo = mock(PostRepository.class);
        CommentRepo mockCommentRepo = mock(CommentRepo.class);

        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo);

        // Act
        String result = controller.adminDashboard();

        // Assert
        assertEquals("Welcome to Admin Dashboard!", result);
    }

    @Test
    void adminController_Constructor_ShouldCreateInstance() {
        // Arrange
        UserRepository mockUserRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        JobSeekerProfileRepository mockJobRepo = mock(JobSeekerProfileRepository.class);
        PostRepository mockPostRepo = mock(PostRepository.class);
        CommentRepo mockCommentRepo = mock(CommentRepo.class);

        // Act
        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo);

        // Assert
        assertEquals(AdminController.class, controller.getClass());
    }

    @Test
    void adminDashboard_MultipleCallsShouldReturnSameMessage() {
        // Arrange
        UserRepository mockUserRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        JobSeekerProfileRepository mockJobRepo = mock(JobSeekerProfileRepository.class);
        PostRepository mockPostRepo = mock(PostRepository.class);
        CommentRepo mockCommentRepo = mock(CommentRepo.class);

        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo);

        // Act
        String result1 = controller.adminDashboard();
        String result2 = controller.adminDashboard();

        // Assert
        assertEquals(result1, result2);
        assertEquals("Welcome to Admin Dashboard!", result1);
        assertEquals("Welcome to Admin Dashboard!", result2);
    }
}
