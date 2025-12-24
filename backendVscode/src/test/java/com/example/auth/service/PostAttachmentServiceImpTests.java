package com.example.auth.service.PostAttachmentService;

import com.example.auth.entity.PostAttchment;
import com.example.auth.entity.PostAttachmentKey;
import com.example.auth.repository.PostAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostAttachmentServiceImpTests {

    @Mock
    private PostAttachmentRepository postAttachmentRepository;

    @InjectMocks
    private PostAttachmentServiceImp postAttachmentService;

    private PostAttchment postAttachment;
    private PostAttachmentKey postAttachmentKey;

    @BeforeEach
    void setUp() {
        postAttachmentKey = new PostAttachmentKey(1L, 2L);
        postAttachment = new PostAttchment(postAttachmentKey);
    }

    @Test
    void findAll_ShouldReturnAllPostAttachments() {
        // Arrange
        List<PostAttchment> expected = Arrays.asList(postAttachment);
        when(postAttachmentRepository.findAll()).thenReturn(expected);

        // Act
        List<PostAttchment> result = postAttachmentService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postAttachmentRepository, times(1)).findAll();
    }

    @Test
    void findPostsByIdOfAttachment_ShouldReturnPostIds() {
        // Arrange
        Long attachmentId = 2L;
        List<Long> expected = Arrays.asList(1L, 3L);
        when(postAttachmentRepository.findPostsByIdOfAttachment(attachmentId)).thenReturn(expected);

        // Act
        List<Long> result = postAttachmentService.findPostsByIdOfAttachment(attachmentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0));
        verify(postAttachmentRepository, times(1)).findPostsByIdOfAttachment(attachmentId);
    }

    @Test
    void findAttachmentsByIdOfPost_ShouldReturnAttachmentIds() {
        // Arrange
        Long postId = 1L;
        List<Long> expected = Arrays.asList(2L, 4L);
        when(postAttachmentRepository.findAttachmentsByIdOfPost(postId)).thenReturn(expected);

        // Act
        List<Long> result = postAttachmentService.findAttachmentsByIdOfPost(postId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0));
        verify(postAttachmentRepository, times(1)).findAttachmentsByIdOfPost(postId);
    }

    @Test
    void deletePostById_ShouldCallRepository() {
        // Arrange
        Long postId = 1L;
        doNothing().when(postAttachmentRepository).deletePostById(postId);

        // Act
        postAttachmentService.deletePostById(postId);

        // Assert
        verify(postAttachmentRepository, times(1)).deletePostById(postId);
    }

    @Test
    void deleteAttachmentById_ShouldCallRepository() {
        // Arrange
        Long attachmentId = 2L;
        Long postId = 1L;
        doNothing().when(postAttachmentRepository).deleteAttachmentById(attachmentId, postId);

        // Act
        postAttachmentService.deleteAttachmentById(attachmentId, postId);

        // Assert
        verify(postAttachmentRepository, times(1)).deleteAttachmentById(attachmentId, postId);
    }

    @Test
    void save_ShouldReturnSavedPostAttachment() {
        // Arrange
        when(postAttachmentRepository.save(any(PostAttchment.class))).thenReturn(postAttachment);

        // Act
        PostAttchment result = postAttachmentService.save(postAttachment);

        // Assert
        assertNotNull(result);
        assertEquals(postAttachmentKey, result.getId());
        verify(postAttachmentRepository, times(1)).save(postAttachment);
    }

    @Test
    void updatePostById_ShouldCallRepository() {
        // Arrange
        Long postId = 1L;
        Long attachmentId = 2L;
        doNothing().when(postAttachmentRepository).updatePostById(postId, attachmentId);

        // Act
        postAttachmentService.updatePostById(postId, attachmentId);

        // Assert
        verify(postAttachmentRepository, times(1)).updatePostById(postId, attachmentId);
    }

    @Test
    void updateAttachmentById_ShouldCallRepository() {
        // Arrange
        Long attachmentId = 2L;
        Long postId = 1L;
        doNothing().when(postAttachmentRepository).updateAttachmentById(attachmentId, postId);

        // Act
        postAttachmentService.updateAttachmentById(attachmentId, postId);

        // Assert
        verify(postAttachmentRepository, times(1)).updateAttachmentById(attachmentId, postId);
    }
}