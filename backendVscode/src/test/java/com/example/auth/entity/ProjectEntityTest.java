package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
    }

    @Test
    void testSetAndGetProjectId() {
        Long id = 1L;
        project.setProjectId(id);
        assertEquals(id, project.getProjectId());
    }

    @Test
    void testSetAndGetTitle() {
        String title = "Mobile App";
        project.setTitle(title);
        assertEquals(title, project.getTitle());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "A mobile application for iOS and Android";
        project.setDescription(description);
        assertEquals(description, project.getDescription());
    }

    @Test
    void testSetAndGetProjectUrl() {
        String url = "https://github.com/user/mobile-app";
        project.setProjectUrl(url);
        assertEquals(url, project.getProjectUrl());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2022, 6, 1);
        project.setStartDate(startDate);
        assertEquals(startDate, project.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        project.setEndDate(endDate);
        assertEquals(endDate, project.getEndDate());
    }

    @Test
    void testConstructorWithAllFields() {
        Long id = 1L;
        String title = "App";
        String description = "Description";
        String url = "https://github.com";
        LocalDate startDate = LocalDate.of(2022, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        project = new Project(id, title, description, url, startDate, endDate);

        assertEquals(id, project.getProjectId());
        assertEquals(title, project.getTitle());
        assertEquals(description, project.getDescription());
        assertEquals(url, project.getProjectUrl());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
    }

    @Test
    void testDefaultConstructor() {
        Project proj = new Project();
        assertNull(proj.getProjectId());
        assertNull(proj.getTitle());
        assertNull(proj.getDescription());
        assertNull(proj.getProjectUrl());
        assertNull(proj.getStartDate());
        assertNull(proj.getEndDate());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String title = "E-Commerce";
        String description = "Web platform";
        String url = "https://ecommerce.com";
        LocalDate startDate = LocalDate.of(2022, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        project.setProjectId(id);
        project.setTitle(title);
        project.setDescription(description);
        project.setProjectUrl(url);
        project.setStartDate(startDate);
        project.setEndDate(endDate);

        assertEquals(id, project.getProjectId());
        assertEquals(title, project.getTitle());
        assertEquals(description, project.getDescription());
        assertEquals(url, project.getProjectUrl());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
    }

    @Test
    void testProjectWithoutEndDate() {
        project.setProjectId(1L);
        project.setTitle("Ongoing Project");
        project.setStartDate(LocalDate.of(2024, 1, 1));

        assertEquals(1L, project.getProjectId());
        assertEquals("Ongoing Project", project.getTitle());
        assertEquals(LocalDate.of(2024, 1, 1), project.getStartDate());
        assertNull(project.getEndDate());
    }
}
