package com.example.auth.service.MessageTest;

import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;
import com.example.auth.entity.Message;
import com.example.auth.entity.User;
import com.example.auth.repository.MessagesRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.MessageService.MessageService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessagesRepository messagesRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Message message;
    private MessageRequest messageRequest;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setFullName("Farouk Ashraf");
        sender.setEmail("faroukashraf36@gmail.com");
        sender.setPassword("AsA@1413243");
        sender.setBirthDate(LocalDate.of(2004, 6, 3));
        sender.setPhoneNumber("0987648921");
        sender.setEnabled(true);

        receiver = new User();
        receiver.setId(2L);
        receiver.setFullName("John Smith");
        receiver.setEmail("john@gmail.com");
        receiver.setPassword("WfR#5656856857");
        receiver.setBirthDate(LocalDate.of(1992, 5, 15));
        receiver.setPhoneNumber("0987654321");
        receiver.setEnabled(true);

        message = Message.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("Hello john")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messageRequest = MessageRequest.builder()
                .senderId(1L)
                .receiverId(2L)
                .content("Hello john")
                .build();
    }

    @Test
    void sendMessage_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(messagesRepository.save(any(Message.class))).thenReturn(message);

        MessageResponse response = messageService.sendMessage(messageRequest);


        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getSenderId());
        assertEquals(2L, response.getReceiverId());
        assertEquals("Hello john", response.getContent());
        assertFalse(response.getIsRead());

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(messagesRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_SenderNotFound_ThrowsException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(messageRequest);
        });

        assertEquals("Sender not found", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).findById(2L);
        verify(messagesRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_ReceiverNotFound_ThrowsException() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(messageRequest);
        });

        assertEquals("Receiver not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(messagesRepository, never()).save(any(Message.class));
    }

    @Test
    void getConversationBetweenTwoUsers_Success() {

        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = Arrays.asList(message);
        Page<Message> messagePage = new PageImpl<>(messages, pageable, messages.size());

        when(messagesRepository.findConversationBetweenTwoUsers(1L, 2L, pageable))
                .thenReturn(messagePage);


        Page<MessageResponse> result = messageService.getConversationBetweenTwoUsers(1L, 2L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(1L, result.getContent().get(0).getSenderId());
        assertEquals(2L, result.getContent().get(0).getReceiverId());

        verify(messagesRepository).findConversationBetweenTwoUsers(1L, 2L, pageable);
    }

    @Test
    void getConversations_Success() {

        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = Arrays.asList(message);
        Page<Message> messagePage = new PageImpl<>(messages, pageable, messages.size());

        when(messagesRepository.findConversations(1L, pageable))
                .thenReturn(messagePage);


        Page<MessageResponse> result = messageService.getConversations(1L, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(messagesRepository).findConversations(1L, pageable);
    }

    @Test
    void getConversations_EmptyResult() {

        Pageable pageable = PageRequest.of(0, 20);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(messagesRepository.findConversations(1L, pageable))
                .thenReturn(messagePage);

        Page<MessageResponse> result = messageService.getConversations(1L, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(messagesRepository).findConversations(1L, pageable);
    }

    @Test
    void countUnread_Success() {
        when(messagesRepository.countUnreadMessages(1L)).thenReturn(5L);

        Long count = messageService.countUnread(1L);

        assertNotNull(count);
        assertEquals(5L, count);

        verify(messagesRepository).countUnreadMessages(1L);
    }

    @Test
    void countUnread_ZeroUnread() {

        when(messagesRepository.countUnreadMessages(1L)).thenReturn(0L);

        Long count = messageService.countUnread(1L);

        assertNotNull(count);
        assertEquals(0L, count);

        verify(messagesRepository).countUnreadMessages(1L);
    }

    @Test
    void markAsRead_Success() {

        doNothing().when(messagesRepository).markAsRead(1L, 2L);

        messageService.markAsRead(1L, 2L);

        verify(messagesRepository).markAsRead(1L, 2L);
    }

    @Test
    void markAsRead_VerifyTransactional() {

        doNothing().when(messagesRepository).markAsRead(1L, 2L);

        messageService.markAsRead(1L, 2L);

        verify(messagesRepository, times(1)).markAsRead(1L, 2L);
    }
}