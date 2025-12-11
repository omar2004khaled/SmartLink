package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long companyProfileId;
    private Long userId;
    private String companyName;
    private String website;
    private String industry;
    private String description;
    private String logoUrl;
    private String coverUrl;
    private Long numberOfFollowers;
    private Integer founded;
    private List<LocationDTO> locations;
    private Boolean isFollowing;
}
