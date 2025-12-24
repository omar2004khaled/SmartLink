package com.example.auth.service.MessageService;

import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;
import com.example.auth.entity.Message;
import com.example.auth.entity.User;
import com.example.auth.repository.MessagesRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;

    @Transactional
    public MessageResponse sendMessage(MessageRequest request) {
        
        User sender = userRepository.findById(request.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
                
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .build();


        message = messagesRepository.save(message);
        return mapToResponse(message);
    }

    public Page<MessageResponse> getConversationBetweenTwoUsers(Long user1, Long user2, Pageable pageable) {
        Page<Message> messagesPage = messagesRepository.findConversationBetweenTwoUsers(user1, user2, pageable);
        return messagesPage.map(this::mapToResponse);
    }

    public Page<MessageResponse> getConversations(Long userId, Pageable pageable) {
        Page<Message> messagesPage = messagesRepository.findConversations(userId, pageable);
  
        return messagesPage.map(this::mapToResponse);
    }

    public Long countUnread(Long userId) {
        return messagesRepository.countUnreadMessages(userId);
    }

    @Transactional
    public void markAsRead(Long senderId, Long receiverId) {
        messagesRepository.markAsRead(senderId, receiverId);
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.getIsRead())
                .build();
    }
    
}
