package com.example.auth.dto;

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
    String url;
    String type;
    @NotNull
    int postId;

}
