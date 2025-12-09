package com.example.auth.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name="post_attach")
public class PostAttchment {
    @EmbeddedId
    private PostAttachmentKey id;
    public PostAttchment(PostAttachmentKey id) {
        this.id = id;
    }
    public PostAttchment() {}

    public PostAttachmentKey getId() {
        return id;
    }

    public void setId(PostAttachmentKey id) {
        this.id = id;
    }
}
