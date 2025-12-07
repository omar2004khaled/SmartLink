package com.example.auth.service.PostAttachmentService;

import com.example.auth.entity.PostAttchment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostAttachmentService {
    List<PostAttchment> findAll();
    List<Long> findPostsByIdOfAttachment(Long AttachID);
    List<Long> findAttachmentsByIdOfPost(Long PostId);

    void deletePostById(Long PostId);
    void deleteAttachmentById(Long AttachmentID, Long PostId);
    PostAttchment save(PostAttchment postAttchment);
    void updatePostById(Long PostId, Long AttachmentID);
    void updateAttachmentById(Long AttachmentID, Long PostId);
}
