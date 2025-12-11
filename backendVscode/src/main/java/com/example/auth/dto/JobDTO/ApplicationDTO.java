package com.example.auth.dto.JobDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplicationDTO {
    String status;
    Long userId;
    Long jobId;
    String cvURL;
    String coverLetter;
}
