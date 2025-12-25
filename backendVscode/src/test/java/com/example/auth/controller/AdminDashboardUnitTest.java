package com.example.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.CommentRepo;
import com.example.auth.repository.ReportRepository;
import com.example.auth.repository.CompanyProfileRepo;
import com.example.auth.repository.ReactionRepository;
import com.example.auth.repository.ConnectionRepository;
import com.example.auth.repository.NotificationRepository;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.VerificationTokenRepository;
import com.example.auth.repository.PasswordResetTokenRepository;
import com.example.auth.repository.LikeCommentRepo;
import com.example.auth.repository.PostAttachmentRepository;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.service.EmailService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminDashboardUnitTest {

    @Test
    void adminDashboard_DirectCall_ShouldReturnCorrectMessage() {
        UserRepository mockUserRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        JobSeekerProfileRepository mockJobRepo = mock(JobSeekerProfileRepository.class);
        PostRepository mockPostRepo = mock(PostRepository.class);
        CommentRepo mockCommentRepo = mock(CommentRepo.class);
        ReportRepository mockReportRepo = mock(ReportRepository.class);
        CompanyProfileRepo mockCompanyRepo = mock(CompanyProfileRepo.class);
        ReactionRepository mockReactionRepo = mock(ReactionRepository.class);
        ConnectionRepository mockConnectionRepo = mock(ConnectionRepository.class);
        NotificationRepository mockNotificationRepo = mock(NotificationRepository.class);
        JobApplicationRepository mockJobAppRepo = mock(JobApplicationRepository.class);
        VerificationTokenRepository mockVerifRepo = mock(VerificationTokenRepository.class);
        PasswordResetTokenRepository mockPassRepo = mock(PasswordResetTokenRepository.class);
        LikeCommentRepo mockLikeRepo = mock(LikeCommentRepo.class);
        PostAttachmentRepository mockPostAttachRepo = mock(PostAttachmentRepository.class);
        CompanyFollowerRepo mockFollowerRepo = mock(CompanyFollowerRepo.class);

        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo, mockReportRepo,
                mockCompanyRepo, mockReactionRepo, mockConnectionRepo, mockNotificationRepo, mockJobAppRepo, mockVerifRepo, 
                mockPassRepo, mockLikeRepo, mockPostAttachRepo, mockFollowerRepo);

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
        ReportRepository mockReportRepo = mock(ReportRepository.class);
        CompanyProfileRepo mockCompanyRepo = mock(CompanyProfileRepo.class);
        ReactionRepository mockReactionRepo = mock(ReactionRepository.class);
        ConnectionRepository mockConnectionRepo = mock(ConnectionRepository.class);
        NotificationRepository mockNotificationRepo = mock(NotificationRepository.class);
        JobApplicationRepository mockJobAppRepo = mock(JobApplicationRepository.class);
        VerificationTokenRepository mockVerifRepo = mock(VerificationTokenRepository.class);
        PasswordResetTokenRepository mockPassRepo = mock(PasswordResetTokenRepository.class);
        LikeCommentRepo mockLikeRepo = mock(LikeCommentRepo.class);
        PostAttachmentRepository mockPostAttachRepo = mock(PostAttachmentRepository.class);
        CompanyFollowerRepo mockFollowerRepo = mock(CompanyFollowerRepo.class);

        // Act
        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo, mockReportRepo,
                mockCompanyRepo, mockReactionRepo, mockConnectionRepo, mockNotificationRepo, mockJobAppRepo, mockVerifRepo, 
                mockPassRepo, mockLikeRepo, mockPostAttachRepo, mockFollowerRepo);

        // Assert
        assertEquals(AdminController.class, controller.getClass());
    }

    @Test
    void adminDashboard_MultipleCallsShouldReturnSameMessage() {
        UserRepository mockUserRepository = mock(UserRepository.class);
        EmailService mockEmailService = mock(EmailService.class);
        JobSeekerProfileRepository mockJobRepo = mock(JobSeekerProfileRepository.class);
        PostRepository mockPostRepo = mock(PostRepository.class);
        CommentRepo mockCommentRepo = mock(CommentRepo.class);
        ReportRepository mockReportRepo = mock(ReportRepository.class);
        CompanyProfileRepo mockCompanyRepo = mock(CompanyProfileRepo.class);
        ReactionRepository mockReactionRepo = mock(ReactionRepository.class);
        ConnectionRepository mockConnectionRepo = mock(ConnectionRepository.class);
        NotificationRepository mockNotificationRepo = mock(NotificationRepository.class);
        JobApplicationRepository mockJobAppRepo = mock(JobApplicationRepository.class);
        VerificationTokenRepository mockVerifRepo = mock(VerificationTokenRepository.class);
        PasswordResetTokenRepository mockPassRepo = mock(PasswordResetTokenRepository.class);
        LikeCommentRepo mockLikeRepo = mock(LikeCommentRepo.class);
        PostAttachmentRepository mockPostAttachRepo = mock(PostAttachmentRepository.class);
        CompanyFollowerRepo mockFollowerRepo = mock(CompanyFollowerRepo.class);

        AdminController controller =
                new AdminController(mockUserRepository, mockEmailService, mockJobRepo, mockPostRepo, mockCommentRepo, mockReportRepo,
                mockCompanyRepo, mockReactionRepo, mockConnectionRepo, mockNotificationRepo, mockJobAppRepo, mockVerifRepo, 
                mockPassRepo, mockLikeRepo, mockPostAttachRepo, mockFollowerRepo);

        // Act
        String result1 = controller.adminDashboard();
        String result2 = controller.adminDashboard();

        // Assert
        assertEquals(result1, result2);
        assertEquals("Welcome to Admin Dashboard!", result1);
        assertEquals("Welcome to Admin Dashboard!", result2);
    }
}
