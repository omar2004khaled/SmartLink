package com.example.auth.controller;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.JobApplication;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.service.JobServices.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
