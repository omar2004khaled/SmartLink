package com.example.auth.service;

import com.example.auth.dto.EducationDto;
import com.example.auth.entity.Education;
import com.example.auth.entity.JobSeekerProfile;
import com.example.auth.repository.EducationRepository;
import com.example.auth.repository.JobSeekerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EducationService {

    private final EducationRepository eduRepo;
    private final JobSeekerProfileRepository profileRepo;

    public EducationService(EducationRepository eduRepo, JobSeekerProfileRepository profileRepo) {
        this.eduRepo = eduRepo;
        this.profileRepo = profileRepo;
    }

    @Transactional(readOnly = true)
    public List<EducationDto> getEducationForProfile(Long profileId) {
        return eduRepo.findByProfile_ProfileId(profileId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EducationDto addEducation(Long profileId, EducationDto dto) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Education e = new Education();
        e.setProfile(profile);
        apply(dto, e);

        Education saved = eduRepo.save(e);
        return toDto(saved);
    }

    @Transactional
    public EducationDto updateEducation(Long profileId, Long eduId, EducationDto dto) {
        Education e = eduRepo.findById(eduId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!e.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Education does not belong to this profile");
        }

        apply(dto, e);
        Education saved = eduRepo.save(e);
        return toDto(saved);
    }

    @Transactional
    public void deleteEducation(Long profileId, Long eduId) {
        Education e = eduRepo.findById(eduId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!e.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Education does not belong to this profile");
        }

        eduRepo.delete(e);
    }

    private EducationDto toDto(Education e) {
        EducationDto dto = new EducationDto();
        dto.setId(e.getEducationId());
        dto.setSchool(e.getSchool());
        dto.setFieldOfStudy(e.getFieldOfStudy());
        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());
        dto.setDescription(e.getDescription());
        return dto;
    }

    private void apply(EducationDto dto, Education e) {
        if (dto.getSchool() != null)        e.setSchool(dto.getSchool());
        if (dto.getFieldOfStudy() != null)  e.setFieldOfStudy(dto.getFieldOfStudy());
        if (dto.getStartDate() != null)     e.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)       e.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null)   e.setDescription(dto.getDescription());
    }
}
