package com.Project.SmartLink.Services.PostService;

import com.Project.SmartLink.entity.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Page<Post> findAll(Pageable pageable);
    Optional<Post> findById(Long theId);
    void deleteById(Long theId);
    Post save(Post employee);
    List<Post> findByUserId(Long theId);
    List<Post> findByContent(String theContent);
    int updatePost(Post post);

}
