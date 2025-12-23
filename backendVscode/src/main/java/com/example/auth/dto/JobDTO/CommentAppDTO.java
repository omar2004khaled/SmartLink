package com.example.auth.dto.JobDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAppDTO {
    @NotNull
    @Size(min = 3)
    String comment;
    @NotNull
    Long JobAppId;
}
