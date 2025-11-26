package com.example.smartLink.service;

import com.example.smartLink.controller.LikeCommentController;
import com.example.smartLink.entity.Comment;
import com.example.smartLink.entity.CommentsLike;
import com.example.smartLink.entity.CommentsLikeId;
import com.example.smartLink.entity.User;
import com.example.smartLink.exceptions.NotEnoughInformationException;
import com.example.smartLink.repository.CommentRepo;
import com.example.smartLink.repository.LikeCommentRepo;
import com.example.smartLink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    LikeCommentRepo likeCommentRepo;
    UserRepository userRepository;
    CommentRepo commentRepo;
    @Autowired
    public LikeService(LikeCommentRepo likeCommentRepo,UserRepository userRepository,CommentRepo commentRepo) {
        this.likeCommentRepo = likeCommentRepo;
        this.userRepository=userRepository;
        this.commentRepo=commentRepo;
    }
    @Transactional
    public void makeOrDeleteLike(Long userId, Long commentId) throws NotEnoughInformationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotEnoughInformationException("There is no such user"));

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotEnoughInformationException("There is no such comment"));

        CommentsLikeId commentsLikeId = new CommentsLikeId(userId, commentId);

        if (likeCommentRepo.existsById(commentsLikeId)) {
            likeCommentRepo.deleteById(commentsLikeId);
        } else {
            CommentsLike like = new CommentsLike();
            like.setId(commentsLikeId);
            like.setUser(user);
            like.setComment(comment);
            likeCommentRepo.save(like);
        }
    }
}
