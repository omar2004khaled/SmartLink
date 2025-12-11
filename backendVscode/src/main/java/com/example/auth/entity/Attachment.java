package com.example.auth.entity;

import com.example.auth.enums.TypeofAttachments;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "Attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Attachment {
    @Column(name = "AttachId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AttachId;

    @Column(name = "URL" , length = 150 , nullable = false)
    @JsonProperty("attachmentURL")
    private String AttachmentURL;

    @Column(name = "Type" , nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("typeOfAttachment")
    private TypeofAttachments typeofAttachments;

}