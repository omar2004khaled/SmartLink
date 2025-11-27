package com.example.auth.controller;

import com.example.auth.controller.ProfileControllers.SkillController;
import com.example.auth.dto.ProfileDtos.SkillDto;
import com.example.auth.service.ProfileServices.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setSkillName("Java");
        skillDto.setProficiency("Expert");
    }

    @Test
    void testList() {
        List<SkillDto> skills = new ArrayList<>();
        skills.add(skillDto);

        when(skillService.getSkillsForProfile(1L)).thenReturn(skills);

        List<SkillDto> result = skillController.list(1L);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getSkillName());
        verify(skillService, times(1)).getSkillsForProfile(1L);
    }

    @Test
    void testListEmpty() {
        when(skillService.getSkillsForProfile(1L)).thenReturn(new ArrayList<>());

        List<SkillDto> result = skillController.list(1L);

        assertTrue(result.isEmpty());
        verify(skillService, times(1)).getSkillsForProfile(1L);
    }

    @Test
    void testAdd() {
        when(skillService.addSkillToProfile(1L, skillDto)).thenReturn(skillDto);

        ResponseEntity<SkillDto> response = skillController.add(1L, skillDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java", response.getBody().getSkillName());
        verify(skillService, times(1)).addSkillToProfile(1L, skillDto);
    }

    @Test
    void testUpdate() {
        when(skillService.updateSkill(1L, 1L, skillDto)).thenReturn(skillDto);

        ResponseEntity<SkillDto> response = skillController.update(1L, 1L, skillDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java", response.getBody().getSkillName());
        verify(skillService, times(1)).updateSkill(1L, 1L, skillDto);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = skillController.delete(1L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(skillService, times(1)).deleteSkill(1L, 1L);
    }
}
