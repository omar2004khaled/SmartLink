package com.example.auth.controller;

import com.example.auth.exceptions.NotEnoughInformationException;
import com.example.auth.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikeCommentController {
    LikeService likeService;
    @Autowired
    public LikeCommentController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/{userId}/{commentId}")
    public void LikeComment(@PathVariable Long userId,@PathVariable Long commentId) throws NotEnoughInformationException{
        try {
            likeService.makeOrDeleteLike(userId, commentId);
        } catch (NotEnoughInformationException e) {
            throw new NotEnoughInformationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
