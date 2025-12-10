package com.example.auth.entity;

import com.example.auth.enums.TypeofAttachments;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Attachment {
    @Column(name = "attach_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachId;

    @Column(name = "url" , length = 150 , nullable = false)
    private String attachmentURL;

    @Column(name = "type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeofAttachments typeofAttachments;

}