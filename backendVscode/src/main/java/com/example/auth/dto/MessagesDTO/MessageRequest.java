package com.example.auth.dto.MessagesDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}
