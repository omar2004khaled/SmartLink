package com.Project.SmartLink.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "Attachment")
public class PostAttachment {
    @Column(name = "AttachId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger AttachId;

    @Column(name = "URL" , length = 150 , nullable = false)
    private String AttachmentURL;

    @Column(name = "Type" , nullable = false)
    private TypeOfAttachment TypeOfAttachment;
}
