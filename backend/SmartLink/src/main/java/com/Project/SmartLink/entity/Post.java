package com.Project.SmartLink.entity;
import jakarta.persistence.*;


import java.sql.Timestamp;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostId")
    private Long PostId;

    @Column(name = "UserId" , nullable = false)
    private Long UserId;

    @Column(name = "content" , nullable = false , length = 2500)
    private String content  ;

    @Column(name = "CreatedAt")
    private Timestamp CreatedAt ;

    public Long getPostId() {
        return PostId;
    }

    public void setPostId(Long postId) {
        PostId = postId;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Post() {
    }

    public Post(Long postId, Long userId, String content) {
        this.PostId = postId;
        this.UserId = userId;
        this.content = content;
    }

    public Post(Long userId, String content) {
        UserId = userId;
        this.content = content;
        CreatedAt = new Timestamp(System.currentTimeMillis());
    }
}
