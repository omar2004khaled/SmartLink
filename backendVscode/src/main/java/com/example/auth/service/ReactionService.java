// ReactionServiceImpl.java
package com.example.auth.service;

import com.example.auth.dto.ReactionDTO;
import com.example.auth.entity.Reaction;
import com.example.auth.enums.*;
import com.example.auth.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Transactional
    public ReactionDTO toggleReaction(Long postId, Long userId, ReactionType reactionType) {
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionType) {
                reactionRepository.delete(reaction);
                return new ReactionDTO(postId, userId, null, false);
            } else {
                reaction.setReactionType(reactionType);
                reactionRepository.save(reaction);
                return new ReactionDTO(postId, userId, reactionType, true);
            }
        } else {
            Reaction newReaction = new Reaction(postId, userId, reactionType);
            reactionRepository.save(newReaction);
            return new ReactionDTO(postId, userId, reactionType, true);
        }
    }

    public Map<ReactionType, Integer> getReactionCounts(Long postId) {
        List<Object[]> results = reactionRepository.getReactionCountsByPostId(postId);
        Map<ReactionType, Integer> counts = new HashMap<>();

        for (Object[] result : results) {
            ReactionType type = (ReactionType) result[0];
            Long count = (Long) result[1];
            counts.put(type, count.intValue());
        }

        return counts;
    }

    public List<Map<String, Object>> getTopReactions(Long postId, int limit) {
        List<Object[]> results = reactionRepository.getReactionCountsByPostId(postId);

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> reactionData = new HashMap<>();
                    reactionData.put("type", (ReactionType) result[0]);
                    reactionData.put("count", (Long) result[1]);
                    return reactionData;
                })
                .collect(Collectors.toList());
    }


    public Integer getTotalReactions(Long postId) {
        return reactionRepository.findByPostId(postId).size();
    }

    public ReactionType getUserReaction(Long postId, Long userId) {
        Optional<Reaction> reaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        return reaction.map(Reaction::getReactionType).orElse(null);
    }
}