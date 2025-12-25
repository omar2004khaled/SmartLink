package com.example.auth.entity;

import com.example.auth.enums.ReportCategory;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "Reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportId")
    private Long reportId;

    @Column(name = "PostId", nullable = false)
    private Long postId;

    @Column(name = "ReporterId", nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ReportCategory", nullable = false)
    private ReportCategory reportCategory;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "Timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "Status", nullable = false)
    private String status; // PENDING, REVIEWED, RESOLVED

    public Report(Long postId, Long reporterId, ReportCategory reportCategory, String description) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reportCategory = reportCategory;
        this.description = description;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = "PENDING";
    }
}