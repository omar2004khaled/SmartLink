package com.Project.SmartLink.Repository;

import com.Project.SmartLink.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();

    Optional<Post> findById(Long theId);
    void deleteById(Long theId);
    Post save(Post post);
    @Query("SELECT p FROM Post p WHERE p.UserId = :userId")
    List<Post> findByUserId(@Param("userId") Long userId);
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:content%")
    List<Post> findByContent(@Param("content") String content);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.content = :content WHERE p.PostId = :id")
    int updateContent(@Param("id") Long id, @Param("content") String content);

}
