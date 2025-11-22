package com.Project.SmartLink.Services;

import com.Project.SmartLink.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> findAll();
    Post findById(int theId);
    void deleteById(int theId);
    Post save(Post employee);
    List<Post> findByUserId(int theId);
    List<Post> findByContent(String theContent);
    Post updatePost(Post post);

}
