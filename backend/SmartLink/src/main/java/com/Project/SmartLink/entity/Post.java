package com.Project.SmartLink.entity;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostId")
    private BigInteger PostId;

    @Column(name = "UserId" , nullable = false)
    private BigInteger UserId;

    @Column(name = "content" , nullable = false , length = 2500)
    private String content  ;

    @Column(name = "CreatedAt")
    private Timestamp CreatedAt ;

    public BigInteger getPostId() {
        return PostId;
    }

    public void setPostId(BigInteger postId) {
        PostId = postId;
    }

    public BigInteger getUserId() {
        return UserId;
    }

    public void setUserId(BigInteger userId) {
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
}
