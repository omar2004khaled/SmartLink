package com.Project.SmartLink.Services.PostAttachmentService;

import com.Project.SmartLink.Repository.PostAttachmentRepository;
import com.Project.SmartLink.entity.Attachment;
import com.Project.SmartLink.entity.Post;
import com.Project.SmartLink.entity.PostAttchment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostAttachmentServiceImp implements PostAttachmentService{
    private PostAttachmentRepository postAttachmentRepository;
    @Autowired
    public PostAttachmentServiceImp(PostAttachmentRepository postAttachmentRepository) {
        this.postAttachmentRepository = postAttachmentRepository;
    }

    @Override
    public List<PostAttchment> findAll() {
        return postAttachmentRepository.findAll();
    }

    @Override
    public List<Long> findPostsByIdOfAttachment(Long AttachID) {
        return postAttachmentRepository.findPostsByIdOfAttachment(AttachID);
    }

    @Override
    public List<Long> findAttachmentsByIdOfPost(Long PostId) {
        return postAttachmentRepository.findAttachmentsByIdOfPost(PostId);
    }

    @Override
    public void deletePostById(Long PostId) {
        postAttachmentRepository.deletePostById(PostId);

    }

    @Override
    public void deleteAttachmentById(Long AttachmentID, Long PostId) {
        postAttachmentRepository.deleteAttachmentById(AttachmentID, PostId);

    }

    @Override
    public PostAttchment save(PostAttchment postAttchment) {
        return postAttachmentRepository.save(postAttchment);
    }

    @Override
    public void updatePostById(Long PostId, Long AttachmentID) {
        postAttachmentRepository.updatePostById(PostId, AttachmentID);
    }

    @Override
    public void updateAttachmentById(Long AttachmentID, Long PostId) {
        postAttachmentRepository.updateAttachmentById(AttachmentID, PostId);

    }
}
