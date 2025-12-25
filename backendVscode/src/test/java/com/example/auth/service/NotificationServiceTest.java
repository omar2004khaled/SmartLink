package com.example.auth.service;

import com.example.auth.entity.Notification;
import com.example.auth.entity.User;
import com.example.auth.enums.NotificationType;
import com.example.auth.repository.NotificationRepository;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Yaseen asaad");
        user.setEmail("Yaseen@example.com");

        notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .type(NotificationType.POST_LIKE)
                .title("Post Liked")
                .message("Jane Smith liked your post")
                .isRead(false)
                .relatedEntityId(123L)
                .relatedEntityType("POST")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createNotification_WithValidData_ShouldCreateNotification() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(
                1L,
                NotificationType.POST_LIKE,
                "Post Liked",
                "Jane Smith liked your post",
                123L,
                "POST");

        assertNotNull(result);
        assertEquals(NotificationType.POST_LIKE, result.getType());
        assertEquals("Post Liked", result.getTitle());
        assertEquals(false, result.getIsRead());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_WithInvalidUserId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.createNotification(
                        999L,
                        NotificationType.POST_LIKE,
                        "Title",
                        "Message",
                        123L,
                        "POST"));

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void getUserNotifications_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(Arrays.asList(notification));
        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(notificationPage);

        Page<Notification> result = notificationService.getUserNotifications(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(notificationRepository).findByUser_IdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    void getUserNotifications_WithoutPagination_ShouldReturnList() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(1L))
                .thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository).findByUser_IdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadOnly() {
        List<Notification> unreadNotifications = Arrays.asList(notification);
        when(notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(unreadNotifications);

        List<Notification> result = notificationService.getUnreadNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
        verify(notificationRepository).findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUnreadNotificationCount_ShouldReturnCount() {
        when(notificationRepository.countUnreadByUserId(1L)).thenReturn(5L);

        Long result = notificationService.getUnreadNotificationCount(1L);

        assertEquals(5L, result);
        verify(notificationRepository).countUnreadByUserId(1L);
    }

    @Test
    void markAsRead_WithValidData_ShouldMarkAsRead() {
        when(notificationRepository.markAsRead(1L, 1L)).thenReturn(1);

        notificationService.markAsRead(1L, 1L);

        verify(notificationRepository).markAsRead(1L, 1L);
    }

    @Test
    void markAsRead_WithInvalidData_ShouldThrowException() {
        when(notificationRepository.markAsRead(999L, 1L)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(999L, 1L));

        assertEquals("Notification not found or user does not have permission", exception.getMessage());
    }

    @Test
    void markAllAsRead_ShouldMarkAllAsRead() {
        when(notificationRepository.markAllAsReadByUserId(1L)).thenReturn(3);

        notificationService.markAllAsRead(1L);

        verify(notificationRepository).markAllAsReadByUserId(1L);
    }

    @Test
    void deleteNotification_WithValidUser_ShouldDelete() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(1L, 1L);

        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_WithInvalidUser_ShouldThrowException() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.deleteNotification(1L, 999L));

        assertEquals("User does not have permission to delete this notification", exception.getMessage());
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    void deleteNotification_WithNonExistentNotification_ShouldThrowException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.deleteNotification(999L, 1L));

        assertEquals("Notification not found", exception.getMessage());
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    void getNotificationsByType_ShouldReturnFilteredNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserAndType(1L, NotificationType.POST_LIKE))
                .thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByType(1L, NotificationType.POST_LIKE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NotificationType.POST_LIKE, result.get(0).getType());
        verify(notificationRepository).findByUserAndType(1L, NotificationType.POST_LIKE);
    }

    @Test
    void getNotificationById_WithValidId_ShouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Notification result = notificationService.getNotificationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getNotificationId());
        verify(notificationRepository).findById(1L);
    }

    @Test
    void getNotificationById_WithInvalidId_ShouldThrowException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.getNotificationById(999L));

        assertEquals("Notification not found with id: 999", exception.getMessage());
    }

    @Test
    void createConnectionRequestNotification_ShouldCreateCorrectNotification() {
        User sender = new User();
        sender.setId(2L);
        sender.setFullName("Jane Smith");

        Notification expectedNotification = Notification.builder()
                .notificationId(2L)
                .user(user)
                .type(NotificationType.CONNECTION_REQUEST)
                .title("New Connection Request")
                .message("Jane Smith sent you a connection request")
                .isRead(false)
                .relatedEntityId(2L)
                .relatedEntityType("USER")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createConnectionRequestNotification(1L, 2L, "Jane Smith");

        assertNotNull(result);
        assertEquals(NotificationType.CONNECTION_REQUEST, result.getType());
        assertEquals("New Connection Request", result.getTitle());
        assertTrue(result.getMessage().contains("Jane Smith"));
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createConnectionAcceptedNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(3L)
                .user(user)
                .type(NotificationType.CONNECTION_ACCEPTED)
                .title("Connection Accepted")
                .message("Jane Smith accepted your connection request")
                .isRead(false)
                .relatedEntityId(2L)
                .relatedEntityType("USER")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createConnectionAcceptedNotification(1L, 2L, "Jane Smith");

        assertNotNull(result);
        assertEquals(NotificationType.CONNECTION_ACCEPTED, result.getType());
        assertEquals("Connection Accepted", result.getTitle());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createPostLikeNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(4L)
                .user(user)
                .type(NotificationType.POST_LIKE)
                .title("Post Liked")
                .message("Jane Smith liked your post")
                .isRead(false)
                .relatedEntityId(123L)
                .relatedEntityType("POST")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createPostLikeNotification(1L, 2L, "Jane Smith", 123L);

        assertNotNull(result);
        assertEquals(NotificationType.POST_LIKE, result.getType());
        assertEquals(123L, result.getRelatedEntityId());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createPostCommentNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(5L)
                .user(user)
                .type(NotificationType.POST_COMMENT)
                .title("New Comment")
                .message("Jane Smith commented on your post")
                .isRead(false)
                .relatedEntityId(123L)
                .relatedEntityType("POST")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createPostCommentNotification(1L, 2L, "Jane Smith", 123L);

        assertNotNull(result);
        assertEquals(NotificationType.POST_COMMENT, result.getType());
        assertEquals("New Comment", result.getTitle());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createCommentLikeNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(6L)
                .user(user)
                .type(NotificationType.COMMENT_LIKE)
                .title("Comment Liked")
                .message("Jane Smith liked your comment")
                .isRead(false)
                .relatedEntityId(456L)
                .relatedEntityType("COMMENT")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createCommentLikeNotification(1L, 2L, "Jane Smith", 456L);

        assertNotNull(result);
        assertEquals(NotificationType.COMMENT_LIKE, result.getType());
        assertEquals("Comment Liked", result.getTitle());
        assertEquals(456L, result.getRelatedEntityId());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createCompanyFollowedNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(7L)
                .user(user)
                .type(NotificationType.COMPANY_FOLLOWED)
                .title("New Follower")
                .message("Jane Smith started following your company")
                .isRead(false)
                .relatedEntityId(2L)
                .relatedEntityType("USER")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createCompanyFollowedNotification(1L, 2L, "Jane Smith");

        assertNotNull(result);
        assertEquals(NotificationType.COMPANY_FOLLOWED, result.getType());
        assertEquals("New Follower", result.getTitle());
        assertTrue(result.getMessage().contains("Jane Smith"));
        assertTrue(result.getMessage().contains("following your company"));
        assertEquals(2L, result.getRelatedEntityId());
        assertEquals("USER", result.getRelatedEntityType());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createApplicationStatusChangeNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(8L)
                .user(user)
                .type(NotificationType.JOB_APPLICATION_STATUS_CHANGE)
                .title("Application Status Updated")
                .message("Your application for Senior Developer has been updated to ACCEPTED")
                .isRead(false)
                .relatedEntityId(123L)
                .relatedEntityType("APPLICATION")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createApplicationStatusChangeNotification(
                1L, "Senior Developer", "ACCEPTED", 123L);

        assertNotNull(result);
        assertEquals(NotificationType.JOB_APPLICATION_STATUS_CHANGE, result.getType());
        assertEquals("Application Status Updated", result.getTitle());
        assertTrue(result.getMessage().contains("Senior Developer"));
        assertTrue(result.getMessage().contains("ACCEPTED"));
        assertEquals(123L, result.getRelatedEntityId());
        assertEquals("APPLICATION", result.getRelatedEntityType());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createApplicationCommentNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(9L)
                .user(user)
                .type(NotificationType.APPLICATION_COMMENT)
                .title("New Comment on Application")
                .message("Tech Corp commented on your application for Senior Developer")
                .isRead(false)
                .relatedEntityId(123L)
                .relatedEntityType("APPLICATION")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createApplicationCommentNotification(
                1L, "Tech Corp", "Senior Developer", 123L);

        assertNotNull(result);
        assertEquals(NotificationType.APPLICATION_COMMENT, result.getType());
        assertEquals("New Comment on Application", result.getTitle());
        assertTrue(result.getMessage().contains("Tech Corp"));
        assertTrue(result.getMessage().contains("Senior Developer"));
        assertEquals(123L, result.getRelatedEntityId());
        assertEquals("APPLICATION", result.getRelatedEntityType());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNewJobPostNotification_ShouldCreateCorrectNotification() {
        Notification expectedNotification = Notification.builder()
                .notificationId(10L)
                .user(user)
                .type(NotificationType.NEW_JOB_FROM_FOLLOWED_COMPANY)
                .title("New Job Posted")
                .message("Tech Corp posted a new job: Senior Developer")
                .isRead(false)
                .relatedEntityId(456L)
                .relatedEntityType("JOB")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        Notification result = notificationService.createNewJobPostNotification(
                1L, "Tech Corp", "Senior Developer", 456L);

        assertNotNull(result);
        assertEquals(NotificationType.NEW_JOB_FROM_FOLLOWED_COMPANY, result.getType());
        assertEquals("New Job Posted", result.getTitle());
        assertTrue(result.getMessage().contains("Tech Corp"));
        assertTrue(result.getMessage().contains("Senior Developer"));
        assertEquals(456L, result.getRelatedEntityId());
        assertEquals("JOB", result.getRelatedEntityType());
        verify(notificationRepository).save(any(Notification.class));
    }
}
