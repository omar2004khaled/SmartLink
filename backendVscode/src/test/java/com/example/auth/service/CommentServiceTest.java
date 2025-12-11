package com.example.auth.service;

import com.example.auth.dto.CommentDTO;
import com.example.auth.entity.Attachment;
import com.example.auth.entity.Comment;
import com.example.auth.entity.Post;
import com.example.auth.entity.User;
import com.example.auth.enums.TypeofAttachments;
import com.example.auth.exceptions.NonExistentObject;
import com.example.auth.repository.CommentRepo;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepo commentRepo;
    @InjectMocks
    CommentService commentService;
    @Captor
    ArgumentCaptor<Comment> commentArgumentCaptor;
    private CommentDTO commentDTO;
    @BeforeEach
    void setUp(){
        commentDTO =new CommentDTO();
        commentDTO.setType("Files");
        commentDTO.setCommentId(Integer.toUnsignedLong(1));
        commentDTO.setUrl("http//mock");
        commentDTO.setText("hello fromSmartLink");
        commentDTO.setUserId(Integer.toUnsignedLong(1));
    }
    @Test
    public void removeComment_NonExistentObject(){
        Mockito.when(commentRepo.findById(commentDTO.getUserId())).thenReturn(Optional.empty());
        Assertions.assertThrows(NonExistentObject.class,()->{
            commentService.RemoveComment(commentDTO.getUserId());
        });
    }
    @Test
    public void removeComment_Success() {
        Comment existingComment = new Comment();
        existingComment.setCommentId(commentDTO.getCommentId());

        Mockito.when(commentRepo.findById(commentDTO.getCommentId()))
                .thenReturn(Optional.of(existingComment));

        commentService.RemoveComment(commentDTO.getCommentId());
        Mockito.verify(commentRepo).deleteById(commentDTO.getCommentId());
    }
    @Test
    public void addComment_Success(){
        User mockUser = new User();
        mockUser.setId(commentDTO.getUserId());

        Post mockPost = new Post();
        mockPost.setPostId(commentDTO.getPostId());

        Comment savedComment = new Comment();
        savedComment.setCommentId(1L);
        savedComment.setContent(commentDTO.getText());

        Mockito.when(userRepository.findById(commentDTO.getUserId())).thenReturn(Optional.of(mockUser));
        Mockito.when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.of(mockPost));
        Mockito.when(commentRepo.save(any())).thenReturn(savedComment);
        commentService.addComment(commentDTO);
        Mockito.verify(userRepository).findById(commentDTO.getUserId());
        Mockito.verify(postRepository).findById(commentDTO.getPostId());

    }
    @Test
    public void addCommentText_Success(){
        commentDTO.setType(null);
        commentDTO.setUrl(null);
        User mockUser = new User();
        mockUser.setId(commentDTO.getUserId());

        Post mockPost = new Post();
        mockPost.setPostId(commentDTO.getPostId());

        Comment savedComment = new Comment();
        savedComment.setCommentId(1L);
        savedComment.setContent(commentDTO.getText());

        Mockito.when(userRepository.findById(commentDTO.getUserId())).thenReturn(Optional.of(mockUser));
        Mockito.when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.of(mockPost));
        Mockito.when(commentRepo.save(any())).thenReturn(savedComment);
        commentService.addComment(commentDTO);
        Mockito.verify(userRepository).findById(commentDTO.getUserId());
        Mockito.verify(postRepository).findById(commentDTO.getPostId());

    }
    @Test
    public void Update_NotFound(){
        Mockito.when(commentRepo.findById(commentDTO.getCommentId())).thenReturn(Optional.empty());
        Assertions.assertThrows(NonExistentObject.class,()->{
            commentService.update(commentDTO);
        });
    }

    @Test
    public void UpdateText_Success(){
        commentDTO.setUrl(null);
        commentDTO.setType(null);
        Comment comment = Comment.builder()
                .commentId(commentDTO.getCommentId())
                .content(commentDTO.getText())
                .build();
        Mockito.when(commentRepo.findById(commentDTO.getCommentId()))
                .thenReturn(Optional.of(comment));
        Mockito.when(commentRepo.save(comment)).thenReturn(comment);
        commentService.update(commentDTO);
        Mockito.verify(commentRepo).findById(commentDTO.getCommentId());
        Mockito.verify(commentRepo).save(comment);
    }

    @Test
    public void UpdateAttach_Success(){
        commentDTO.setText(null);
        Comment comment = Comment.builder()
                .commentId(commentDTO.getCommentId())
                .attachment(Attachment.builder()
                        .typeofAttachments(TypeofAttachments.valueOf(commentDTO.getType()))
                        .AttachmentURL(commentDTO.getUrl())
                        .build())
                .build();
        Mockito.when(commentRepo.findById(commentDTO.getCommentId()))
                .thenReturn(Optional.of(comment));
        Mockito.when(commentRepo.save(comment)).thenReturn(comment);
        commentService.update(commentDTO);
        Mockito.verify(commentRepo).findById(commentDTO.getCommentId());
        Mockito.verify(commentRepo).save(comment);
    }
    @Test
    public void UpdateTextAttach_Success(){
        Comment comment = Comment.builder()
                .commentId(commentDTO.getCommentId())
                .content(commentDTO.getText())
                .attachment(Attachment.builder()
                        .typeofAttachments(TypeofAttachments.valueOf(commentDTO.getType()))
                        .AttachmentURL(commentDTO.getUrl())
                        .build())
                .build();
        Mockito.when(commentRepo.findById(commentDTO.getCommentId()))
                .thenReturn(Optional.of(comment));
        Mockito.when(commentRepo.save(comment)).thenReturn(comment);
        commentService.update(commentDTO);
        Mockito.verify(commentRepo).findById(commentDTO.getCommentId());
        Mockito.verify(commentRepo).save(comment);
    }


}
