package com.example.auth.controller.MessageTests;


import com.example.auth.controller.MessageControllers.MessageController;
import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;

import com.example.auth.service.MessageService.MessageService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MessageRequest messageRequest;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
        objectMapper = new ObjectMapper();

        messageRequest = MessageRequest.builder()
                .senderId(1L)
                .receiverId(2L)
                .content("Test message")
                .build();

        messageResponse = MessageResponse.builder()
                .id(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("Test message")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    @Test
    void sendMessage_Success() throws Exception {
        when(messageService.sendMessage(any(MessageRequest.class)))
                .thenReturn(messageResponse);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.content").value("Test message"))
                .andExpect(jsonPath("$.isRead").value(false));

        verify(messageService).sendMessage(any(MessageRequest.class));
    }

    @Test
    void sendMessage_Exception_ReturnsBadRequest() throws Exception {
        when(messageService.sendMessage(any(MessageRequest.class)))
                .thenThrow(new RuntimeException("Sender not found"));

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService).sendMessage(any(MessageRequest.class));
    }


    @Test
    void getConversation_Success() throws Exception {
        Page<MessageResponse> page =
                new PageImpl<>(List.of(messageResponse), PageRequest.of(0, 20), 1);

        when(messageService.getConversationBetweenTwoUsers(eq(1L), eq(2L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/messages/conversation")
                        .param("user1", "1")
                        .param("user2", "2")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(messageService).getConversationBetweenTwoUsers(eq(1L), eq(2L), any(Pageable.class));
    }

    @Test
    void getConversations_Success() throws Exception {
        Page<MessageResponse> page =
                new PageImpl<>(List.of(messageResponse), PageRequest.of(0, 20), 1);

        when(messageService.getConversations(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/messages/inbox")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(messageService).getConversations(eq(1L), any(Pageable.class));
    }

    @Test
    void countUnread_Success() throws Exception {
        when(messageService.countUnread(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/messages/unread/count")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(messageService).countUnread(1L);
    }

    @Test
    void markAsRead_Success() throws Exception {
        doNothing().when(messageService).markAsRead(1L, 2L);

        mockMvc.perform(post("/api/messages/read")
                        .param("senderId", "1")
                        .param("receiverId", "2"))
                .andExpect(status().isOk());

        verify(messageService).markAsRead(1L, 2L);
    }
}
