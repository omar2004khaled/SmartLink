package com.example.auth.controller;

import com.example.auth.controller.ProfileControllers.EducationController;
import com.example.auth.dto.ProfileDtos.EducationDto;
import com.example.auth.service.ProfileServices.EducationService;
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
class EducationControllerTest {

    @Mock
    private EducationService educationService;

    @InjectMocks
    private EducationController educationController;

    private EducationDto educationDto;

    @BeforeEach
    void setUp() {
        educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setSchool("MIT");
        educationDto.setFieldOfStudy("Computer Science");
        educationDto.setStartDate(LocalDate.of(2020, 9, 1));
        educationDto.setEndDate(LocalDate.of(2024, 5, 31));
        educationDto.setDescription("Bachelors");
    }

    @Test
    void testList() {
        List<EducationDto> educations = new ArrayList<>();
        educations.add(educationDto);

        when(educationService.getEducationForProfile(1L)).thenReturn(educations);

        List<EducationDto> result = educationController.list(1L);

        assertEquals(1, result.size());
        assertEquals("MIT", result.get(0).getSchool());
        verify(educationService, times(1)).getEducationForProfile(1L);
    }

    @Test
    void testListEmpty() {
        when(educationService.getEducationForProfile(1L)).thenReturn(new ArrayList<>());

        List<EducationDto> result = educationController.list(1L);

        assertTrue(result.isEmpty());
        verify(educationService, times(1)).getEducationForProfile(1L);
    }

    @Test
    void testAdd() {
        when(educationService.addEducation(1L, educationDto)).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = educationController.add(1L, educationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MIT", response.getBody().getSchool());
        verify(educationService, times(1)).addEducation(1L, educationDto);
    }

    @Test
    void testUpdate() {
        when(educationService.updateEducation(1L, 1L, educationDto)).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = educationController.update(1L, 1L, educationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MIT", response.getBody().getSchool());
        verify(educationService, times(1)).updateEducation(1L, 1L, educationDto);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = educationController.delete(1L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(educationService, times(1)).deleteEducation(1L, 1L);
    }
}
