package com.example.smartLink.repository;

import com.example.smartLink.controller.LikeCommentController;
import com.example.smartLink.entity.CommentsLike;
import com.example.smartLink.entity.CommentsLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommentRepo extends JpaRepository<CommentsLike, CommentsLikeId> {
}
