package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class SkillDtoTest {

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        skillDto.setId(id);
        assertEquals(id, skillDto.getId());
    }

    @Test
    void testSetAndGetSkillName() {
        String skillName = "Java";
        skillDto.setSkillName(skillName);
        assertEquals(skillName, skillDto.getSkillName());
    }

    @Test
    void testSetAndGetProficiency() {
        String proficiency = "Expert";
        skillDto.setProficiency(proficiency);
        assertEquals(proficiency, skillDto.getProficiency());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String skillName = "Python";
        String proficiency = "Advanced";

        skillDto.setId(id);
        skillDto.setSkillName(skillName);
        skillDto.setProficiency(proficiency);

        assertEquals(id, skillDto.getId());
        assertEquals(skillName, skillDto.getSkillName());
        assertEquals(proficiency, skillDto.getProficiency());
    }

    @Test
    void testNullValues() {
        assertNull(skillDto.getId());
        assertNull(skillDto.getSkillName());
        assertNull(skillDto.getProficiency());
    }

    @Test
    void testMultipleSkillsWithDifferentProficiencies() {
        skillDto.setId(1L);
        skillDto.setSkillName("Java");
        skillDto.setProficiency("Expert");

        assertEquals(1L, skillDto.getId());
        assertEquals("Java", skillDto.getSkillName());
        assertEquals("Expert", skillDto.getProficiency());

        // Test updating with different skill
        skillDto.setId(2L);
        skillDto.setSkillName("Python");
        skillDto.setProficiency("Intermediate");

        assertEquals(2L, skillDto.getId());
        assertEquals("Python", skillDto.getSkillName());
        assertEquals("Intermediate", skillDto.getProficiency());
    }
}
