package com.Project.SmartLink.DTO;

import com.Project.SmartLink.entity.Attachment;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public class PostDTO {
    private Long id;
    private String content;
    private Long userId;
    private List<Attachment> attachments;
    private Timestamp createdAt;

    public PostDTO(Long id , String content, Long userId, List<Attachment> attachments , Timestamp createdAt) { //on creating post
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.attachments = attachments;
        this.createdAt = createdAt;
    }
    public PostDTO(){

    }

    public PostDTO(String content, Long userId, List<Attachment> attachments, Timestamp createdAt) {
        this.content = content;
        this.userId = userId;
        this.attachments = attachments;
        this.createdAt = createdAt;
    }

    public PostDTO(Long id, String content, List<Attachment> attachments , Timestamp createdAt) { // on modifying post
        this.id = id;
        this.content = content;
        this.attachments = attachments;
        this.createdAt = createdAt;
    }

    public PostDTO(Long id) { // on deleting or searching for a post
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
