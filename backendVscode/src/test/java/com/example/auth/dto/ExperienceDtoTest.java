package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.ExperienceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceDtoTest {

    private ExperienceDto experienceDto;

    @BeforeEach
    void setUp() {
        experienceDto = new ExperienceDto();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        experienceDto.setId(id);
        assertEquals(id, experienceDto.getId());
    }

    @Test
    void testSetAndGetCompanyName() {
        String company = "Google";
        experienceDto.setCompanyName(company);
        assertEquals(company, experienceDto.getCompanyName());
    }

    @Test
    void testSetAndGetTitle() {
        String title = "Software Engineer";
        experienceDto.setTitle(title);
        assertEquals(title, experienceDto.getTitle());
    }

    @Test
    void testSetAndGetLocation() {
        String location = "Mountain View, CA";
        experienceDto.setLocation(location);
        assertEquals(location, experienceDto.getLocation());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        experienceDto.setStartDate(startDate);
        assertEquals(startDate, experienceDto.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        experienceDto.setEndDate(endDate);
        assertEquals(endDate, experienceDto.getEndDate());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "Senior engineer role";
        experienceDto.setDescription(description);
        assertEquals(description, experienceDto.getDescription());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String company = "Google";
        String title = "Engineer";
        String location = "CA";
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String description = "Role";

        experienceDto.setId(id);
        experienceDto.setCompanyName(company);
        experienceDto.setTitle(title);
        experienceDto.setLocation(location);
        experienceDto.setStartDate(startDate);
        experienceDto.setEndDate(endDate);
        experienceDto.setDescription(description);

        assertEquals(id, experienceDto.getId());
        assertEquals(company, experienceDto.getCompanyName());
        assertEquals(title, experienceDto.getTitle());
        assertEquals(location, experienceDto.getLocation());
        assertEquals(startDate, experienceDto.getStartDate());
        assertEquals(endDate, experienceDto.getEndDate());
        assertEquals(description, experienceDto.getDescription());
    }

    @Test
    void testNullValues() {
        assertNull(experienceDto.getId());
        assertNull(experienceDto.getCompanyName());
        assertNull(experienceDto.getTitle());
        assertNull(experienceDto.getLocation());
        assertNull(experienceDto.getStartDate());
        assertNull(experienceDto.getEndDate());
        assertNull(experienceDto.getDescription());
    }
}
