package com.example.auth.dto.JobDTO;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@AllArgsConstructor
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
