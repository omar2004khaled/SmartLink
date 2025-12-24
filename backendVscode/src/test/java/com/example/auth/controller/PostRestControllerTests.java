package com.example.auth.controller;

import com.example.auth.dto.PostDTO;
import com.example.auth.service.PostService.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostRestControllerTests {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostRestController postRestController;

    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        postDTO = new PostDTO(1L, "Test post content", 100L, "JOB_SEEKER",
                Arrays.asList(), new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void findAll_ShouldReturnPostList() {
        // Arrange
        List<PostDTO> posts = Arrays.asList(postDTO);
        when(postService.findAll(any())).thenReturn(posts);

        // Act
        List<PostDTO> result = postRestController.findAll(0, 5, "PostId", true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(postService, times(1)).findAll(any());
    }

    @Test
    void findById_ValidId_ShouldReturnPost() {
        // Arrange
        when(postService.findById(1L)).thenReturn(postDTO);

        // Act
        PostDTO result = postRestController.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postService, times(1)).findById(1L);
    }

    @Test
    void findById_InvalidId_ShouldReturnNull() {
        // Arrange
        when(postService.findById(999L)).thenReturn(null);

        // Act
        PostDTO result = postRestController.findById(999L);

        // Assert
        assertNull(result);
        verify(postService, times(1)).findById(999L);
    }

    @Test
    void addPost_ShouldSaveAndReturnPost() {
        // Arrange
        when(postService.save(postDTO)).thenReturn(postDTO);

        // Act
        PostDTO result = postRestController.addPost(postDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postService, times(1)).save(postDTO);
    }

    @Test
    void updatePost_ValidPost_ShouldUpdateAndReturnPost() {
        // Arrange
        when(postService.updatePost(1L, postDTO)).thenReturn(postDTO);

        // Act
        PostDTO result = postRestController.updatePost(1L, postDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postService, times(1)).updatePost(1L, postDTO);
    }

    @Test
    void updatePost_NullPost_ShouldReturnNull() {
        // Act
        PostDTO result = postRestController.updatePost(1L, null);

        // Assert
        assertNull(result);
        verify(postService, never()).updatePost(anyLong(), any());
    }

    @Test
    void deletePost_ShouldCallService() {
        // Arrange
        doNothing().when(postService).deleteById(1L);

        // Act
        postRestController.deletePost(1L);

        // Assert
        verify(postService, times(1)).deleteById(1L);
    }
}