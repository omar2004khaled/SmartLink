package com.example.auth.repository;

import com.example.auth.entity.CommentsLike;
import com.example.auth.entity.CommentsLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface LikeCommentRepo extends JpaRepository<CommentsLike, CommentsLikeId> {
    
    @Modifying
    @Transactional
    void deleteByUser_Id(Long userId);

    @Modifying
    @Transactional
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM Comments_like WHERE CommentId = :commentId", nativeQuery = true)
    void deleteByCommentId(@org.springframework.data.repository.query.Param("commentId") Long commentId);
}
