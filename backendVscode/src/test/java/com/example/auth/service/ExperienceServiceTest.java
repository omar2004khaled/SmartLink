package com.example.auth.service;

import com.example.auth.dto.ProfileDtos.ExperienceDto;
import com.example.auth.entity.ProfileEntities.Experience;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.repository.ProfileRepositories.ExperienceRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.service.ProfileServices.ExperienceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private JobSeekerProfileRepository profileRepository;

    @InjectMocks
    private ExperienceService experienceService;

    private JobSeekerProfile profile;
    private Experience experience;
    private ExperienceDto experienceDto;

    @BeforeEach
    void setUp() {
        profile = new JobSeekerProfile();
        profile.setProfileId(1L);

        experience = new Experience();
        experience.setExperienceId(1L);
        experience.setCompanyName("Google");
        experience.setTitle("Software Engineer");
        experience.setLocation("Mountain View");
        experience.setStartDate(LocalDate.of(2021, 6, 1));
        experience.setEndDate(LocalDate.of(2023, 12, 31));
        experience.setDescription("Senior role");
        experience.setProfile(profile);

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
    void testGetExperienceForProfile() {
        List<Experience> experiences = new ArrayList<>();
        experiences.add(experience);

        when(experienceRepository.findByProfile_ProfileId(1L)).thenReturn(experiences);

        List<ExperienceDto> result = experienceService.getExperienceForProfile(1L);

        assertEquals(1, result.size());
        assertEquals("Google", result.get(0).getCompanyName());
        verify(experienceRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testGetExperienceForProfileEmpty() {
        when(experienceRepository.findByProfile_ProfileId(1L)).thenReturn(new ArrayList<>());

        List<ExperienceDto> result = experienceService.getExperienceForProfile(1L);

        assertTrue(result.isEmpty());
        verify(experienceRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testAddExperience() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(experienceRepository.save(any(Experience.class))).thenReturn(experience);

        ExperienceDto result = experienceService.addExperience(1L, experienceDto);

        assertNotNull(result);
        assertEquals("Google", result.getCompanyName());
        assertEquals("Software Engineer", result.getTitle());
        verify(profileRepository, times(1)).findById(1L);
        verify(experienceRepository, times(1)).save(any(Experience.class));
    }

    @Test
    void testAddExperienceProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> experienceService.addExperience(1L, experienceDto));
        verify(profileRepository, times(1)).findById(1L);
        verify(experienceRepository, never()).save(any());
    }

    @Test
    void testUpdateExperience() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
        when(experienceRepository.save(any(Experience.class))).thenReturn(experience);

        ExperienceDto updateDto = new ExperienceDto();
        updateDto.setCompanyName("Microsoft");

        ExperienceDto result = experienceService.updateExperience(1L, 1L, updateDto);

        assertNotNull(result);
        verify(experienceRepository, times(1)).findById(1L);
        verify(experienceRepository, times(1)).save(any(Experience.class));
    }

    @Test
    void testUpdateExperienceNotFound() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> experienceService.updateExperience(1L, 1L, experienceDto));
        verify(experienceRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteExperience() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));

        experienceService.deleteExperience(1L, 1L);

        verify(experienceRepository, times(1)).findById(1L);
        verify(experienceRepository, times(1)).delete(experience);
    }

    @Test
    void testDeleteExperienceNotFound() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> experienceService.deleteExperience(1L, 1L));
        verify(experienceRepository, times(1)).findById(1L);
        verify(experienceRepository, never()).delete(any());
    }

    @Test
    void testUpdateExperienceWrongProfile() {
        JobSeekerProfile differentProfile = new JobSeekerProfile();
        differentProfile.setProfileId(2L);
        experience.setProfile(differentProfile);

        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));

        assertThrows(RuntimeException.class, () -> experienceService.updateExperience(1L, 1L, experienceDto));
    }
}
