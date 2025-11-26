package com.example.smartLink.entity;

import com.example.smartLink.enums.TypeofAttachments;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
    private BigInteger AttachId;

    @Column(name = "URL" , length = 150 , nullable = false)
    private String AttachmentURL;

    @Column(name = "Type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeofAttachments typeofAttachments;

}