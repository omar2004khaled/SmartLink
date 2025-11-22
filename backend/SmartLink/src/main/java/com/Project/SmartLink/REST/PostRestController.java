package com.Project.SmartLink.REST;

import com.Project.SmartLink.Services.PostService;
import com.Project.SmartLink.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Post")
public class PostRestController {
    private PostService postService;
    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/all")
    public List<Post> findAll() {
        return postService.findAll();
    }
    @GetMapping("/{id}")
    public Post findById(@PathVariable int id) {
        return postService.findById(id);
    }
    @PostMapping("/add")
    public Post addPost(@RequestBody Post post) {
        return postService.save(post);
    }
    @PutMapping("/update/{id}")
    public Post updatePost(@PathVariable int id, @RequestBody Post post) {
        return postService.updatePost(post);
    }
    @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable int id) {
        postService.deleteById(id);
    }

}
