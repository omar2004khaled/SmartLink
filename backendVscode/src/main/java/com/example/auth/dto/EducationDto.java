package com.example.auth.dto;

import java.time.LocalDate;

public class EducationDto {
    private Long id;
    private String school;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
