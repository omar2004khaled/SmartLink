package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.Skill;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SkillEntityTest {

    private Skill skill;
    private JobSeekerProfile profile;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        profile = new JobSeekerProfile();
    }

    @Test
    void testSetAndGetSkillId() {
        Long id = 1L;
        skill.setSkillId(id);
        assertEquals(id, skill.getSkillId());
    }

    @Test
    void testSetAndGetProfile() {
        skill.setProfile(profile);
        assertEquals(profile, skill.getProfile());
    }

    @Test
    void testSetAndGetSkillName() {
        String skillName = "Spring Boot";
        skill.setSkillName(skillName);
        assertEquals(skillName, skill.getSkillName());
    }

    @Test
    void testSetAndGetProficiency() {
        String proficiency = "Expert";
        skill.setProficiency(proficiency);
        assertEquals(proficiency, skill.getProficiency());
    }

    @Test
    void testConstructorWithAllFields() {
        Long id = 1L;
        String skillName = "Java";
        String proficiency = "Expert";

        skill = new Skill(id, profile, skillName, proficiency);

        assertEquals(id, skill.getSkillId());
        assertEquals(profile, skill.getProfile());
        assertEquals(skillName, skill.getSkillName());
        assertEquals(proficiency, skill.getProficiency());
    }

    @Test
    void testDefaultConstructor() {
        Skill s = new Skill();
        assertNull(s.getSkillId());
        assertNull(s.getProfile());
        assertNull(s.getSkillName());
        assertNull(s.getProficiency());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String skillName = "Python";
        String proficiency = "Advanced";

        skill.setSkillId(id);
        skill.setProfile(profile);
        skill.setSkillName(skillName);
        skill.setProficiency(proficiency);

        assertEquals(id, skill.getSkillId());
        assertEquals(profile, skill.getProfile());
        assertEquals(skillName, skill.getSkillName());
        assertEquals(proficiency, skill.getProficiency());
    }

    @Test
    void testMultipleSkills() {
        Skill skill1 = new Skill(1L, profile, "Java", "Expert");
        Skill skill2 = new Skill(2L, profile, "Python", "Intermediate");

        assertEquals(1L, skill1.getSkillId());
        assertEquals("Java", skill1.getSkillName());
        assertEquals("Expert", skill1.getProficiency());

        assertEquals(2L, skill2.getSkillId());
        assertEquals("Python", skill2.getSkillName());
        assertEquals("Intermediate", skill2.getProficiency());
    }

    @Test
    void testSkillWithoutProficiency() {
        skill.setSkillId(1L);
        skill.setSkillName("JavaScript");
        skill.setProfile(profile);

        assertEquals(1L, skill.getSkillId());
        assertEquals("JavaScript", skill.getSkillName());
        assertNull(skill.getProficiency());
    }
}
