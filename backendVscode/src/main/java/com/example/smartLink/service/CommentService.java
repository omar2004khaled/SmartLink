package com.example.smartLink.service;

import com.example.smartLink.dto.CommentDTO;
import com.example.smartLink.entity.Attachment;
import com.example.smartLink.entity.Comment;
import com.example.smartLink.enums.TypeofAttachments;
import com.example.smartLink.repository.AttacchmentsRepository;
import com.example.smartLink.repository.CommentRepo;
import com.example.smartLink.repository.PostRepository;
import com.example.smartLink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class CommentService {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AttacchmentsRepository attacchmentsRepository;
    public boolean RemoveComment(Long ID){
        commentRepo.deleteById(ID);
        return true;
    }
    public Long addComment(CommentDTO commentDTO)
    {
        try {
            Comment comment = Comment.builder()
                    .user(userRepository.getById(commentDTO.getUserId()))
                    .post(postRepository.findById(commentDTO.getPostId()))
                    .createdAt(LocalDateTime.now())
                    .attachment(Attachment.builder()
                            .AttachmentURL(commentDTO.getURL())
                            .typeofAttachments(TypeofAttachments.valueOf(commentDTO.getType()))
                    .build())
                    .content(commentDTO.getText())
                    .build();
            Comment c= commentRepo.save(comment);
            return c.getCommentId();
        }
        catch (Exception e){};
        return null;
    }

}
