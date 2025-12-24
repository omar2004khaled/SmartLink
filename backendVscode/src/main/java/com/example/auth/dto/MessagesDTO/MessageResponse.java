package com.example.auth.dto.MessagesDTO;

import lombok.*;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long unreadCount;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
