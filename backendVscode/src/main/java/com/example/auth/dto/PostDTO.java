package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private Long userId;
    private List<AttachmentDTO> attachments;
    private Timestamp createdAt;




    public PostDTO(Long id) {
        this.id = id;
    }
}
