package com.example.auth.controller;

import com.example.auth.dto.JobSeekerProfileRequest;
import com.example.auth.dto.JobSeekerProfileResponse;
import com.example.auth.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSeekerProfileResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProfile(id));
    }

    @PostMapping
    public ResponseEntity<JobSeekerProfileResponse> create(@RequestBody JobSeekerProfileRequest req) {
        return ResponseEntity.ok(service.createProfile(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobSeekerProfileResponse> update(@PathVariable Long id,
                                                           @RequestBody JobSeekerProfileRequest req) {
        return ResponseEntity.ok(service.updateProfile(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
