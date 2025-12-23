package com.example.auth.service;

import com.example.auth.entity.Comment;
import com.example.auth.entity.CommentsLike;
import com.example.auth.entity.CommentsLikeId;
import com.example.auth.entity.User;
import com.example.auth.exceptions.NotEnoughInformationException;
import com.example.auth.repository.CommentRepo;
import com.example.auth.repository.LikeCommentRepo;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    LikeCommentRepo likeCommentRepo;
    UserRepository userRepository;
    CommentRepo commentRepo;
    NotificationService notificationService;

    @Autowired
    public LikeService(LikeCommentRepo likeCommentRepo, UserRepository userRepository,
            CommentRepo commentRepo, NotificationService notificationService) {
        this.likeCommentRepo = likeCommentRepo;
        this.userRepository = userRepository;
        this.commentRepo = commentRepo;
        this.notificationService = notificationService;
    }

    @Transactional
    public void makeOrDeleteLike(Long userId, Long commentId) throws NotEnoughInformationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotEnoughInformationException("There is no such user"));

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotEnoughInformationException("There is no such comment"));

        CommentsLikeId commentsLikeId = new CommentsLikeId(userId, commentId);

        if (likeCommentRepo.existsById(commentsLikeId)) {
            // User is removing their like - no notification
            likeCommentRepo.deleteById(commentsLikeId);
        } else {
            // User is adding a new like - create notification
            CommentsLike like = new CommentsLike();
            like.setId(commentsLikeId);
            like.setUser(user);
            like.setComment(comment);
            likeCommentRepo.save(like);

            // Create notification for comment owner (if not liking own comment)
            if (!comment.getUser().getId().equals(userId)) {
                try {
                    notificationService.createCommentLikeNotification(
                            comment.getUser().getId(),
                            userId,
                            user.getFullName(),
                            commentId);
                } catch (Exception e) {
                    // Log error but don't fail the like operation
                    System.err.println("Failed to create comment like notification: " + e.getMessage());
                }
            }
        }
    }
}
