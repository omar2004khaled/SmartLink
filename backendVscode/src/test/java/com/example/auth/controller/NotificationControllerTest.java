package com.example.auth.controller;

import com.example.auth.dto.NotificationDto;
import com.example.auth.entity.Notification;
import com.example.auth.entity.User;
import com.example.auth.enums.NotificationType;
import com.example.auth.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Notification notification;
    private NotificationDto notificationDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(1L);
        user.setFullName("Yaseen Asaad");

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

        notificationDto = new NotificationDto();
        notificationDto.setNotificationId(1L);
        notificationDto.setUserId(1L);
        notificationDto.setType(NotificationType.POST_LIKE);
        notificationDto.setTitle("Post Liked");
        notificationDto.setMessage("Jane Smith liked your post");
        notificationDto.setIsRead(false);
        notificationDto.setRelatedEntityId(123L);
        notificationDto.setRelatedEntityType("POST");
        notificationDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getUserNotifications_WithPagination_ShouldReturnPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(Arrays.asList(notification), pageable, 1);
        when(notificationService.getUserNotifications(eq(1L), any(Pageable.class)))
                .thenReturn(notificationPage);

        mockMvc.perform(get("/api/notifications")
                .param("userId", "1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].notificationId").value(1L))
                .andExpect(jsonPath("$.content[0].type").value("POST_LIKE"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(notificationService).getUserNotifications(eq(1L), any(Pageable.class));
    }

    @Test
    void getAllUserNotifications_ShouldReturnList() throws Exception {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getUserNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/all")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].notificationId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Post Liked"));

        verify(notificationService).getUserNotifications(1L);
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadOnly() throws Exception {
        List<Notification> unreadNotifications = Arrays.asList(notification);
        when(notificationService.getUnreadNotifications(1L)).thenReturn(unreadNotifications);

        mockMvc.perform(get("/api/notifications/unread")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isRead").value(false));

        verify(notificationService).getUnreadNotifications(1L);
    }

    @Test
    void getUnreadNotificationCount_ShouldReturnCount() throws Exception {
        when(notificationService.getUnreadNotificationCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread/count")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(notificationService).getUnreadNotificationCount(1L);
    }

    @Test
    void markAsRead_WithValidData_ShouldReturnOk() throws Exception {
        doNothing().when(notificationService).markAsRead(1L, 1L);

        mockMvc.perform(put("/api/notifications/1/read")
                .param("userId", "1"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L, 1L);
    }

    @Test
    void markAllAsRead_ShouldReturnOk() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/notifications/read-all")
                .param("userId", "1"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(1L);
    }

    @Test
    void deleteNotification_WithValidData_ShouldReturnNoContent() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L, 1L);

        mockMvc.perform(delete("/api/notifications/1")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1L, 1L);
    }

    @Test
    void getNotificationsByType_ShouldReturnFilteredNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getNotificationsByType(1L, NotificationType.POST_LIKE))
                .thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/type")
                .param("userId", "1")
                .param("type", "POST_LIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("POST_LIKE"));

        verify(notificationService).getNotificationsByType(1L, NotificationType.POST_LIKE);
    }

    @Test
    void getNotification_WithValidId_ShouldReturnNotification() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(notification);

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1L))
                .andExpect(jsonPath("$.title").value("Post Liked"));

        verify(notificationService).getNotificationById(1L);
    }

    @Test
    void getNotification_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(notificationService.getNotificationById(999L))
                .thenThrow(new RuntimeException("Notification not found"));

        mockMvc.perform(get("/api/notifications/999"))
                .andExpect(status().isNotFound());

        verify(notificationService).getNotificationById(999L);
    }

    @Test
    void createNotification_WithValidData_ShouldReturnNotification() throws Exception {
        NotificationController.NotificationCreateRequest request = 
                new NotificationController.NotificationCreateRequest();
        request.setUserId(1L);
        request.setType(NotificationType.POST_LIKE);
        request.setTitle("Post Liked");
        request.setMessage("Jane Smith liked your post");
        request.setRelatedEntityId(123L);
        request.setRelatedEntityType("POST");

        when(notificationService.createNotification(
                eq(1L),
                eq(NotificationType.POST_LIKE),
                eq("Post Liked"),
                eq("Jane Smith liked your post"),
                eq(123L),
                eq("POST")
        )).thenReturn(notification);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1L))
                .andExpect(jsonPath("$.type").value("POST_LIKE"));

        verify(notificationService).createNotification(
                eq(1L),
                eq(NotificationType.POST_LIKE),
                eq("Post Liked"),
                eq("Jane Smith liked your post"),
                eq(123L),
                eq("POST")
        );
    }

    @Test
    void createNotification_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        NotificationController.NotificationCreateRequest request = 
                new NotificationController.NotificationCreateRequest();
        request.setUserId(999L);
        request.setType(NotificationType.POST_LIKE);
        request.setTitle("Post Liked");
        request.setMessage("Message");

        when(notificationService.createNotification(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(notificationService).createNotification(any(), any(), any(), any(), any(), any());
    }
}

