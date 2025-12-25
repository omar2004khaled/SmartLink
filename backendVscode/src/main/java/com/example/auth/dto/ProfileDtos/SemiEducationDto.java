package com.example.auth.dto.ProfileDtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SemiEducationDto {
    private Long id;
    private String fieldOfStudy;
    private String description;
}
