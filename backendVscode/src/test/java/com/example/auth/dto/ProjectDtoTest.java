package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.ProjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDtoTest {

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        projectDto.setId(id);
        assertEquals(id, projectDto.getId());
    }

    @Test
    void testSetAndGetTitle() {
        String title = "E-Commerce Platform";
        projectDto.setTitle(title);
        assertEquals(title, projectDto.getTitle());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "A full-stack e-commerce application";
        projectDto.setDescription(description);
        assertEquals(description, projectDto.getDescription());
    }

    @Test
    void testSetAndGetProjectUrl() {
        String url = "https://github.com/user/ecommerce";
        projectDto.setProjectUrl(url);
        assertEquals(url, projectDto.getProjectUrl());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2023, 1, 15);
        projectDto.setStartDate(startDate);
        assertEquals(startDate, projectDto.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2024, 3, 20);
        projectDto.setEndDate(endDate);
        assertEquals(endDate, projectDto.getEndDate());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String title = "Project";
        String description = "Description";
        String url = "https://example.com";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        projectDto.setId(id);
        projectDto.setTitle(title);
        projectDto.setDescription(description);
        projectDto.setProjectUrl(url);
        projectDto.setStartDate(startDate);
        projectDto.setEndDate(endDate);

        assertEquals(id, projectDto.getId());
        assertEquals(title, projectDto.getTitle());
        assertEquals(description, projectDto.getDescription());
        assertEquals(url, projectDto.getProjectUrl());
        assertEquals(startDate, projectDto.getStartDate());
        assertEquals(endDate, projectDto.getEndDate());
    }

    @Test
    void testNullValues() {
        assertNull(projectDto.getId());
        assertNull(projectDto.getTitle());
        assertNull(projectDto.getDescription());
        assertNull(projectDto.getProjectUrl());
        assertNull(projectDto.getStartDate());
        assertNull(projectDto.getEndDate());
    }
}
