package com.example.auth.dto.JobDTO;

import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUpdateRequest {
    private String title;
    private String description;
    private ExperienceLevel experienceLevel;
    private JobType jobType;
    private LocationType locationType;
    private String jobLocation;
    private Integer salaryMin;
    private Integer salaryMax;
    private Instant deadline;
}
