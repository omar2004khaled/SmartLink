package com.example.auth.controller;

import com.example.auth.controller.ProfileControllers.ProjectController;
import com.example.auth.dto.ProfileDtos.ProjectDto;
import com.example.auth.service.ProfileServices.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setTitle("E-Commerce Platform");
        projectDto.setDescription("Full-stack app");
        projectDto.setProjectUrl("https://github.com");
        projectDto.setStartDate(LocalDate.of(2023, 1, 1));
        projectDto.setEndDate(LocalDate.of(2024, 3, 31));
    }

    @Test
    void testList() {
        List<ProjectDto> projects = new ArrayList<>();
        projects.add(projectDto);

        when(projectService.getProjectsForProfile(1L)).thenReturn(projects);

        List<ProjectDto> result = projectController.list(1L);

        assertEquals(1, result.size());
        assertEquals("E-Commerce Platform", result.get(0).getTitle());
        verify(projectService, times(1)).getProjectsForProfile(1L);
    }

    @Test
    void testListEmpty() {
        when(projectService.getProjectsForProfile(1L)).thenReturn(new ArrayList<>());

        List<ProjectDto> result = projectController.list(1L);

        assertTrue(result.isEmpty());
        verify(projectService, times(1)).getProjectsForProfile(1L);
    }

    @Test
    void testAdd() {
        when(projectService.addProjectToProfile(1L, projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.add(1L, projectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("E-Commerce Platform", response.getBody().getTitle());
        verify(projectService, times(1)).addProjectToProfile(1L, projectDto);
    }

    @Test
    void testUpdate() {
        when(projectService.updateProjectForProfile(1L, 1L, projectDto)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response = projectController.update(1L, 1L, projectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("E-Commerce Platform", response.getBody().getTitle());
        verify(projectService, times(1)).updateProjectForProfile(1L, 1L, projectDto);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = projectController.delete(1L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(projectService, times(1)).deleteProjectFromProfile(1L, 1L);
    }
}
