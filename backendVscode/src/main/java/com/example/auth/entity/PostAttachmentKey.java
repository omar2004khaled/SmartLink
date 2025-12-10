package com.example.auth.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostAttachmentKey implements Serializable {

    private Long postId;
    private Long attachId;

    public PostAttachmentKey() {}

    public PostAttachmentKey(Long postId, Long attachId) {
        this.postId = postId;
        this.attachId = attachId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getAttachId() {
        return attachId;
    }

    public void setAttachId(Long attachId) {
        this.attachId = attachId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostAttachmentKey that = (PostAttachmentKey) o;
        return Objects.equals(postId, that.postId) && Objects.equals(attachId, that.attachId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, attachId);
    }
}
