package com.Project.SmartLink.entity;

import jakarta.persistence.*;



@Entity
@Table(name = "Attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachId")
    private Long AttachId;

    @Column(name = "URL" , length = 150 , nullable = false)
    private String attachmentURL;

    @Column(name = "Type" , nullable = false)
    @Enumerated
    private TypeOfAttachment typeOfAttachment;

    public Long getAttachId() {
        return AttachId;
    }

    public void setAttachId(Long attachId) {
        AttachId = attachId;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public TypeOfAttachment getTypeOfAttachment() {
        return typeOfAttachment;
    }

    public void setTypeOfAttachment(TypeOfAttachment typeOfAttachment) {
        this.typeOfAttachment = typeOfAttachment;
    }
    public Attachment() {
    }

    public Attachment(String attachmentURL, TypeOfAttachment typeOfAttachment) {
        this.attachmentURL = attachmentURL;
        this.typeOfAttachment = typeOfAttachment;
    }
}
