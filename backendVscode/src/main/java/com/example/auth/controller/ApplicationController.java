package com.example.auth.controller;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.dto.JobDTO.CommentAppDTO;
import com.example.auth.dto.JobDTO.StatusChange;
import com.example.auth.entity.JobApplication;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.service.JobServices.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apply")
public class ApplicationController {
    JobApplicationService jobApplicationService;
    @Autowired
    public ApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }
    @PostMapping("/post")
    public ResponseEntity<Long> post(@RequestBody ApplicationDTO applicationDTO){
        return ResponseEntity.status(HttpStatus.OK).body(jobApplicationService.savePost(applicationDTO));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<List<ApplicationDTO>> post(@PathVariable Long jobId){
        return ResponseEntity.status(HttpStatus.OK).body(jobApplicationService.getByJobId(jobId));
    }

    @PatchMapping("/add/comment")
    public ResponseEntity<ApplicationDTO> addComment(@RequestBody CommentAppDTO comment){
        return ResponseEntity.status(HttpStatus.OK).body(jobApplicationService.addComment(comment.getComment(),comment.getJobAppId()));
    }

    @PatchMapping("/status")
    public ResponseEntity<ApplicationDTO> changeStatus(@RequestBody StatusChange comment){
        return ResponseEntity.status(HttpStatus.OK).body(jobApplicationService.changeStatus(comment.getStatus(),comment.getJobApp()));
    }

}
