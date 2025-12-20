package com.example.auth.dto;

import com.example.auth.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NotificationDto {
    private Long notificationId;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private Long relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
    
    public NotificationDto() {
    }
    
    public NotificationDto(Long notificationId, Long userId, NotificationType type, String title, 
                          String message, Boolean isRead, Long relatedEntityId, 
                          String relatedEntityType, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
        this.createdAt = createdAt;
    }

}

