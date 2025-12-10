package com.example.auth.controller;


import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.service.JobServices.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

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
    public ResponseEntity<Page<JobResponse>> getCurrentJobs(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getCurrentJobs(companyId, page, size));
    }

    @GetMapping("/company/ended/{companyId}")
    public ResponseEntity<Page<JobResponse>> getEndedJobs(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getEndedJobs(companyId, page, size));
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
