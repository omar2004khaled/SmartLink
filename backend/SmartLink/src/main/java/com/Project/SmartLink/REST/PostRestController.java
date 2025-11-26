package com.Project.SmartLink.REST;

import com.Project.SmartLink.DTO.PostDTO;
import com.Project.SmartLink.Services.AttachmentService.AttachmentService;
import com.Project.SmartLink.Services.PostAttachmentService.PostAttachmentService;
import com.Project.SmartLink.Services.PostService.PostService;
import com.Project.SmartLink.entity.Post;
import com.Project.SmartLink.entity.Attachment;
import com.Project.SmartLink.entity.PostAttachmentKey;
import com.Project.SmartLink.entity.PostAttchment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Post")
public class PostRestController {
    private PostService postService;
    private AttachmentService attachmentService;
    private PostAttachmentService postAttachmentService;
    @Autowired
    public PostRestController(PostService postService , AttachmentService attachmentService , PostAttachmentService postAttachmentService) {
        this.postService = postService;
        this.attachmentService = attachmentService;
        this.postAttachmentService = postAttachmentService;
    }
    @GetMapping("/all")
    public List<PostDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "PostId") String sortBy,
                                 @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postService.findAll(pageable);
        if (posts.getContent().size()==0) return null;
        List<PostDTO> answers = new ArrayList<>();
        for (Post post : posts.getContent()){
            List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.getPostId());
            List<Attachment> attachments = new ArrayList<>();
            for (Long attachmentID : attachmentIDs) {
                Optional<Attachment> attachment = attachmentService.findById(attachmentID);
                if (attachment.isPresent()) attachments.add(attachment.get());
            }
            PostDTO answer = new PostDTO(post.getPostId() ,  post.getContent(),post.getUserId(), attachments , post.getCreatedAt());
            answers.add(answer);
        }
        return answers;
    }
    @GetMapping("/{id}")
    public PostDTO findById(@PathVariable Long id) {
        Optional<Post> post = postService.findById(id);
        if (!post.isPresent()) return null;
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(post.get().getPostId());
        List<Attachment> attachments = new ArrayList<>();
        for (Long attachmentID : attachmentIDs) {
            Optional<Attachment> attachment= attachmentService.findById(attachmentID);
            if (attachment.isPresent()) attachments.add(attachment.get());
        }
        PostDTO answer = new PostDTO(post.get().getPostId(), post.get().getContent(), post.get().getUserId(), attachments , post.get().getCreatedAt());
        return answer;
    }
    @PostMapping("/add")
    public PostDTO addPost(@RequestBody PostDTO postDTO) {
        Post post = new Post(postDTO.getUserId(), postDTO.getContent());
        List<Attachment> attachments = postDTO.getAttachments();
        List<Attachment> savedAttachments = new ArrayList<>();
        post = postService.save(post);
        for (Attachment attachment : attachments) {
            Attachment saved = attachmentService.save(attachment);
            savedAttachments.add(saved);
            PostAttchment postAttchment = new PostAttchment(new PostAttachmentKey(post.getPostId(), saved.getAttachId()));
            postAttachmentService.save(postAttchment);
        }
        PostDTO answer = new PostDTO(post.getPostId(), post.getContent(), post.getUserId(),savedAttachments , post.getCreatedAt());
        return answer;
    }
    @PutMapping("/update/{id}")
    public PostDTO updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        if (postDTO==null) return null;
        PostDTO answer = new PostDTO();
        Optional<Post> post = postService.findById(id);
        if (!post.isPresent()) return null;
        if (postDTO.getContent()!=null) {                    ///  update the content of the post
            post.get().setContent(postDTO.getContent());
            int savedpost = postService.updatePost(post.get());
            answer = new PostDTO(post.get().getPostId(), postDTO.getContent(), postDTO.getAttachments(), post.get().getCreatedAt());
        }
        else if (postDTO.getAttachments()!=null){     ///  update the attachments of the post
            System.out.println("ana el attach ya wlaaa");
            List<Attachment> attachments = postDTO.getAttachments();
            List<Attachment> savedAttachments = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if (attachment.getAttachId() != null) { // edit exisiting attachment
                    attachmentService.updateAttachmentById(attachment);
                    savedAttachments.add(attachment);
                }else { // no id is added so it is a new attachment
                    Attachment saved = attachmentService.save(attachment);
                    savedAttachments.add(saved);
                    PostAttchment postAttchment = new PostAttchment(new PostAttachmentKey(post.get().getPostId(), saved.getAttachId()));
                    postAttachmentService.save(postAttchment);
                }
            }
            answer = new PostDTO( postDTO.getContent(),post.get().getUserId(),savedAttachments, post.get().getCreatedAt());
        }
        return answer;
    }
    @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable Long id) {
        List<Long> attachmentIDs = postAttachmentService.findAttachmentsByIdOfPost(id);
        for (Long attachmentID : attachmentIDs) {
            attachmentService.deleteById(attachmentID);
            postAttachmentService.deleteAttachmentById(attachmentID, id);
        }
        postAttachmentService.deletePostById(id);
        postService.deleteById(id);
    }

}
