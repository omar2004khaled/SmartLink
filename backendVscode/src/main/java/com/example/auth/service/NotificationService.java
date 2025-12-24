package com.example.auth.service;

import com.example.auth.dto.NotificationDto;
import com.example.auth.entity.Notification;
import com.example.auth.entity.User;
import com.example.auth.enums.NotificationType;
import com.example.auth.repository.NotificationRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Notification createNotification(Long userId, NotificationType type, String title, String message,
            Long relatedEntityId, String relatedEntityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Push notification via WebSocket for instant delivery
        pushNotificationViaWebSocket(userId, savedNotification);

        return savedNotification;
    }

    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new RuntimeException("Notification not found or user does not have permission");
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("User does not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.findByUserAndType(userId, type);
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
    }

    @Transactional
    public Notification createConnectionRequestNotification(Long receiverId, Long senderId, String senderName) {
        return createNotification(
                receiverId,
                NotificationType.CONNECTION_REQUEST,
                "New Connection Request",
                senderName + " sent you a connection request",
                senderId,
                "USER");
    }

    @Transactional
    public Notification createConnectionAcceptedNotification(Long userId, Long acceptedById, String acceptedByName) {
        return createNotification(
                userId,
                NotificationType.CONNECTION_ACCEPTED,
                "Connection Accepted",
                acceptedByName + " accepted your connection request",
                acceptedById,
                "USER");
    }

    @Transactional
    public Notification createPostLikeNotification(Long postOwnerId, Long likerId, String likerName, Long postId) {
        return createNotification(
                postOwnerId,
                NotificationType.POST_LIKE,
                "Post Liked",
                likerName + " liked your post",
                postId,
                "POST");
    }

    @Transactional
    public Notification createPostCommentNotification(Long postOwnerId, Long commenterId, String commenterName,
            Long postId) {
        return createNotification(
                postOwnerId,
                NotificationType.POST_COMMENT,
                "New Comment",
                commenterName + " commented on your post",
                postId,
                "POST");
    }

    @Transactional
    public Notification createCommentLikeNotification(Long commentOwnerId, Long likerId, String likerName,
            Long commentId) {
        return createNotification(
                commentOwnerId,
                NotificationType.COMMENT_LIKE,
                "Comment Liked",
                likerName + " liked your comment",
                commentId,
                "COMMENT");
    }

    @Transactional
    public Notification createCompanyFollowedNotification(Long companyId, Long followerId, String followerName) {
        return createNotification(
                companyId,
                NotificationType.COMPANY_FOLLOWED,
                "New Follower",
                followerName + " started following your company",
                followerId,
                "USER");
    }

    @Transactional
    public Notification createApplicationStatusChangeNotification(Long applicantId, String jobTitle,
            String newStatus, Long applicationId) {
        return createNotification(
                applicantId,
                NotificationType.JOB_APPLICATION_STATUS_CHANGE,
                "Application Status Updated",
                "Your application for " + jobTitle + " has been updated to " + newStatus,
                applicationId,
                "JOB_APPLICATION");
    }

    @Transactional
    public Notification createApplicationCommentNotification(Long applicantId, String companyName,
            String jobTitle, Long applicationId) {
        return createNotification(
                applicantId,
                NotificationType.APPLICATION_COMMENT,
                "New Comment on Application",
                companyName + " commented on your application for " + jobTitle,
                applicationId,
                "JOB_APPLICATION");
    }

    @Transactional
    public Notification createNewJobPostNotification(Long followerId, String companyName,
            String jobTitle, Long jobId) {
        return createNotification(
                followerId,
                NotificationType.NEW_JOB_FROM_FOLLOWED_COMPANY,
                "New Job Opportunity",
                companyName + " posted a new job: " + jobTitle,
                jobId,
                "JOB");
    }

    private void pushNotificationViaWebSocket(Long userId, Notification notification) {
        try {
            // Convert to DTO to avoid Hibernate proxy serialization issues
            NotificationDto dto = new NotificationDto(
                    notification.getNotificationId(),
                    notification.getUser().getId(),
                    notification.getType(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getIsRead(),
                    notification.getRelatedEntityId(),
                    notification.getRelatedEntityType(),
                    notification.getCreatedAt());

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + userId,
                    dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
