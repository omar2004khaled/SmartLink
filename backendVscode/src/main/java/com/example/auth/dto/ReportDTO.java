package com.example.auth.dto;

import com.example.auth.enums.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long reportId;
    private Long postId;
    private Long reporterId;
    private ReportCategory reportCategory;
    private String description;
    private Timestamp timestamp;
    private String status;
}