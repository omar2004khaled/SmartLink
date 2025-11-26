package com.example.smartLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigInteger;

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
