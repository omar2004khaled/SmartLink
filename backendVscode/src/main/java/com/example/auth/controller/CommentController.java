package com.example.auth.controller;

import com.example.auth.dto.CommentDTO;
import com.example.auth.exceptions.NonExistentObject;
import com.example.auth.exceptions.NotEnoughInformationException;
import com.example.auth.service.CommentService;
import com.example.auth.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {
    CommentService commentService;
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping("/add")
    public ResponseEntity<Long> addComment(@RequestBody CommentDTO commentDTO) throws Exception{
      
        try {

            if(commentDTO.getUrl() == null && commentDTO.getText() ==null) throw new NotEnoughInformationException("There are too many nulls");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(commentService.addComment(commentDTO));
        } catch (NotEnoughInformationException notEnoughInformationException){
            throw new NotEnoughInformationException(notEnoughInformationException.getMessage());
        }


    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> removeComment(@PathVariable Long id) throws Exception{
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(commentService.RemoveComment(id));
        }catch (NonExistentObject nonExistenceObject){
            throw new NonExistentObject(nonExistenceObject.getMessage());
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    @GetMapping("/getAll/{postId}/{pageNo}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable Long postId,@PathVariable int pageNo)
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByPostId(pageNo, Constants.PAGESIZE,postId));
    }

    @PutMapping("/put")
    public ResponseEntity<CommentDTO> getAllComments(@RequestBody CommentDTO commentDTO)
    {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(commentService.update(commentDTO));
        }catch (NonExistentObject nonExistenceObject){
            throw new NonExistentObject(nonExistenceObject.getMessage());
        }

    }

}
