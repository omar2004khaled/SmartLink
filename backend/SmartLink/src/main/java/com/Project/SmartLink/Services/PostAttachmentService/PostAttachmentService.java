package com.Project.SmartLink.Services.PostAttachmentService;


import com.Project.SmartLink.entity.PostAttchment;
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
