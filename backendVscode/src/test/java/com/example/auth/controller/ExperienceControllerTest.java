package com.example.auth.controller;

import com.example.auth.controller.ProfileControllers.ExperienceController;
import com.example.auth.dto.ProfileDtos.ExperienceDto;
import com.example.auth.service.ProfileServices.ExperienceService;
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
class ExperienceControllerTest {

    @Mock
    private ExperienceService experienceService;

    @InjectMocks
    private ExperienceController experienceController;

    private ExperienceDto experienceDto;

    @BeforeEach
    void setUp() {
        experienceDto = new ExperienceDto();
        experienceDto.setId(1L);
        experienceDto.setCompanyName("Google");
        experienceDto.setTitle("Software Engineer");
        experienceDto.setLocation("Mountain View");
        experienceDto.setStartDate(LocalDate.of(2021, 6, 1));
        experienceDto.setEndDate(LocalDate.of(2023, 12, 31));
        experienceDto.setDescription("Senior role");
    }

    @Test
    void testList() {
        List<ExperienceDto> experiences = new ArrayList<>();
        experiences.add(experienceDto);

        when(experienceService.getExperienceForProfile(1L)).thenReturn(experiences);

        List<ExperienceDto> result = experienceController.list(1L);

        assertEquals(1, result.size());
        assertEquals("Google", result.get(0).getCompanyName());
        verify(experienceService, times(1)).getExperienceForProfile(1L);
    }

    @Test
    void testListEmpty() {
        when(experienceService.getExperienceForProfile(1L)).thenReturn(new ArrayList<>());

        List<ExperienceDto> result = experienceController.list(1L);

        assertTrue(result.isEmpty());
        verify(experienceService, times(1)).getExperienceForProfile(1L);
    }

    @Test
    void testAdd() {
        when(experienceService.addExperience(1L, experienceDto)).thenReturn(experienceDto);

        ResponseEntity<ExperienceDto> response = experienceController.add(1L, experienceDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Google", response.getBody().getCompanyName());
        verify(experienceService, times(1)).addExperience(1L, experienceDto);
    }

    @Test
    void testUpdate() {
        when(experienceService.updateExperience(1L, 1L, experienceDto)).thenReturn(experienceDto);

        ResponseEntity<ExperienceDto> response = experienceController.update(1L, 1L, experienceDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Google", response.getBody().getCompanyName());
        verify(experienceService, times(1)).updateExperience(1L, 1L, experienceDto);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = experienceController.delete(1L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(experienceService, times(1)).deleteExperience(1L, 1L);
    }
}
