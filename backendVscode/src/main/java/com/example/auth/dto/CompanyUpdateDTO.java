package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {
    private Long companyId;
    private String companyName;
    private String description;
    private String website;
    private String industry;
    private Integer founded;
    private String logoUrl;
    private String coverImageUrl;
    private List<LocationDTO> locations;
}
