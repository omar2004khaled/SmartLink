// ReactionRepository.java
package com.example.auth.repository;

import com.example.auth.entity.Reaction;
import com.example.auth.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Query("SELECT r FROM Reaction r WHERE r.postId = :postId AND r.userId = :userId")
    Optional<Reaction> findByPostIdAndUserId(@Param("postId") Long postId,@Param("userId") Long userId);
    @Query("SELECT r FROM Reaction r WHERE r.postId = :postId")
    List<Reaction> findByPostId(@Param("postId") Long postId);
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.postId = :postId AND r.reactionType = :reactionType")
    int countByPostIdAndReactionType(@Param("postId") Long postId, @Param("reactionType") ReactionType reactionType);

    @Query("SELECT r.reactionType, COUNT(r) as count FROM Reaction r WHERE r.postId = :postId GROUP BY r.reactionType ORDER BY COUNT(r) DESC")
    List<Object[]> getReactionCountsByPostId(@Param("postId") Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);
}