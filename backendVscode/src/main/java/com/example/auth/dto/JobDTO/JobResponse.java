package com.example.auth.dto.JobDTO;

import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long jobId;

    private String title;

    private String description;

    private String companyName;

    private String jobLocation;
    private ExperienceLevel experienceLevel;
    private JobType jobType;
    private LocationType locationType;

    private Instant createdAt;
    private Integer salaryMin;
    private Integer salaryMax;

    private Instant deadline;
    private boolean isApplied;
}

