package com.example.auth.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PostAttachmentKey implements Serializable {

    private Long PostId;
    private Long AttachId;

    public PostAttachmentKey() {}

    public PostAttachmentKey(Long postId, Long attachId) {
        this.PostId = postId;
        this.AttachId = attachId;
    }

    public Long getPostId() {
        return PostId;
    }

    public void setPostId(Long postId) {
        this.PostId = postId;
    }

    public Long getAttachId() {
        return AttachId;
    }

    public void setAttachId(Long attachId) {
        this.AttachId = attachId;
    }
}
