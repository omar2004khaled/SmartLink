package com.example.auth.controller;

import com.example.auth.dto.PostDTO;  // Updated package path
import com.example.auth.service.AttachmentService.AttachmentService;  // Updated package path
import com.example.auth.service.PostAttachmentService.PostAttachmentService;  // Updated package path
import com.example.auth.service.PostService.PostService;  // Updated package path
import com.example.auth.entity.Post;  // Updated package path
import com.example.auth.entity.Attachment;  // Updated package path
import com.example.auth.entity.PostAttachmentKey;  // Updated package path
import com.example.auth.entity.PostAttchment;  // Updated package path
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
@CrossOrigin(origins = "http://localhost:5173")
public class PostRestController {
    private PostService postService;
    @Autowired
    public PostRestController(PostService postService ) {
        this.postService = postService;

    }
    @GetMapping("/all")
    public List<PostDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size,
                                                           @RequestParam(defaultValue = "PostId") String sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.findAll(pageable);
    }
    @GetMapping("/{id}")
    public PostDTO findById(@PathVariable Long id) {
        return postService.findById(id);
    }
    @PostMapping("/add")
    public PostDTO addPost(@RequestBody PostDTO postDTO) {
        return postService.save(postDTO);
    }
    @PutMapping("/update/{id}")
    public PostDTO updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        if (postDTO==null) return null;
        return postService.updatePost(id, postDTO);
    }
    @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteById(id);
    }

}
