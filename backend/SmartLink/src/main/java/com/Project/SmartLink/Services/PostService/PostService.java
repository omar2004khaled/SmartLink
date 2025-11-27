package com.Project.SmartLink.Services.PostService;

import com.Project.SmartLink.DTO.PostDTO;
import com.Project.SmartLink.entity.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostDTO> findAll(Pageable pageable);
    PostDTO findById(Long theId);
    void deleteById(Long theId);
    PostDTO save(PostDTO postDTO);
    List<Post> findByUserId(Long theId);
    List<Post> findByContent(String theContent);
    PostDTO updatePost(Long id, PostDTO postDTO);

}
