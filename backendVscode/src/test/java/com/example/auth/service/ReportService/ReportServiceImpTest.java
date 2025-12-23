package com.example.auth.service.ReportService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auth.entity.Report;
import com.example.auth.enums.ReportCategory;
import com.example.auth.repository.ReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportServiceImpTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImp reportService;

    @Test
    void createReport_success() {
        // Arrange
        Long postId = 1L;
        Long reporterId = 2L;

        Report report = new Report(postId, reporterId, ReportCategory.SPAM_OR_SCAM, "Spam post");

        when(reportRepository.findByPostIdAndReporterId(postId, reporterId))
                .thenReturn(Optional.empty());
        when(reportRepository.save(any(Report.class)))
                .thenReturn(report);

        // Act
        var result = reportService.createReport(
                postId, reporterId, ReportCategory.SPAM_OR_SCAM, "Spam post"
        );

        // Assert
        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(reporterId, result.getReporterId());
    }

    @Test
    void createReport_userAlreadyReported_throwsException() {
        // Arrange
        Long postId = 1L;
        Long reporterId = 2L;

        when(reportRepository.findByPostIdAndReporterId(postId, reporterId))
                .thenReturn(Optional.of(new Report()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                reportService.createReport(
                        postId, reporterId, ReportCategory.HATE_SPEECH, null
                )
        );
    }

    @Test
    void hasUserReportedPost_returnsTrue() {
        // Arrange
        when(reportRepository.findByPostIdAndReporterId(1L, 2L))
                .thenReturn(Optional.of(new Report()));

        // Act
        boolean result = reportService.hasUserReportedPost(1L, 2L);

        // Assert
        assertTrue(result);
    }
}
