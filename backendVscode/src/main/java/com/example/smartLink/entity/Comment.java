package com.example.smartLink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentId")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostId", nullable = false)
    private Post post;

    @Column(name = "Content", length = 2500, nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RepliedTo")
    private Comment repliedTo;

    @OneToMany
    @JoinTable(
            name = "Comments_Attach",
            joinColumns = @JoinColumn(name = "CommentId"),
            inverseJoinColumns = @JoinColumn(name = "AttachId")
    )
    private List<Attachment> attachments = new ArrayList<>();
}
