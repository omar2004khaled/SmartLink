package com.Project.SmartLink.Services;

import com.Project.SmartLink.Repository.PostRepository;
import com.Project.SmartLink.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PostServiceImp implements PostService{
    private PostRepository postRepository;

    @Autowired
    public PostServiceImp(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(int theId) {
        return postRepository.findById(theId);
    }

    @Override
    public void deleteById(int theId) {
        postRepository.deleteById(theId);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> findByUserId(int theId) {
        return postRepository.findByUserId(theId);
    }

    @Override
    public List<Post> findByContent(String theContent) {
        return postRepository.findByContent(theContent);
    }

    @Override
    public Post updatePost(Post post) {
        return postRepository.updatePost(post);
    }
}
