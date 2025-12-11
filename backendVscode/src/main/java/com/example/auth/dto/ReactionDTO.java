// ReactionDTO.java
package com.example.auth.dto;

import com.example.auth.enums.ReactionType;

import lombok.*;
@Setter
@Getter
public class ReactionDTO {
    private Long postId;
    private Long userId;
    private ReactionType reactionType;
    private boolean reacted;
    public ReactionDTO() {}

    public ReactionDTO(Long postId, Long userId, ReactionType reactionType, boolean reacted) {
        this.postId = postId;
        this.userId = userId;
        this.reactionType = reactionType;
        this.reacted = reacted;
    }

}