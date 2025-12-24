package com.example.auth.service;

import com.example.auth.dto.ProfileDtos.SkillDto;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.entity.ProfileEntities.Skill;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.ProfileRepositories.SkillRepository;
import com.example.auth.service.ProfileServices.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private JobSeekerProfileRepository profileRepository;

    @InjectMocks
    private SkillService skillService;

    private JobSeekerProfile profile;
    private Skill skill;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        profile = new JobSeekerProfile();
        profile.setProfileId(1L);

        skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("Java");
        skill.setProficiency("Expert");
        skill.setProfile(profile);

        skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setSkillName("Java");
        skillDto.setProficiency("Expert");
    }

    @Test
    void testGetSkillsForProfile() {
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);

        when(skillRepository.findByProfile_ProfileId(1L)).thenReturn(skills);

        List<SkillDto> result = skillService.getSkillsForProfile(1L);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getSkillName());
        verify(skillRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testGetSkillsForProfileEmpty() {
        when(skillRepository.findByProfile_ProfileId(1L)).thenReturn(new ArrayList<>());

        List<SkillDto> result = skillService.getSkillsForProfile(1L);

        assertTrue(result.isEmpty());
        verify(skillRepository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testAddSkillToProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        SkillDto result = skillService.addSkillToProfile(1L, skillDto);

        assertNotNull(result);
        assertEquals("Java", result.getSkillName());
        assertEquals("Expert", result.getProficiency());
        verify(profileRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    void testAddSkillToProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> skillService.addSkillToProfile(1L, skillDto));
        verify(profileRepository, times(1)).findById(1L);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void testUpdateSkill() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        SkillDto updateDto = new SkillDto();
        updateDto.setSkillName("Python");
        updateDto.setProficiency("Advanced");

        SkillDto result = skillService.updateSkill(1L, 1L, updateDto);

        assertNotNull(result);
        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    void testUpdateSkillNotFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> skillService.updateSkill(1L, 1L, skillDto));
        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void testUpdateSkillWrongProfile() {
        JobSeekerProfile differentProfile = new JobSeekerProfile();
        differentProfile.setProfileId(2L);
        skill.setProfile(differentProfile);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(RuntimeException.class, () -> skillService.updateSkill(1L, 1L, skillDto));
        verify(skillRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteSkill() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.deleteSkill(1L, 1L);

        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).delete(skill);
    }

    @Test
    void testDeleteSkillNotFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> skillService.deleteSkill(1L, 1L));
        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, never()).delete(any());
    }

    @Test
    void testDeleteSkillWrongProfile() {
        JobSeekerProfile differentProfile = new JobSeekerProfile();
        differentProfile.setProfileId(2L);
        skill.setProfile(differentProfile);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(RuntimeException.class, () -> skillService.deleteSkill(1L, 1L));
        verify(skillRepository, times(1)).findById(1L);
    }
}
