package com.example.auth.service.PostService;

import com.example.auth.dto.PostDTO;
import com.example.auth.entity.*;
import com.example.auth.enums.TypeofAttachments;
import com.example.auth.repository.*;
import com.example.auth.service.AttachmentService.AttachmentService;
import com.example.auth.service.PostAttachmentService.PostAttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImpTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private PostAttachmentService postAttachmentService;

    @Mock
    private CommentRepo commentRepository;

    @InjectMocks
    private PostServiceImp postService;

    private Post post;
    private PostDTO postDTO;
    private Attachment attachment;
    private List<Long> attachmentIds;

    @BeforeEach
    void setUp() {
        attachmentIds = Arrays.asList(1L, 2L);

        attachment = new Attachment();
        attachment.setAttachId(1L);
        attachment.setAttachmentURL("https://example.com/image.jpg");
        attachment.setTypeofAttachments(TypeofAttachments.Image);

        post = new Post(100L, "Test post content");
        post.setPostId(1L);
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        List<Attachment> attachments = Arrays.asList(attachment);
        postDTO = new PostDTO(1L, "Test post content", 100L, attachments, new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void findAll_WithPosts_ShouldReturnPostDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post));

        when(postRepository.findAll(pageable)).thenReturn(postPage);
        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(attachmentIds);
        when(attachmentService.findById(1L)).thenReturn(Optional.of(attachment));
        when(attachmentService.findById(2L)).thenReturn(Optional.empty());

        // Act
        List<PostDTO> result = postService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(postRepository, times(1)).findAll(pageable);
        verify(postAttachmentService, times(1)).findAttachmentsByIdOfPost(1L);
    }

    @Test
    void findAll_WithNoPosts_ShouldReturnNull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> emptyPage = new PageImpl<>(Collections.emptyList());
        when(postRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        List<PostDTO> result = postService.findAll(pageable);

        // Assert
        assertNull(result);
        verify(postRepository, times(1)).findAll(pageable);
        verify(postAttachmentService, never()).findAttachmentsByIdOfPost(any());
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPostDTO() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(attachmentIds);
        when(attachmentService.findById(1L)).thenReturn(Optional.of(attachment));
        when(attachmentService.findById(2L)).thenReturn(Optional.empty());

        // Act
        PostDTO result = postService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test post content", result.getContent());
        verify(postRepository, times(1)).findById(1L);
        verify(postAttachmentService, times(1)).findAttachmentsByIdOfPost(1L);
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnNull() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        PostDTO result = postService.findById(1L);

        // Assert
        assertNull(result);
        verify(postRepository, times(1)).findById(1L);
        verify(postAttachmentService, never()).findAttachmentsByIdOfPost(any());
    }

    @Test
    void deleteById_ShouldDeletePostAndRelatedData() {
        // Arrange
        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(attachmentIds);
        when(commentRepository.findByPostIdNative(eq(1L), any(Pageable.class)))
                .thenReturn(Page.empty())
                .thenReturn(Page.empty());
        doNothing().when(postAttachmentService).deleteAttachmentById(anyLong(), anyLong());
        doNothing().when(attachmentService).deleteById(anyLong());
        doNothing().when(postAttachmentService).deletePostById(1L);
        doNothing().when(postRepository).deleteById(1L);

        // Act
        postService.deleteById(1L);

        // Assert
        verify(postAttachmentService, times(1)).findAttachmentsByIdOfPost(1L);
        verify(attachmentService, times(2)).deleteById(anyLong());
        verify(postAttachmentService, times(2)).deleteAttachmentById(anyLong(), eq(1L));
        verify(postAttachmentService, times(1)).deletePostById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void save_ShouldSavePostAndAttachments() {
        // Arrange
        Post savedPost = new Post(100L, "Test post content");
        savedPost.setPostId(1L);

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(attachmentService.save(any(Attachment.class))).thenReturn(attachment);
        when(postAttachmentService.save(any(PostAttchment.class))).thenReturn(new PostAttchment());

        // Act
        PostDTO result = postService.save(postDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(attachmentService, times(1)).save(any(Attachment.class));
        verify(postAttachmentService, times(1)).save(any(PostAttchment.class));
    }

    @Test
    void findByUserId_ShouldReturnPosts() {
        // Arrange
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findByUserId(100L)).thenReturn(posts);

        // Act
        List<Post> result = postService.findByUserId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByUserId(100L);
    }

    @Test
    void findByContent_ShouldReturnPosts() {
        // Arrange
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findByContent("test")).thenReturn(posts);

        // Act
        List<Post> result = postService.findByContent("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByContent("test");
    }

    @Test
    void updatePost_UpdateContent_ShouldUpdateContent() {
        // Arrange
        PostDTO existingPostDTO = new PostDTO(1L, "Old content", 100L, new ArrayList<>(), new Timestamp(System.currentTimeMillis()));
        PostDTO updateDTO = new PostDTO();
        updateDTO.setContent("New content");

        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(new ArrayList<>());
        when(attachmentService.findById(anyLong())).thenReturn(Optional.empty());
        doNothing().when(postRepository).updateContent(1L, "New content");

        // Act
        PostDTO result = postService.updatePost(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New content", result.getContent());
        verify(postRepository, times(1)).updateContent(1L, "New content");
    }

    @Test
    void updatePost_UpdateAttachments_ShouldUpdateAttachments() {
        // Arrange
        List<Attachment> attachments = Arrays.asList(attachment);
        PostDTO existingPostDTO = new PostDTO(1L, "Content", 100L, new ArrayList<>(),new Timestamp(System.currentTimeMillis()));
        PostDTO updateDTO = new PostDTO();
        updateDTO.setAttachments(attachments);

        attachment.setAttachId(null); // New attachment

        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(new ArrayList<>());
        when(attachmentService.findById(anyLong())).thenReturn(Optional.empty());
        when(attachmentService.save(any(Attachment.class))).thenReturn(attachment);
        when(postAttachmentService.save(any(PostAttchment.class))).thenReturn(new PostAttchment());

        // Act
        PostDTO result = postService.updatePost(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAttachments());
        verify(attachmentService, times(1)).save(any(Attachment.class));
        verify(postAttachmentService, times(1)).save(any(PostAttchment.class));
    }

    @Test
    void updatePost_WhenPostDoesNotExist_ShouldReturnNull() {
        // Arrange
        PostDTO updateDTO = new PostDTO();
        updateDTO.setContent("New content");

        when(postAttachmentService.findAttachmentsByIdOfPost(1L)).thenReturn(new ArrayList<>());
        when(attachmentService.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        PostDTO result = postService.updatePost(1L, updateDTO);

        // Assert
        assertNull(result);
        verify(postRepository, never()).updateContent(anyLong(), anyString());
    }
}