package com.example.auth.dto.JobDTO;
import com.example.auth.enums.ExperienceLevel;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemiJobDto {
    private Long jobId;
    private String title;
    private String description;
    private ExperienceLevel experienceLevel;

}
