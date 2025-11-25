package com.example.smartLink.repository;

import com.example.smartLink.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepo extends JpaRepository<Comment,Long> {
    @Query("SELECT Comment c Where")
}
