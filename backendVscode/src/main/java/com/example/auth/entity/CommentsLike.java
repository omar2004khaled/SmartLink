package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "Comments_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsLike {

    @EmbeddedId
    private CommentsLikeId id;

    @MapsId("userId")  // ✅ Changed from "UserId" to "userId"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @MapsId("commentId")  // ✅ Changed from "CommentId" to "commentId"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommentId")
    private Comment comment;
}