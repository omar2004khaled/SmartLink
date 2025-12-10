package com.example.auth.service.PostService;

import com.example.auth.dto.AttachmentDTO;
import com.example.auth.dto.PostDTO;
import com.example.auth.enums.TypeofAttachments;
import com.example.auth.repository.*;
import com.example.auth.service.AttachmentService.*;
import com.example.auth.service.PostAttachmentService.*;
import com.example.auth.entity.Attachment;
import com.example.auth.entity.Post;
import com.example.auth.entity.PostAttachmentKey;
import com.example.auth.entity.PostAttchment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImp implements PostService{
    private PostRepository postRepository;
    private AttachmentService attachmentService;
    private PostAttachmentService postAttachmentService;

    @Autowired
    public PostServiceImp(PostRepository postRepository , AttachmentService attachmentService ,PostAttachmentService postAttachmentService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
        this.postAttachmentService = postAttachmentService;
    }
    @Override
    public List<PostDTO> findAll(Pageable pageable) {
        Page<Post> posts =  postRepository.findAll(pageable);
        if (posts.getContent().size()==0) return null;
        List<PostDTO> answers = new ArrayList<>();
        for (Post post : posts.getContent()){
            List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.getPostId());
            List<AttachmentDTO> attachments = new ArrayList<>();
            for (Long attachmentID : attachmentIDs) {
                Optional<Attachment> attachment = attachmentService.findById(attachmentID);
                if (attachment.isPresent()) {
                    AttachmentDTO dto = new AttachmentDTO(
                        attachment.get().getAttachId(),
                        attachment.get().getTypeofAttachments().name(),
                        attachment.get().getAttachmentURL()
                    );
                    attachments.add(dto);
                }
            }
            PostDTO answer = new PostDTO(post.getPostId() ,  post.getContent(),post.getUserId(), attachments , post.getCreatedAt());
            answers.add(answer);
        }
        return answers;
    }
    @Override
    public PostDTO findById(Long theId) {
        Optional<Post> post = postRepository.findById(theId);
        if (!post.isPresent()) return null;
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.get().getPostId());
        List<AttachmentDTO> attachments = new ArrayList<>();
        for (Long attachmentID : attachmentIDs) {
            Optional<Attachment> attachment= attachmentService.findById(attachmentID);
            if (attachment.isPresent()) {
                AttachmentDTO dto = new AttachmentDTO(
                    attachment.get().getAttachId(),
                    attachment.get().getTypeofAttachments().name(),
                    attachment.get().getAttachmentURL()
                );
                attachments.add(dto);
            }
        }
        PostDTO answer = new PostDTO(post.get().getPostId(), post.get().getContent(), post.get().getUserId(), attachments , post.get().getCreatedAt());
        return answer;
    }

    @Override
    public void deleteById(Long theId) {
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(theId);
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
        List<AttachmentDTO> attachmentDTOs = postDTO.getAttachments();
        List<AttachmentDTO> savedAttachmentDTOs = new ArrayList<>();
        post = postRepository.save(post);
        
        if (attachmentDTOs != null) {
            for (AttachmentDTO attachmentDTO : attachmentDTOs) {
                Attachment attachment = new Attachment();
                attachment.setAttachmentURL(attachmentDTO.getAttachmentURL());
                attachment.setTypeofAttachments(TypeofAttachments.valueOf(attachmentDTO.getTypeOfAttachment()));
                
                Attachment saved = attachmentService.save(attachment);
                AttachmentDTO savedDTO = new AttachmentDTO(
                    saved.getAttachId(),
                    saved.getTypeofAttachments().name(),
                    saved.getAttachmentURL()
                );
                savedAttachmentDTOs.add(savedDTO);
                
                PostAttchment postAttchment = new PostAttchment(new PostAttachmentKey(post.getPostId(), saved.getAttachId()));
                postAttachmentService.save(postAttchment);
            }
        }
        
        PostDTO answer = new PostDTO(post.getPostId(), post.getContent(), post.getUserId(), savedAttachmentDTOs, post.getCreatedAt());
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
        PostDTO savedPostDTO= findById(id);
        if (savedPostDTO== null) return null;
        if (postDTO.getContent()!=null) {                    ///  update the content of the post
            savedPostDTO.setContent(postDTO.getContent());
            postRepository.updateContent(id, postDTO.getContent());
            answer = savedPostDTO;
        }
        else if (postDTO.getAttachments()!=null){     ///  update the attachments of the post
            List<AttachmentDTO> attachmentDTOs = postDTO.getAttachments();
            List<AttachmentDTO> savedAttachmentDTOs = new ArrayList<>();
            for (AttachmentDTO attachmentDTO : attachmentDTOs) {
                if (attachmentDTO.getAttachId() != null) { // edit existing attachment
                    Attachment attachment = new Attachment();
                    attachment.setAttachId(attachmentDTO.getAttachId());
                    attachment.setAttachmentURL(attachmentDTO.getAttachmentURL());
                    attachment.setTypeofAttachments(TypeofAttachments.valueOf(attachmentDTO.getTypeOfAttachment()));
                    attachmentService.updateAttachmentById(attachment);
                    savedAttachmentDTOs.add(attachmentDTO);
                }else { // no id is added so it is a new attachment
                    Attachment attachment = new Attachment();
                    attachment.setAttachmentURL(attachmentDTO.getAttachmentURL());
                    attachment.setTypeofAttachments(TypeofAttachments.valueOf(attachmentDTO.getTypeOfAttachment()));
                    Attachment saved = attachmentService.save(attachment);
                    AttachmentDTO savedDTO = new AttachmentDTO(
                        saved.getAttachId(),
                        saved.getTypeofAttachments().name(),
                        saved.getAttachmentURL()
                    );
                    savedAttachmentDTOs.add(savedDTO);
                    PostAttchment postAttchment = new PostAttchment(new PostAttachmentKey(id, saved.getAttachId()));
                    postAttachmentService.save(postAttchment);
                }
            }
            answer = new PostDTO(savedPostDTO.getId(), savedPostDTO.getContent(),savedPostDTO.getUserId(),savedAttachmentDTOs, savedPostDTO.getCreatedAt());
        }
        return answer;
    }
}
