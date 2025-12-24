package com.example.auth.controller.MessageControllers;

import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;
import com.example.auth.service.MessageService.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) {
        try {
            MessageResponse response = messageService.sendMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/conversation")
    public Page<MessageResponse> getConversation(@RequestParam Long user1,@RequestParam Long user2,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page, size);
        return messageService.getConversationBetweenTwoUsers(user1, user2, pageable);
    }

    @GetMapping("/inbox")
    public Page<MessageResponse> getConversations(@RequestParam Long userId,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageService.getConversations(userId, pageable);
    }

    @GetMapping("/unread/count")
    public Long countUnread(@RequestParam Long userId) {
        return messageService.countUnread(userId);
    }

    @PostMapping("/read")
    public void markAsRead(@RequestParam Long senderId, @RequestParam Long receiverId) {
        messageService.markAsRead(senderId, receiverId);
    }
}
