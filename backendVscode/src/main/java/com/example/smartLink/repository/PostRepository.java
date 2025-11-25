package com.example.smartLink.repository;

import com.example.smartLink.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAll();
    Post findById(int theId);
    void deleteById(int theId);
    Post save(Post post);
    List<Post> findByUserId(int theId);
    List<Post> findByContent(String theContent);
    @Query("UPDATE Post p SET p.content = :content WHERE p.id = :id")
    Post updatePost(Post post);

}