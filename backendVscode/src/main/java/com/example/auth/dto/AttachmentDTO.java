package com.example.auth.dto;

import com.example.auth.enums.TypeofAttachments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    private Long attachId;
    private String typeOfAttachment;
    private String attachmentURL;
}
