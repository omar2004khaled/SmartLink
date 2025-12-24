package com.example.auth.dto.JobDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusChange {
    @NotNull
    String status;
    @NotNull
    Long JobApp;
}
