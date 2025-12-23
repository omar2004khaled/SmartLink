package com.example.auth.service.ReportService;

import com.example.auth.dto.ReportDTO;
import com.example.auth.entity.Report;
import com.example.auth.enums.ReportCategory;
import com.example.auth.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportServiceImp implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public ReportDTO createReport(Long postId, Long reporterId, ReportCategory category, String description) {
        // Check if user already reported this post
        if (hasUserReportedPost(postId, reporterId)) {
            throw new IllegalArgumentException("User has already reported this post");
        }

        Report report = new Report(postId, reporterId, category, description);
        Report savedReport = reportRepository.save(report);
        return convertToDTO(savedReport);
    }

    @Override
    public boolean hasUserReportedPost(Long postId, Long reporterId) {
        Optional<Report> existingReport = reportRepository.findByPostIdAndReporterId(postId, reporterId);
        return existingReport.isPresent();
    }

    @Override
    public List<ReportDTO> getReportsByPostId(Long postId) {
        List<Report> reports = reportRepository.findByPostId(postId);
        return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public long getReportCountForPost(Long postId) {
        return reportRepository.countByPostId(postId);
    }

    private ReportDTO convertToDTO(Report report) {
        return new ReportDTO(
            report.getReportId(),
            report.getPostId(),
            report.getReporterId(),
            report.getReportCategory(),
            report.getDescription(),
            report.getTimestamp(),
            report.getStatus()
        );
    }
}