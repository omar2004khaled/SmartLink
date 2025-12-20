package com.example.auth.controller;

import com.example.auth.dto.NotificationDto;
import com.example.auth.entity.Notification;
import com.example.auth.enums.NotificationType;
import com.example.auth.service.NotificationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        Page<NotificationDto> notificationDtos = notifications.map(this::toDto);
        return ResponseEntity.ok(notificationDtos);
    }
    

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDto>> getAllUserNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationDtos);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationDtos);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@RequestParam Long userId) {
        Long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }
    

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type")
    public ResponseEntity<List<NotificationDto>> getNotificationsByType(
            @RequestParam Long userId,
            @RequestParam NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationDtos);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> getNotification(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.getNotificationById(notificationId);
            return ResponseEntity.ok(toDto(notification));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationCreateRequest request) {
        try {
            Notification notification = notificationService.createNotification(
                    request.getUserId(),
                    request.getType(),
                    request.getTitle(),
                    request.getMessage(),
                    request.getRelatedEntityId(),
                    request.getRelatedEntityType()
            );
            return ResponseEntity.ok(toDto(notification));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @Setter
    @Getter
    public static class NotificationCreateRequest {
        private Long userId;
        private NotificationType type;
        private String title;
        private String message;
        private Long relatedEntityId;
        private String relatedEntityType;

    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setRelatedEntityType(notification.getRelatedEntityType());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}

