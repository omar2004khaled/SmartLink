// ReactionController.java
package com.example.auth.controller;

import com.example.auth.dto.ReactionDTO;
import com.example.auth.enums.ReactionType;
import com.example.auth.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reactions")
@CrossOrigin(origins = "http://localhost:5173")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<ReactionDTO> toggleReaction(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam ReactionType reactionType) {
        ReactionDTO result = reactionService.toggleReaction(postId, userId, reactionType);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<ReactionType, Integer>> getReactionCounts(@PathVariable Long postId) {
        Map<ReactionType, Integer> counts = reactionService.getReactionCounts(postId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/top/{postId}")
    public ResponseEntity<List<Map<String, Object>>> getTopReactions(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "3") int limit) {
        List<Map<String, Object>> topReactions = reactionService.getTopReactions(postId, limit);
        return ResponseEntity.ok(topReactions);
    }

    @GetMapping("/total/{postId}")
    public ResponseEntity<Integer> getTotalReactions(@PathVariable Long postId) {
        Integer total = reactionService.getTotalReactions(postId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/user")
    public ResponseEntity<ReactionType> getUserReaction(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        ReactionType userReaction = reactionService.getUserReaction(postId, userId);
        return ResponseEntity.ok(userReaction);
    }
}