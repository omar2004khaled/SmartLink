package com.example.auth.repository;

import com.example.auth.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPostId(Long postId);
    List<Report> findByReporterId(Long reporterId);
    Optional<Report> findByPostIdAndReporterId(Long postId, Long reporterId);
    long countByPostId(Long postId);
}