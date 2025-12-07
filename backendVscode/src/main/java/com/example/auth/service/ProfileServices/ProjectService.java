package com.example.auth.service.ProfileServices;

import com.example.auth.dto.ProfileDtos.ProjectDto;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.entity.ProfileEntities.Project;
import com.example.auth.repository.ProfileRepositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final JobSeekerProfileRepository profileRepo;

    public ProjectService(ProjectRepository projectRepo, JobSeekerProfileRepository profileRepo) {
        this.projectRepo = projectRepo;
        this.profileRepo = profileRepo;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsForProfile(Long profileId) {
        return projectRepo.findByProfile_ProfileId(profileId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ProjectDto addProjectToProfile(Long profileId, ProjectDto dto) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Project project = new Project();
        apply(dto, project);

        Project saved = projectRepo.save(project);

        profile.getProjects().add(saved); // update link

        return toDto(saved);
    }

    @Transactional
    public ProjectDto updateProjectForProfile(Long profileId, Long projectId, ProjectDto dto) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!profile.getProjects().contains(project)) {
            throw new RuntimeException("Project is not linked to this profile");
        }

        apply(dto, project);
        Project saved = projectRepo.save(project);
        return toDto(saved);
    }

    @Transactional
    public void deleteProjectFromProfile(Long profileId, Long projectId) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        profile.getProjects().remove(project);
        // Optionally also delete the project entity entirely:
        // projectRepo.delete(project);
    }

    private ProjectDto toDto(Project p) {
        ProjectDto dto = new ProjectDto();
        dto.setId(p.getProjectId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setProjectUrl(p.getProjectUrl());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        return dto;
    }

    private void apply(ProjectDto dto, Project p) {
        if (dto.getTitle() != null)       p.setTitle(dto.getTitle());
        if (dto.getDescription() != null) p.setDescription(dto.getDescription());
        if (dto.getProjectUrl() != null)  p.setProjectUrl(dto.getProjectUrl());
        if (dto.getStartDate() != null)   p.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)     p.setEndDate(dto.getEndDate());
    }
}
