package com.Project.SmartLink.Repository;

import com.Project.SmartLink.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostAttachmentRepositoryIntegrationTest {

    @Autowired
    private PostAttachmentRepository postAttachmentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void testInsertPostAttachment() {
        Post post = new Post(1L, "Post content");
        post = postRepository.save(post);

        Attachment attachment = new Attachment();
        attachment.setAttachmentURL("img.png");
        attachment.setTypeOfAttachment(TypeOfAttachment.Image);
        attachment = attachmentRepository.save(attachment);

        PostAttachmentKey key = new PostAttachmentKey(post.getPostId(), attachment.getAttachId());
        PostAttchment pa = new PostAttchment(key);

        PostAttchment saved = postAttachmentRepository.save(pa);

        assertNotNull(saved);
        assertEquals(post.getPostId(), saved.getId().getPostId());
    }
}
