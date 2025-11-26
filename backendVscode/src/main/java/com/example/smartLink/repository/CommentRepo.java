package com.example.smartLink.repository;

import com.example.smartLink.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT * FROM Comments WHERE postId = :postId",
            countQuery = "SELECT COUNT(*) FROM Comments WHERE postId = :postId",
            nativeQuery = true)
    Page<Comment> findByPostIdNative(@Param("postId") Long postId, Pageable pageable);
}
