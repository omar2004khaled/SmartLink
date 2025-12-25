package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.EducationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EducationDtoTest {

    private EducationDto educationDto;

    @BeforeEach
    void setUp() {
        educationDto = new EducationDto();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        educationDto.setId(id);
        assertEquals(id, educationDto.getId());
    }

    @Test
    void testSetAndGetSchool() {
        String school = "MIT";
        educationDto.setSchool(school);
        assertEquals(school, educationDto.getSchool());
    }

    @Test
    void testSetAndGetFieldOfStudy() {
        String field = "Computer Science";
        educationDto.setFieldOfStudy(field);
        assertEquals(field, educationDto.getFieldOfStudy());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2020, 9, 1);
        educationDto.setStartDate(startDate);
        assertEquals(startDate, educationDto.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        educationDto.setEndDate(endDate);
        assertEquals(endDate, educationDto.getEndDate());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "Bachelors degree in Computer Science";
        educationDto.setDescription(description);
        assertEquals(description, educationDto.getDescription());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String school = "MIT";
        String field = "CS";
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        String description = "Degree";

        educationDto.setId(id);
        educationDto.setSchool(school);
        educationDto.setFieldOfStudy(field);
        educationDto.setStartDate(startDate);
        educationDto.setEndDate(endDate);
        educationDto.setDescription(description);

        assertEquals(id, educationDto.getId());
        assertEquals(school, educationDto.getSchool());
        assertEquals(field, educationDto.getFieldOfStudy());
        assertEquals(startDate, educationDto.getStartDate());
        assertEquals(endDate, educationDto.getEndDate());
        assertEquals(description, educationDto.getDescription());
    }

    @Test
    void testNullValues() {
        assertNull(educationDto.getId());
        assertNull(educationDto.getSchool());
        assertNull(educationDto.getFieldOfStudy());
        assertNull(educationDto.getStartDate());
        assertNull(educationDto.getEndDate());
        assertNull(educationDto.getDescription());
    }
}
