package com.example.auth.service.ReportService;

import com.example.auth.dto.ReportDTO;
import com.example.auth.enums.ReportCategory;

import java.util.List;

public interface ReportService {
    ReportDTO createReport(Long postId, Long reporterId, ReportCategory category, String description);
    boolean hasUserReportedPost(Long postId, Long reporterId);
    List<ReportDTO> getReportsByPostId(Long postId);
    long getReportCountForPost(Long postId);
}