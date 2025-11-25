package com.example.auth.service;

import com.example.auth.dto.JobSeekerProfileRequest;
import com.example.auth.dto.JobSeekerProfileResponse;
import com.example.auth.entity.JobSeekerProfile;
import com.example.auth.entity.Location;
import com.example.auth.entity.User;
import com.example.auth.repository.JobSeekerProfileRepository;
import com.example.auth.repository.LocationRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final JobSeekerProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final LocationRepository locationRepo;

    public ProfileService(JobSeekerProfileRepository profileRepo,UserRepository userRepo,LocationRepository locationRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.locationRepo = locationRepo;
    }

    @Transactional(readOnly = true)
    public JobSeekerProfileResponse getProfile(Long id) {
        JobSeekerProfile p = profileRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Profile not found with id " + id));
        return toResponse(p);
    }

    @Transactional
    public JobSeekerProfileResponse createProfile(JobSeekerProfileRequest req) {
        JobSeekerProfile p = new JobSeekerProfile();

        applyRequestToEntity(p, req);

        if (req.getUserId() != null) {
            User u = userRepo.findById(req.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id " + req.getUserId()));
            p.setUser(u); }
        if (req.getLocationId() != null) {
            Location loc = locationRepo.findById(req.getLocationId())
            .orElseThrow(() -> new RuntimeException("Location not found with id " + req.getLocationId()));
            p.setLocation(loc);
        }

        JobSeekerProfile saved = profileRepo.save(p);
        return toResponse(saved);
    }

    @Transactional
    public JobSeekerProfileResponse updateProfile(Long id, JobSeekerProfileRequest req) {
        JobSeekerProfile p = profileRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Profile not found with id " + id));
        applyRequestToEntity(p, req);

        if (req.getUserId() != null) {
            User u = userRepo.findById(req.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id " + req.getUserId()));
            p.setUser(u);}
        if (req.getLocationId() != null) {
            Location loc = locationRepo.findById(req.getLocationId())
            .orElseThrow(() -> new RuntimeException("Location not found with id " + req.getLocationId()));
            p.setLocation(loc);
        }

        JobSeekerProfile saved = profileRepo.save(p);
        return toResponse(saved);
    }

    @Transactional
    public void deleteProfile(Long id) {
        JobSeekerProfile p = profileRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id " + id));
        profileRepo.delete(p);
    }

    private void applyRequestToEntity(JobSeekerProfile p, JobSeekerProfileRequest req) {
        if (req.getProfilePicUrl() != null) p.setProfilePicUrl(req.getProfilePicUrl());
        if (req.getBio() != null) p.setBio(req.getBio());
        if (req.getHeadline() != null) p.setHeadline(req.getHeadline());
        if (req.getBirthDate() != null) p.setBirthDate(req.getBirthDate());
        if (req.getGender() != null) {
            try {
                p.setGender(JobSeekerProfile.Gender.valueOf(req.getGender()));
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Invalid gender: " + req.getGender());
            }
        }
    }

    private JobSeekerProfileResponse toResponse(JobSeekerProfile p) {
        JobSeekerProfileResponse dto = new JobSeekerProfileResponse();
        dto.setProfileId(p.getProfileId());
        dto.setProfilePicUrl(p.getProfilePicUrl());
        dto.setBio(p.getBio());
        dto.setHeadline(p.getHeadline());
        dto.setBirthDate(p.getBirthDate());
        dto.setGender(p.getGender() != null ? p.getGender().name() : null);

        User u = p.getUser();
        if (u != null) {
            dto.setUserId(u.getUserId());
            dto.setUserName(u.getName());
            dto.setUserEmail(u.getEmail());
        }

        Location loc = p.getLocation();
        if (loc != null) {
            dto.setLocationId(loc.getLocationId());
            dto.setCountry(loc.getCountry());
            dto.setCity(loc.getCity());
        }

        return dto;
    }
}
