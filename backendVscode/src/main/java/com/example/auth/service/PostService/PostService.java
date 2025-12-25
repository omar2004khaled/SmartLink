package com.example.auth.service.PostService;

import com.example.auth.dto.PostDTO;
import com.example.auth.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    List<PostDTO> findAll(Pageable pageable);
    PostDTO findById(Long theId);
    void deleteById(Long theId);
    PostDTO save(PostDTO postDTO);
    List<Post> findByUserId(Long theId);
    List<Post> findByContent(String theContent);
    PostDTO updatePost(Long id, PostDTO postDTO);
    Long findAuthorByPostId(Long postId);
}
