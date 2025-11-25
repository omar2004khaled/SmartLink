package com.example.smartLink.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    Long commentId;
    Long userId;
    @Size(min = 2 ,max = 2000)
    String text;
    String URL;
    String type;
    @NotNull
    int postId;

}
