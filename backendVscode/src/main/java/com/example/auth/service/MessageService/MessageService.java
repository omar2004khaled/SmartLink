package com.example.auth.service.MessageService;

import com.example.auth.dto.MessagesDTO.MessageRequest;
import com.example.auth.dto.MessagesDTO.MessageResponse;
import com.example.auth.entity.Message;
import com.example.auth.entity.User;
import com.example.auth.entity.CompanyProfile;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.repository.MessagesRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.CompanyProfileRepo;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
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
    private final CompanyProfileRepo companyProfileRepo;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

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
        return messagesPage.map(message -> mapToResponseWithOtherUserInfo(message, userId));
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

    private MessageResponse mapToResponseWithOtherUserInfo(Message message, Long currentUserId) {
        User otherUser = message.getSender().getId().equals(currentUserId)? message.getReceiver():message.getSender();
        String profilePicture = getProfilePicture(otherUser);

        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.getIsRead())
                .otherUserId(otherUser.getId())
                .otherUserName(otherUser.getFullName())
                .otherUserProfilePicture(profilePicture)
                .otherUserType(otherUser.getUserType())
                .build();
    }

    private String getProfilePicture(User user) {
        if ("COMPANY".equals(user.getUserType())) {
            return companyProfileRepo.findByUser_Id(user.getId())
                    .map(CompanyProfile::getLogoUrl)
                    .orElse(null);
        } else {
            return jobSeekerProfileRepository.findByUser_Id(user.getId())
                    .map(JobSeekerProfile::getProfilePicUrl)
                    .orElse(null);
        }
    }
}