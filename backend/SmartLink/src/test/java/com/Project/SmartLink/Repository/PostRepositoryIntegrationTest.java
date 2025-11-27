package com.Project.SmartLink.Repository;

import com.Project.SmartLink.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void testSavePost() {
        Post p = new Post(1L, "Hello");
        Post saved = postRepository.save(p);

        assertNotNull(saved.getPostId());
    }

    @Test
    void testFindById() {
        Post p = new Post(1L, "Test post");
        Post saved = postRepository.save(p);

        assertTrue(postRepository.findById(saved.getPostId()).isPresent());
    }
}
