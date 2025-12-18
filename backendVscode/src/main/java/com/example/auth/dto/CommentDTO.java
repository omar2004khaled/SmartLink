package com.example.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("userId")
    Long userId;
    @Size(min = 2 ,max = 2000)
     @JsonProperty("text")
    String text;
    @JsonProperty("url")
    String url;
    @JsonProperty("type")
    String type;
    @NotNull
    @JsonProperty("postId")
    Long postId;

}
