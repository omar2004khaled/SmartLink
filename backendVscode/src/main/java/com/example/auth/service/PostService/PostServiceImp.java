package com.example.auth.service.PostService;

import com.example.auth.dto.PostDTO;
import com.example.auth.entity.*;
import com.example.auth.repository.*;
import com.example.auth.service.AttachmentService.*;
import com.example.auth.service.PostAttachmentService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImp implements PostService {
    private PostRepository postRepository;
    private AttachmentService attachmentService;
    private PostAttachmentService postAttachmentService;
    private CommentRepo commentRepository;
    private UserRepository userRepository;

    @Autowired
    public PostServiceImp(PostRepository postRepository, AttachmentService attachmentService,
            PostAttachmentService postAttachmentService, CommentRepo commentRepo, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
        this.postAttachmentService = postAttachmentService;
        this.commentRepository = commentRepo;
        this.userRepository = userRepository;
    }

    @Override
    public List<PostDTO> findAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        if (posts.getContent().size() == 0)
            return null;
        List<PostDTO> answers = new ArrayList<>();
        for (Post post : posts.getContent()) {
            List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.getPostId());
            List<Attachment> attachments = new ArrayList<>();
            for (Long attachmentID : attachmentIDs) {
                Optional<Attachment> attachment = attachmentService.findById(attachmentID);
                if (attachment.isPresent())
                    attachments.add(attachment.get());
            }
            String userType = userRepository.findById(post.getUserId())
                    .map(user -> user.getUserType() != null ? user.getUserType().toString() : "JOB_SEEKER")
                    .orElse("JOB_SEEKER");
            PostDTO answer = new PostDTO(post.getPostId(), post.getContent(), post.getUserId(), userType, attachments,
                    post.getCreatedAt());

            answers.add(answer);
        }
        return answers;
    }

    @Override
    public PostDTO findById(Long theId) {
        Optional<Post> post = postRepository.findById(theId);
        if (!post.isPresent())
            return null;
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.get().getPostId());
        List<Attachment> attachments = new ArrayList<>();
        for (Long attachmentID : attachmentIDs) {
            Optional<Attachment> attachment = attachmentService.findById(attachmentID);
            if (attachment.isPresent())
                attachments.add(attachment.get());
        }
        String userType = userRepository.findById(post.get().getUserId())
                .map(user -> user.getUserType() != null ? user.getUserType().toString() : "JOB_SEEKER")
                .orElse("JOB_SEEKER");
        PostDTO answer = new PostDTO(post.get().getPostId(), post.get().getContent(), post.get().getUserId(), userType,
                attachments, post.get().getCreatedAt());
        return answer;
    }

    @Override
    public void deleteById(Long theId) {
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(theId);

        while (true) {
            Page<Comment> comments = commentRepository.findByPostIdNative(theId, Pageable.ofSize(10));
            if (comments.isEmpty())
                break;
            for (Comment comment : comments) {
                commentRepository.delete(comment);
            }
        }
        for (Long attachmentID : attachmentIDs) {
            attachmentService.deleteById(attachmentID);
            postAttachmentService.deleteAttachmentById(attachmentID, theId);
        }
        postAttachmentService.deletePostById(theId);
        postRepository.deleteById(theId);
    }

    @Override
    public PostDTO save(PostDTO postDTO) {
        Post post = new Post(postDTO.getUserId(), postDTO.getContent());
        List<Attachment> attachments = postDTO.getAttachments();
        List<Attachment> savedAttachments = new ArrayList<>();
        post = postRepository.save(post);
        for (Attachment attachment : attachments) {
            Attachment saved = attachmentService.save(attachment);
            savedAttachments.add(saved);
            PostAttchment postAttchment = new PostAttchment(
                    new PostAttachmentKey(post.getPostId(), saved.getAttachId().longValue()));
            postAttachmentService.save(postAttchment);
        }
        String userType = userRepository.findById(post.getUserId())
                .map(user -> user.getUserType() != null ? user.getUserType().toString() : "JOB_SEEKER")
                .orElse("JOB_SEEKER");
        PostDTO answer = new PostDTO(post.getPostId(), post.getContent(), post.getUserId(), userType, savedAttachments,
                post.getCreatedAt());
        return answer;
    }

    @Override
    public List<Post> findByUserId(Long theId) {

        return postRepository.findByUserId(theId);
    }

    @Override
    public List<Post> findByContent(String theContent) {
        return postRepository.findByContent(theContent);
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO) {
        PostDTO answer = new PostDTO();
        PostDTO savedPostDTO = findById(id);
        if (savedPostDTO == null)
            return null;
        if (postDTO.getContent() != null) { /// update the content of the post
            savedPostDTO.setContent(postDTO.getContent());
            postRepository.updateContent(id, postDTO.getContent());
            answer = savedPostDTO;
        } else if (postDTO.getAttachments() != null) { /// update the attachments of the post
            List<Attachment> attachments = postDTO.getAttachments();
            List<Attachment> savedAttachments = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if (attachment.getAttachId() != null) { // edit exisiting attachment
                    attachmentService.updateAttachmentById(attachment);
                    savedAttachments.add(attachment);
                } else { // no id is added so it is a new attachment
                    Attachment saved = attachmentService.save(attachment);
                    savedAttachments.add(saved);
                    PostAttchment postAttchment = new PostAttchment(
                            new PostAttachmentKey(id, saved.getAttachId().longValue()));
                    postAttachmentService.save(postAttchment);
                }
            }
            String userType = savedPostDTO.getUserType(); // Preserve existing userType
            answer = new PostDTO(savedPostDTO.getId(), savedPostDTO.getContent(), savedPostDTO.getUserId(), userType,
                    savedAttachments, savedPostDTO.getCreatedAt());
        }
        return answer;
    }
}
