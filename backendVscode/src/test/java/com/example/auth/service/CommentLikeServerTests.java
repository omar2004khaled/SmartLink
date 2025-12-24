package com.example.auth.service;

import com.example.auth.entity.Comment;
import com.example.auth.entity.CommentsLike;
import com.example.auth.entity.CommentsLikeId;
import com.example.auth.entity.User;
import com.example.auth.exceptions.NotEnoughInformationException;
import com.example.auth.repository.CommentRepo;
import com.example.auth.repository.LikeCommentRepo;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@AutoConfigureMockMvc
@SpringBootTest
public class CommentLikeServerTests {

    @Mock
    LikeCommentRepo likeCommentRepo;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepo commentRepo;

    @InjectMocks
    LikeService likeService;

    @Captor
    ArgumentCaptor<CommentsLike> commentsLikeArgumentCaptor;

    @Captor
    ArgumentCaptor<CommentsLikeId> commentsLikeIdArgumentCaptor;

    private Long userId;
    private Long commentId;
    private User mockUser;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        userId = 1L;
        commentId = 4L;

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFullName("Test User");
        mockUser.setEmail("test@example.com");

        mockComment = new Comment();
        mockComment.setCommentId(commentId);
        mockComment.setContent("Test comment");
    }

    @Test
    public void makeOrDeleteLike_CreateLike_Success() {

        CommentsLikeId commentsLikeId = new CommentsLikeId(userId, commentId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.of(mockComment));
        Mockito.when(likeCommentRepo.existsById(commentsLikeId)).thenReturn(false);
        try {
            likeService.makeOrDeleteLike(userId, commentId);
        } catch (NotEnoughInformationException e) {
            throw new RuntimeException(e);
        }


        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(commentRepo).findById(commentId);
        Mockito.verify(likeCommentRepo).existsById(commentsLikeId);
        Mockito.verify(likeCommentRepo).save(any());
    }

    @Test
    public void makeOrDeleteLike_DeleteLike_Success() {
        CommentsLikeId commentsLikeId = new CommentsLikeId(userId, commentId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.of(mockComment));
        Mockito.when(likeCommentRepo.existsById(commentsLikeId)).thenReturn(true);


        try {
            likeService.makeOrDeleteLike(userId, commentId);
        } catch (NotEnoughInformationException e) {
            throw new RuntimeException(e);
        }

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(commentRepo).findById(commentId);
        Mockito.verify(likeCommentRepo).existsById(commentsLikeId);
        Mockito.verify(likeCommentRepo).deleteById(commentsLikeId);
    }

    @Test
    public void makeOrDeleteLike_UserNotFound() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotEnoughInformationException.class, () -> {
            likeService.makeOrDeleteLike(userId, commentId);
        });

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(commentRepo, Mockito.never()).findById(any());
    }

    @Test
    public void makeOrDeleteLike_CommentNotFound() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotEnoughInformationException.class, () -> {
            likeService.makeOrDeleteLike(userId, commentId);
        });

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(commentRepo).findById(commentId);

    }

}