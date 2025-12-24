// src/test/java/com/example/auth/controller/ReactionControllerTest.java
package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.enums.ReactionType;
import com.example.auth.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@AutoConfigureMockMvc
@SpringBootTest
class ReactionControllerTest {

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private ReactionController reactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toggleReaction_ShouldReturnOk() {
        // Arrange
        Long postId = 1L;
        Long userId = 1L;
        ReactionType reactionType = ReactionType.LIKE;
        ReactionDTO expected = new ReactionDTO(postId, userId, reactionType, true);

        when(reactionService.toggleReaction(postId, userId, reactionType))
                .thenReturn(expected);

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.toggleReaction(postId, userId, reactionType);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getReactionCounts_ShouldReturnCounts() {
        // Arrange
        Long postId = 1L;
        Map<ReactionType, Integer> expected = new HashMap<>();
        expected.put(ReactionType.LIKE, 5);
        expected.put(ReactionType.LOVE, 3);

        when(reactionService.getReactionCounts(postId)).thenReturn(expected);

        // Act
        ResponseEntity<Map<ReactionType, Integer>> response = reactionController.getReactionCounts(postId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }
}