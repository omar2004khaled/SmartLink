package com.example.auth.controller;

import com.example.auth.dto.ReactionDTO;
import com.example.auth.enums.ReactionType;
import com.example.auth.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionControllerTests {

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private ReactionController reactionController;

    private ReactionDTO reactionDTO;
    private Map<ReactionType, Integer> reactionCounts;
    private List<Map<String, Object>> topReactions;

    @BeforeEach
    void setUp() {
        reactionDTO = new ReactionDTO(1L, 100L, ReactionType.LIKE, true);

        reactionCounts = new HashMap<>();
        reactionCounts.put(ReactionType.LIKE, 5);
        reactionCounts.put(ReactionType.LOVE, 3);

        topReactions = Arrays.asList(
                Map.of("type", ReactionType.LIKE, "count", 5L),
                Map.of("type", ReactionType.LOVE, "count", 3L)
        );
    }

    @Test
    void toggleReaction_ShouldReturnReactionDTO() {
        // Arrange
        when(reactionService.toggleReaction(1L, 100L, ReactionType.LIKE))
                .thenReturn(reactionDTO);

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.toggleReaction(1L, 100L, ReactionType.LIKE);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getPostId());
        assertEquals(ReactionType.LIKE, response.getBody().getReactionType());
        verify(reactionService, times(1)).toggleReaction(1L, 100L, ReactionType.LIKE);
    }

    @Test
    void getReactionCounts_ShouldReturnCountsMap() {
        // Arrange
        when(reactionService.getReactionCounts(1L)).thenReturn(reactionCounts);

        // Act
        ResponseEntity<Map<ReactionType, Integer>> response = reactionController.getReactionCounts(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(5, response.getBody().get(ReactionType.LIKE));
        verify(reactionService, times(1)).getReactionCounts(1L);
    }

    @Test
    void getTopReactions_ShouldReturnTopReactions() {
        // Arrange
        when(reactionService.getTopReactions(1L, 3)).thenReturn(topReactions);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reactionController.getTopReactions(1L, 3);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reactionService, times(1)).getTopReactions(1L, 3);
    }

    @Test
    void getTopReactions_WithDefaultLimit_ShouldUseDefault() {
        // Arrange
        when(reactionService.getTopReactions(1L, 3)).thenReturn(topReactions);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reactionController.getTopReactions(1L, 3);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reactionService, times(1)).getTopReactions(1L, 3);
    }

    @Test
    void getTotalReactions_ShouldReturnTotalCount() {
        // Arrange
        when(reactionService.getTotalReactions(1L)).thenReturn(10);

        // Act
        ResponseEntity<Integer> response = reactionController.getTotalReactions(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody());
        verify(reactionService, times(1)).getTotalReactions(1L);
    }

    @Test
    void getUserReaction_ShouldReturnUserReactionType() {
        // Arrange
        when(reactionService.getUserReaction(1L, 100L)).thenReturn(ReactionType.LIKE);

        // Act
        ResponseEntity<ReactionType> response = reactionController.getUserReaction(1L, 100L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ReactionType.LIKE, response.getBody());
        verify(reactionService, times(1)).getUserReaction(1L, 100L);
    }
}