package com.example.auth.dto.JobDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationDTO {
    private Long id;  // lowercase
    private String name;
    private String email;
    private String status;
    private Long userId;
    private Long jobId;
    private String cvURL;
    private String coverLetter;
    private List<String> comments;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;  // Changed from dateTimeFormat
}