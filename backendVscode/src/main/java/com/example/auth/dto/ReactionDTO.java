// ReactionDTO.java
package com.example.auth.dto;

import com.example.auth.enums.ReactionType;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDTO {
    private Long postId;
    private Long userId;
    private ReactionType reactionType;
    private boolean reacted;

}