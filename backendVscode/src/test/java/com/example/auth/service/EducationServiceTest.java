package com.example.auth.service;

import com.example.auth.dto.ProfileDtos.EducationDto;
import com.example.auth.entity.ProfileEntities.Education;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.repository.ProfileRepositories.EducationRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.service.ProfileServices.EducationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private JobSeekerProfileRepository profileRepository;

    @InjectMocks
    private EducationService educationService;

    private JobSeekerProfile profile;
    private Education education;
    private EducationDto educationDto;

    @BeforeEach
    void setUp() {
        profile = new JobSeekerProfile();
        profile.setProfileId(1L);

        education = new Education();
        education.setEducationId(1L);
        education.setSchool("MIT");
        education.setFieldOfStudy("Computer Science");
        education.setStartDate(LocalDate.of(2020, 9, 1));
        education.setEndDate(LocalDate.of(2024, 5, 31));
        education.setDescription("Bachelors");
        education.setProfile(profile);

        educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setSchool("MIT");
        educationDto.setFieldOfStudy("Computer Science");
        educationDto.setStartDate(LocalDate.of(2020, 9, 1));
        educationDto.setEndDate(LocalDate.of(2024, 5, 31));
        educationDto.setDescription("Bachelors");
    }

    @Test
    void testGetEducationForProfile() {
        List<Education> educations = new ArrayList<>();
        educations.add(education);

        when(educationRepository.findByProfile_ProfileId(1L)).thenReturn(educations);

        List<EducationDto> result = educationService.getEducationForProfile(1L);

        assertEquals(1, result.size());
        assertEquals("MIT", result.get(0).getSchool());
        verify(educationRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testGetEducationForProfileEmpty() {
        when(educationRepository.findByProfile_ProfileId(1L)).thenReturn(new ArrayList<>());

        List<EducationDto> result = educationService.getEducationForProfile(1L);

        assertTrue(result.isEmpty());
        verify(educationRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testAddEducation() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(educationRepository.save(any(Education.class))).thenReturn(education);

        EducationDto result = educationService.addEducation(1L, educationDto);

        assertNotNull(result);
        assertEquals("MIT", result.getSchool());
        assertEquals("Computer Science", result.getFieldOfStudy());
        verify(profileRepository, times(1)).findById(1L);
        verify(educationRepository, times(1)).save(any(Education.class));
    }

    @Test
    void testAddEducationProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> educationService.addEducation(1L, educationDto));
        verify(profileRepository, times(1)).findById(1L);
        verify(educationRepository, never()).save(any());
    }

    @Test
    void testUpdateEducation() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        when(educationRepository.save(any(Education.class))).thenReturn(education);

        EducationDto updateDto = new EducationDto();
        updateDto.setSchool("Harvard");

        EducationDto result = educationService.updateEducation(1L, 1L, updateDto);

        assertNotNull(result);
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, times(1)).save(any(Education.class));
    }

    @Test
    void testUpdateEducationNotFound() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> educationService.updateEducation(1L, 1L, educationDto));
        verify(educationRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteEducation() {
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));

        educationService.deleteEducation(1L, 1L);

        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, times(1)).delete(education);
    }

    @Test
    void testDeleteEducationNotFound() {
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> educationService.deleteEducation(1L, 1L));
        verify(educationRepository, times(1)).findById(1L);
        verify(educationRepository, never()).delete(any());
    }

    @Test
    void testUpdateEducationWrongProfile() {
        JobSeekerProfile differentProfile = new JobSeekerProfile();
        differentProfile.setProfileId(2L);
        education.setProfile(differentProfile);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));

        assertThrows(RuntimeException.class, () -> educationService.updateEducation(1L, 1L, educationDto));
    }
}
