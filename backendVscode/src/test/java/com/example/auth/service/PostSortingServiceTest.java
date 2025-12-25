package com.example.auth.service;

import com.example.auth.dto.PostDTO;
import com.example.auth.entity.Reaction;
import com.example.auth.entity.User;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.ConnectionRepository;
import com.example.auth.repository.ReactionRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.PostService.PostService;
import com.example.auth.utility.Constants;
import com.example.auth.utility.RedisKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSortingServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private PostService postService;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private CompanyFollowerRepo companyFollowerRepo;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostSortingService postSortingService;

    private Long userId;
    private Long authorId;
    private Long postId;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        authorId = 2L;
        postId = 100L;

        postDTO = new PostDTO();
        postDTO.setId(postId);
        postDTO.setUserId(authorId);
        postDTO.setCreatedAt(Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)));
    }

    @Test
    void testExecuteWithValidPosts() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        postSortingService.execute(0, userId);

        verify(postService).findAll(PageRequest.of(0, Constants.PAGE_SIZE_FOR_REDIS));
        verify(redisService, atLeastOnce()).addToSortedSet(eq(RedisKeys.FEED + userId), anyString(), anyDouble());
        verify(redisService).maintainFeedKeyLRU(eq(RedisKeys.FEED + userId), eq(500), eq(60000L));
    }

    @Test
    void testExecuteWithNullPosts() {
        when(postService.findAll(any(PageRequest.class))).thenReturn(null);

        postSortingService.execute(0, userId);

        verify(postService).findAll(PageRequest.of(0, Constants.PAGE_SIZE_FOR_REDIS));
        verify(redisService, never()).addToSortedSet(anyString(), anyString(), anyDouble());
    }

    @Test
    void testExecuteWithEmptyPosts() {
        when(postService.findAll(any(PageRequest.class))).thenReturn(new ArrayList<>());

        postSortingService.execute(0, userId);

        verify(postService).findAll(PageRequest.of(0, Constants.PAGE_SIZE_FOR_REDIS));
        verify(redisService, never()).addToSortedSet(anyString(), anyString(), anyDouble());
    }

    @Test
    void testExecuteWithException() {
        when(postService.findAll(any(PageRequest.class))).thenThrow(new RuntimeException("Test exception"));

        postSortingService.execute(0, userId);

        verify(postService).findAll(PageRequest.of(0, Constants.PAGE_SIZE_FOR_REDIS));
    }

    @Test
    void testComputeScoreWithRecentPost() {
        List<PostDTO> posts = new ArrayList<>();
        postDTO.setCreatedAt(Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        postSortingService.execute(0, userId);

        verify(redisService, atLeastOnce()).addToSortedSet(anyString(), anyString(), doubleThat(score -> score > 0));
    }

    @Test
    void testComputeScoreWithLikes() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        List<Reaction> reactions = new ArrayList<>();
        reactions.add(new Reaction());
        reactions.add(new Reaction());
        reactions.add(new Reaction());

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(reactions);
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        postSortingService.execute(0, userId);

        verify(reactionRepository).findByPostId(postId);
    }
    @Test
    void testComputeScoreWithCompanyFollow() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        User viewer = new User();
        User author = new User();

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(viewer));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(companyFollowerRepo.existsByFollowerAndCompany(viewer, author)).thenReturn(true);

        postSortingService.execute(0, userId);

        verify(companyFollowerRepo).existsByFollowerAndCompany(viewer, author);
    }

    @Test
    void testComputeScoreWithNullCreatedAt() {
        List<PostDTO> posts = new ArrayList<>();
        postDTO.setCreatedAt(null);
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        postSortingService.execute(0, userId);

        verify(redisService, atLeastOnce()).addToSortedSet(anyString(), anyString(), anyDouble());
    }

    @Test
    void testComputeScoreWithReactionRepositoryException() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenThrow(new RuntimeException("DB error"));
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        postSortingService.execute(0, userId);

        verify(redisService, atLeastOnce()).addToSortedSet(anyString(), anyString(), anyDouble());
    }

    @Test
    void testExecuteAddsPageMarker() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(companyFollowerRepo.existsByFollowerAndCompany(any(User.class), any(User.class))).thenReturn(false);

        int page = 5;
        postSortingService.execute(page, userId);

        verify(redisService).addToSortedSet(
                eq(RedisKeys.FEED + userId),
                eq(RedisKeys.PAGE + (page + 1)),
                eq(-Double.MAX_VALUE / 2)
        );
    }

    @Test
    void testComputeScoreWithUserNotFound() {
        List<PostDTO> posts = new ArrayList<>();
        posts.add(postDTO);

        when(postService.findAll(any(PageRequest.class))).thenReturn(posts);
        when(reactionRepository.findByPostId(postId)).thenReturn(new ArrayList<>());
        when(connectionRepository.findConnectionBetweenUsers(userId, authorId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(authorId)).thenReturn(Optional.of(new User()));

        postSortingService.execute(0, userId);

        verify(companyFollowerRepo, never()).existsByFollowerAndCompany(any(User.class), any(User.class));
    }
}