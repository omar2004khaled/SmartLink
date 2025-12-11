package com.example.auth.repository;

import com.example.auth.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT * FROM comments WHERE post_id = :postId",
            countQuery = "SELECT COUNT(*) FROM comments WHERE post_id = :postId",
            nativeQuery = true)
    Page<Comment> findByPostIdNative(@Param("postId") Long postId, Pageable pageable);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comments WHERE post_id = :postId", nativeQuery = true)
    void deleteByPostId(@Param("postId") Long postId);
}
