package com.example.auth.dto.JobDTO;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long jobId;

    private String title;

    private String description;

    private String companyName;

    private String jobLocation;

    private Instant createdAt;
}

