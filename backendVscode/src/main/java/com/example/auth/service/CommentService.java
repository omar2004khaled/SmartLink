package com.example.auth.service;

import com.example.auth.dto.CommentDTO;
import com.example.auth.entity.Attachment;
import com.example.auth.entity.Comment;
import com.example.auth.entity.Post;
import com.example.auth.entity.User;
import com.example.auth.enums.TypeofAttachments;
import com.example.auth.exceptions.NonExistentObject;
import com.example.auth.repository.AttacchmentsRepository;
import com.example.auth.repository.CommentRepo;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AttacchmentsRepository attachmentsRepository;
    public boolean RemoveComment(Long ID){
        Optional<Comment> c=commentRepo.findById(ID);
        if(c.isEmpty()) throw new NonExistentObject("Object Does not exist");
        commentRepo.deleteById(ID);
        return true;
    }
    @Transactional
    public Long addComment(CommentDTO commentDTO) {
        System.out.println("Adding comment for userId: " + commentDTO.getUserId() +
                ", postId: " + commentDTO.getPostId());

        // Fetch entities with proper error handling
        User user = userRepository.findById(commentDTO.getUserId()).get();
        Post post = postRepository.findById((commentDTO.getPostId());


        Attachment attachment =null;
        System.out.println(commentDTO.getUrl()+" "+commentDTO.getType());
        if(commentDTO.getUrl() != null) {

            System.out.println(commentDTO.getType().toUpperCase());
            attachment = Attachment.builder()
                    .AttachmentURL(commentDTO.getUrl())
                    .typeofAttachments(commentDTO.getType() != null ? TypeofAttachments.valueOf(commentDTO.getType()) : null)
                    .build();
            System.out.println("saving Attachments");

        }
        // Build comment
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .attachment(attachment)
                .content(commentDTO.getText())
                .build();

        // Save comment
        Comment savedComment = commentRepo.save(comment);

        return savedComment.getCommentId();
    }
    @Transactional
    public List<CommentDTO> getCommentsByPostId(int pageNo,int pageSize,Long postId){
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        List<Comment> tmpComments=commentRepo.findByPostIdNative(postId,pageable).getContent();
        return tmpComments.stream().map(this::ConvertToDTO).toList();
    }
    private CommentDTO ConvertToDTO(Comment comment){
        CommentDTO c= CommentDTO.builder()
                .text(comment.getContent())
                .build();
        if(comment.getAttachment() != null) {
            c.setUrl(comment.getAttachment().getAttachmentURL());
            if(comment.getAttachment().getTypeofAttachments()!=null)
                c.setType(comment.getAttachment().getTypeofAttachments().toString());
        }
        return c;
    }
    @Transactional
    public CommentDTO update(CommentDTO commentDTO){
        Optional<Comment> c=commentRepo.findById(commentDTO.getCommentId());
        if(c.isEmpty()) throw new NonExistentObject("Object Does Not exist");
        Comment comment=c.get();
        comment.setAttachment(Attachment.builder()
                .AttachmentURL(commentDTO.getUrl())
                .typeofAttachments(commentDTO.getType()!=null?TypeofAttachments.valueOf(commentDTO.getType()):null)
                .build());
        comment.setContent(commentDTO.getText());
        comment.setCreatedAt(LocalDateTime.now());
        return ConvertToDTO(commentRepo.save(comment));
    }
}
