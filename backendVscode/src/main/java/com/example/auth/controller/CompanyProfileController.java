package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCompanyByUserId(@PathVariable Long userId) {
        try {
            CompanyDTO company = companyProfileService.getCompanyByUserId(userId);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company profile not found for user");
        }
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompanyProfile(@PathVariable Long companyId, @RequestParam(required = false) Long userId) {
        try {
            CompanyDTO company = companyProfileService.getCompanyProfile(companyId, userId);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR in get Profile");
        }
    }

    @GetMapping("/{companyId}/about")
    public ResponseEntity<?> getCompanyAbout(@PathVariable Long companyId) {
        try {
            CompanyDTO company = companyProfileService.getCompanyProfile(companyId, null);

            AboutCompanyDTO response = new AboutCompanyDTO();
            response.setDescription(company.getDescription());
            response.setWebsite(company.getWebsite());
            response.setIndustry(company.getIndustry());
            response.setFounded(company.getFounded());
            response.setLocations(company.getLocations());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR in get about data");
        }
    }

    @GetMapping("/{companyId}/posts")
    public ResponseEntity<?> getCompanyPosts(@PathVariable Long companyId) {
        try {
            //Implement posts retrieval
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR in get posts");
        }
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompanyProfile(@PathVariable Long companyId, @RequestBody CompanyUpdateDTO request) {
        try {
            System.out.println(request.toString());
            CompanyDTO updated = companyProfileService.updateCompanyProfile(companyId, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error in update");
        }
    }

    @PostMapping("/{companyId}/follow")
    public ResponseEntity<?> followCompany(@PathVariable Long companyId, @RequestBody FollowRequest request) {
        try {
            companyProfileService.followCompany(companyId, request.getUserId());
            return ResponseEntity.ok("Successfully followed company");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error in follow");
        }
    }

    @PostMapping("/{companyId}/unfollow")
    public ResponseEntity<?> unfollowCompany(@PathVariable Long companyId, @RequestBody FollowRequest request) {
        try {
            companyProfileService.unfollowCompany(companyId, request.getUserId());
            return ResponseEntity.ok("Successfully unfollowed company");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error in unfollow");
        }
    }
}