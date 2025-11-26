package com.example.smartLink.repository;

import com.example.smartLink.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAll();
    Post findById(int theId);
    void deleteById(int theId);
    Post save(Post post);


}