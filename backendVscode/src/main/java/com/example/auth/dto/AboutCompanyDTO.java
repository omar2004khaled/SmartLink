package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutCompanyDTO {
    private String description;
    private String website;
    private String industry;
    private Integer founded;
    private List<LocationDTO> locations;
}
