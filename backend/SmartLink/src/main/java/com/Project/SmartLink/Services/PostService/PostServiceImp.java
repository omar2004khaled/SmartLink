package com.Project.SmartLink.Services.PostService;

import com.Project.SmartLink.Repository.PostRepository;
import com.Project.SmartLink.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public Optional<Post> findById(Long theId) {
        return postRepository.findById(theId);
    }

    @Override
    public void deleteById(Long theId) {

        postRepository.deleteById(theId);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
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
    public int updatePost(Post post) {
        return postRepository.updateContent(post.getPostId(), post.getContent());
    }
}
