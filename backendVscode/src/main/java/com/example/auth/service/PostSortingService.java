package com.example.auth.service;

import com.example.auth.dto.PostDTO;
import com.example.auth.entity.Post;
import com.example.auth.repository.ReactionRepository;
import com.example.auth.repository.ConnectionRepository;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.PostService.PostService;
import com.example.auth.utility.Constants;
import com.example.auth.utility.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PostSortingService {
    private RedisService redisService;
    private PostService postService;
    private ReactionRepository reactionRepository;
    private ConnectionRepository connectionRepository;
    private CompanyFollowerRepo companyFollowerRepo;
    private UserRepository userRepository;

    private static final int MAX_FEED_LRU_KEYS = 500;
    private static final long FEED_TTL_SECONDS = 60 * 1000;

    @Autowired
    public PostSortingService(RedisService redisService, PostService postService,
                              ReactionRepository reactionRepository,
                              ConnectionRepository connectionRepository,
                              CompanyFollowerRepo companyFollowerRepo,
                              UserRepository userRepository) {
        this.redisService = redisService;
        this.postService = postService;
        this.reactionRepository = reactionRepository;
        this.connectionRepository = connectionRepository;
        this.companyFollowerRepo = companyFollowerRepo;
        this.userRepository = userRepository;
    }

    public void execute(int page, Long userId) {
        try {
            int fetchSize = Constants.PAGE_SIZE_FOR_REDIS;
            List<PostDTO> posts = postService.findAll(PageRequest.of(page, fetchSize));
            if (posts == null || posts.isEmpty()) return;

            final String feedKey = RedisKeys.FEED + userId;

            for (PostDTO postDTO : posts) {
                Long postId = postDTO.getId();
                Long authorId = postDTO.getUserId();

                double score = computeScore(postDTO, userId, authorId, postId);
                redisService.addToSortedSet(feedKey, String.valueOf(postId), score);
            }

            String pageMarker = RedisKeys.PAGE + (page + 1);
            redisService.addToSortedSet(feedKey, pageMarker, -Double.MAX_VALUE / 2);

            redisService.maintainFeedKeyLRU(feedKey, MAX_FEED_LRU_KEYS, FEED_TTL_SECONDS);

        } catch (Exception e) {
            System.err.println("Error in PostSortingService.execute: " + e.getMessage());
        }
    }

    private double computeScore(PostDTO postDTO, Long viewerId, Long authorId, Long postId) {
        double score = 0.0;
        try {
            Instant created = postDTO.getCreatedAt() != null
                    ? postDTO.getCreatedAt().toInstant()
                    : Instant.now();
            long hoursAgo = Math.max(0, Duration.between(created, Instant.now()).toHours());
            double recencyWeight = Math.max(0, Constants.HOURS_IN_THREE_DAYS - hoursAgo) / Double.valueOf(Constants.HOURS_IN_THREE_DAYS);
            score += recencyWeight * 30.0;
        } catch (Exception ex) {
        }

        try {
            Integer likes = reactionRepository.findByPostId(postId).size();
            if (likes != null) score += likes * 2.0;
        } catch (Exception ex) {
        }

        try {
            Optional<?> connection = connectionRepository.findConnectionBetweenUsers(viewerId, authorId);
            if (connection.isPresent()) {
                score += Constants.CONNECTION_SCORE;
            }
        } catch (Exception ex) {
        }
        try {
            Optional<?> followerUserOpt = userRepository.findById(viewerId);
            Optional<?> compUserOpt = userRepository.findById(authorId);
            if (followerUserOpt.isPresent() && compUserOpt.isPresent()) {
                boolean follows = companyFollowerRepo.existsByFollowerAndCompany(
                        (com.example.auth.entity.User) followerUserOpt.get(),
                        (com.example.auth.entity.User) compUserOpt.get()
                );
                if (follows) score += Constants.FOLLOW_SCORE;
            }
        } catch (Exception ex) {
        }

        return score;
    }
}
