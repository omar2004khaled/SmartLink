package com.example.auth.service;

import com.example.auth.dto.ProfileDtos.ProjectDto;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.entity.ProfileEntities.Project;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.ProfileRepositories.ProjectRepository;
import com.example.auth.service.ProfileServices.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private JobSeekerProfileRepository profileRepository;

    @InjectMocks
    private ProjectService projectService;

    private JobSeekerProfile profile;
    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        profile = new JobSeekerProfile();
        profile.setProfileId(1L);

        project = new Project();
        project.setProjectId(1L);
        project.setTitle("E-Commerce Platform");
        project.setDescription("Full-stack app");
        project.setProjectUrl("https://github.com");
        project.setStartDate(LocalDate.of(2023, 1, 1));
        project.setEndDate(LocalDate.of(2024, 3, 31));

        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setTitle("E-Commerce Platform");
        projectDto.setDescription("Full-stack app");
        projectDto.setProjectUrl("https://github.com");
        projectDto.setStartDate(LocalDate.of(2023, 1, 1));
        projectDto.setEndDate(LocalDate.of(2024, 3, 31));

        Set<Project> projects = new HashSet<>();
        projects.add(project);
        profile.setProjects(projects);
    }

    @Test
    void testGetProjectsForProfile() {
        when(projectRepository.findByProfile_ProfileId(1L)).thenReturn(List.of(project));

        List<ProjectDto> result = projectService.getProjectsForProfile(1L);

        assertEquals(1, result.size());
        assertEquals("E-Commerce Platform", result.get(0).getTitle());
        verify(projectRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testGetProjectsForProfileNotFound() {
        when(projectRepository.findByProfile_ProfileId(1L)).thenReturn(List.of());

        List<ProjectDto> result = projectService.getProjectsForProfile(1L);
        
        assertEquals(0, result.size());
        verify(projectRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testAddProjectToProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto result = projectService.addProjectToProfile(1L, projectDto);

        assertNotNull(result);
        assertEquals("E-Commerce Platform", result.getTitle());
        verify(profileRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testAddProjectToProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.addProjectToProfile(1L, projectDto));
        verify(profileRepository, times(1)).findById(1L);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testUpdateProjectForProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto updateDto = new ProjectDto();
        updateDto.setTitle("Updated Project");

        ProjectDto result = projectService.updateProjectForProfile(1L, 1L, updateDto);

        assertNotNull(result);
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testUpdateProjectProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.updateProjectForProfile(1L, 1L, projectDto));
        verify(profileRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProjectProjectNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.updateProjectForProfile(1L, 1L, projectDto));
        verify(profileRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteProjectFromProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProjectFromProfile(1L, 1L);

        verify(profileRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteProjectProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.deleteProjectFromProfile(1L, 1L));
        verify(profileRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteProjectProjectNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.deleteProjectFromProfile(1L, 1L));
        verify(profileRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(1L);
    }
}
