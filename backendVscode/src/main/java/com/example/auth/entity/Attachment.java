package com.example.auth.entity;

import com.example.auth.enums.TypeofAttachments;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "attachment")
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
    private String AttachmentURL;

    @Column(name = "Type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeofAttachments typeofAttachments;

}