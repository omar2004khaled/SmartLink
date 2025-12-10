package com.example.auth.dto.JobDTO;

import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobFilter {
    private String title;
    private ExperienceLevel experienceLevel;
    private JobType jobType;
    private LocationType locationType;
    private String jobLocation;
    private Integer minSalary;
    private Integer maxSalary;

    @Override
    public String toString() {
        return "JobFilter{" +
                "title='" + title + '\'' +
                ", experienceLevel=" + experienceLevel +
                ", jobType=" + jobType +
                ", locationType=" + locationType +
                ", jobLocation='" + jobLocation + '\'' +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                '}';
    }
}