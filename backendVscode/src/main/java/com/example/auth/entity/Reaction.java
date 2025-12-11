// Reaction.java
package com.example.auth.entity;
import com.example.auth.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Reaction() {}

    public Reaction(Long postId, Long userId, ReactionType reactionType) {
        this.postId = postId;
        this.userId = userId;
        this.reactionType = reactionType;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getReactionId() { return reactionId; }
    public void setReactionId(Long reactionId) { this.reactionId = reactionId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) { this.reactionType = reactionType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}