package com.example.smartLink.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    Long commentId;
    Long userId;
    @Size(min = 2 ,max = 2000)
    String text;
    String URL;
    @NotNull
    int postId;

}
