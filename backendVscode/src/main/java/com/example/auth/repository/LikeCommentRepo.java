package com.example.auth.repository;

import com.example.auth.entity.CommentsLike;
import com.example.auth.entity.CommentsLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommentRepo extends JpaRepository<CommentsLike, CommentsLikeId> {
}
