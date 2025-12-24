package com.example.auth.controller.MessageControllers;


import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;
import com.example.auth.service.MessageService.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketMessage;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    public static final String TOPIC_MESSAGES = "/topic/messages/";
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest message) {
        MessageResponse savedMessage = messageService.sendMessage(
                MessageRequest.builder()
                        .senderId(message.getSenderId())
                        .receiverId(message.getReceiverId())
                        .content(message.getContent())
                        .build()
        );

 
        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.getReceiverId(),
                savedMessage
        );

        messagingTemplate.convertAndSend(
                TOPIC_MESSAGES + message.getSenderId(),
                savedMessage
        );
    }
}
