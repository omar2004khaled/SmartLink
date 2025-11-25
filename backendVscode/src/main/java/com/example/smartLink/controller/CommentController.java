package com.example.smartLink.controller;

import com.example.smartLink.dto.CommentDTO;
import com.example.smartLink.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    CommentService commentService;
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping("/add")
    public ResponseEntity<Long> addComment(@RequestBody CommentDTO commentDTO){
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.addComment(commentDTO));
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<Boolean> removeComment(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.RemoveComment(id));
    }


}
