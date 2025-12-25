package com.example.auth.dto.ProfileDtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SemiEducationDto {
    private Long id;
    private String fieldOfStudy;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
