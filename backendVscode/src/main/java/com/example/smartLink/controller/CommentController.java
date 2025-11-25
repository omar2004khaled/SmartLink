package com.example.smartLink.controller;

import com.example.smartLink.dto.CommentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @PostMapping("/add")
    public ResponseEntity<CommentDTO> addComment(CommentDTO){

    }
}
