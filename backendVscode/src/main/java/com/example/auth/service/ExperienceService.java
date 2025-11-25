package com.example.auth.service;

import com.example.auth.dto.ExperienceDto;
import com.example.auth.entity.Experience;
import com.example.auth.entity.JobSeekerProfile;
import com.example.auth.repository.ExperienceRepository;
import com.example.auth.repository.JobSeekerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepository expRepo;
    private final JobSeekerProfileRepository profileRepo;

    public ExperienceService(ExperienceRepository expRepo, JobSeekerProfileRepository profileRepo) {
        this.expRepo = expRepo;
        this.profileRepo = profileRepo;
    }

    @Transactional(readOnly = true)
    public List<ExperienceDto> getExperienceForProfile(Long profileId) {
        return expRepo.findByProfile_ProfileId(profileId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ExperienceDto addExperience(Long profileId, ExperienceDto dto) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Experience e = new Experience();
        e.setProfile(profile);
        apply(dto, e);

        Experience saved = expRepo.save(e);
        return toDto(saved);
    }

    @Transactional
    public ExperienceDto updateExperience(Long profileId, Long expId, ExperienceDto dto) {
        Experience e = expRepo.findById(expId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!e.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Experience does not belong to this profile");
        }

        apply(dto, e);
        Experience saved = expRepo.save(e);
        return toDto(saved);
    }

    @Transactional
    public void deleteExperience(Long profileId, Long expId) {
        Experience e = expRepo.findById(expId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!e.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Experience does not belong to this profile");
        }

        expRepo.delete(e);
    }

    private ExperienceDto toDto(Experience e) {
        ExperienceDto dto = new ExperienceDto();
        dto.setId(e.getExperienceId());
        dto.setCompanyName(e.getCompanyName());
        dto.setTitle(e.getTitle());
        dto.setLocation(e.getLocation());
        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());
        dto.setDescription(e.getDescription());
        return dto;
    }

    private void apply(ExperienceDto dto, Experience e) {
        if (dto.getCompanyName() != null) e.setCompanyName(dto.getCompanyName());
        if (dto.getTitle() != null)       e.setTitle(dto.getTitle());
        if (dto.getLocation() != null)    e.setLocation(dto.getLocation());
        if (dto.getStartDate() != null)   e.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)     e.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null) e.setDescription(dto.getDescription());
    }
}
