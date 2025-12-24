package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.Education;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class EducationEntityTest {

    private Education education;
    private JobSeekerProfile profile;

    @BeforeEach
    void setUp() {
        education = new Education();
        profile = new JobSeekerProfile();
    }

    @Test
    void testSetAndGetEducationId() {
        Long id = 1L;
        education.setEducationId(id);
        assertEquals(id, education.getEducationId());
    }

    @Test
    void testSetAndGetProfile() {
        education.setProfile(profile);
        assertEquals(profile, education.getProfile());
    }

    @Test
    void testSetAndGetSchool() {
        String school = "Harvard University";
        education.setSchool(school);
        assertEquals(school, education.getSchool());
    }

    @Test
    void testSetAndGetFieldOfStudy() {
        String field = "Computer Science";
        education.setFieldOfStudy(field);
        assertEquals(field, education.getFieldOfStudy());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2018, 9, 1);
        education.setStartDate(startDate);
        assertEquals(startDate, education.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2022, 5, 31);
        education.setEndDate(endDate);
        assertEquals(endDate, education.getEndDate());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "Bachelor's degree";
        education.setDescription(description);
        assertEquals(description, education.getDescription());
    }

    @Test
    void testConstructorWithAllFields() {
        Long id = 1L;
        String school = "MIT";
        String field = "CS";
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 5, 31);
        String description = "Degree";

        education = new Education(id, profile, school, field, startDate, endDate, description);

        assertEquals(id, education.getEducationId());
        assertEquals(profile, education.getProfile());
        assertEquals(school, education.getSchool());
        assertEquals(field, education.getFieldOfStudy());
        assertEquals(startDate, education.getStartDate());
        assertEquals(endDate, education.getEndDate());
        assertEquals(description, education.getDescription());
    }

    @Test
    void testDefaultConstructor() {
        Education edu = new Education();
        assertNull(edu.getEducationId());
        assertNull(edu.getProfile());
        assertNull(edu.getSchool());
        assertNull(edu.getFieldOfStudy());
        assertNull(edu.getStartDate());
        assertNull(edu.getEndDate());
        assertNull(edu.getDescription());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String school = "MIT";
        String field = "CS";
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 5, 31);
        String description = "Degree";

        education.setEducationId(id);
        education.setProfile(profile);
        education.setSchool(school);
        education.setFieldOfStudy(field);
        education.setStartDate(startDate);
        education.setEndDate(endDate);
        education.setDescription(description);

        assertEquals(id, education.getEducationId());
        assertEquals(profile, education.getProfile());
        assertEquals(school, education.getSchool());
        assertEquals(field, education.getFieldOfStudy());
        assertEquals(startDate, education.getStartDate());
        assertEquals(endDate, education.getEndDate());
        assertEquals(description, education.getDescription());
    }
}
