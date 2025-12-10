package com.example.auth.controller;


import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.service.JobServices.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @GetMapping("/company/current/{companyId}")
    public ResponseEntity<List<JobResponse>> getCurrentJobs(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.getCurrentJobs(companyId));
    }

    @GetMapping("/company/ended/{companyId}")
    public ResponseEntity<List<JobResponse>> getEndedJobs(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.getEndedJobs(companyId));
    }
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long jobId,@RequestBody JobUpdateRequest request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }


}
