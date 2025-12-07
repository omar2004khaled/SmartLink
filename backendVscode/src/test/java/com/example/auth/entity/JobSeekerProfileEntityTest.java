package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JobSeekerProfileEntityTest {

    private JobSeekerProfile profile;
    private com.example.auth.entity.ProfileEntities.Location location;

    @BeforeEach
    void setUp() {
        profile = new JobSeekerProfile();
        location = new com.example.auth.entity.ProfileEntities.Location(1L, "USA", "New York");
    }

    @Test
    void testSetAndGetProfileId() {
        Long id = 1L;
        profile.setProfileId(id);
        assertEquals(id, profile.getProfileId());
    }

    @Test
    void testSetAndGetProfilePicUrl() {
        String url = "https://example.com/pic.jpg";
        profile.setProfilePicUrl(url);
        assertEquals(url, profile.getProfilePicUrl());
    }

    @Test
    void testSetAndGetBio() {
        String bio = "Experienced developer";
        profile.setBio(bio);
        assertEquals(bio, profile.getBio());
    }

    @Test
    void testSetAndGetHeadline() {
        String headline = "Senior Developer";
        profile.setHeadline(headline);
        assertEquals(headline, profile.getHeadline());
    }

    @Test
    void testSetAndGetGender() {
        JobSeekerProfile.Gender gender = JobSeekerProfile.Gender.MALE;
        profile.setGender(gender);
        assertEquals(gender, profile.getGender());
    }

    @Test
    void testSetAndGetLocation() {
        profile.setLocation(location);
        assertEquals(location, profile.getLocation());
    }

    @Test
    void testSetAndGetBirthDate() {
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        profile.setBirthDate(birthDate);
        assertEquals(birthDate, profile.getBirthDate());
    }

    @Test
    void testSetAndGetEducations() {
        Set<Education> educations = new HashSet<>();
        Education edu = new Education();
        edu.setEducationId(1L);
        educations.add(edu);

        profile.setEducations(educations);
        assertEquals(educations, profile.getEducations());
        assertTrue(profile.getEducations().contains(edu));
    }

    @Test
    void testSetAndGetSkills() {
        Set<Skill> skills = new HashSet<>();
        Skill skill = new Skill();
        skill.setSkillId(1L);
        skills.add(skill);

        profile.setSkills(skills);
        assertEquals(skills, profile.getSkills());
        assertTrue(profile.getSkills().contains(skill));
    }

    @Test
    void testSetAndGetExperiences() {
        Set<Experience> experiences = new HashSet<>();
        Experience exp = new Experience();
        exp.setExperienceId(1L);
        experiences.add(exp);

        profile.setExperiences(experiences);
        assertEquals(experiences, profile.getExperiences());
        assertTrue(profile.getExperiences().contains(exp));
    }

    @Test
    void testSetAndGetProjects() {
        Set<Project> projects = new HashSet<>();
        Project proj = new Project();
        proj.setProjectId(1L);
        projects.add(proj);

        profile.setProjects(projects);
        assertEquals(projects, profile.getProjects());
        assertTrue(profile.getProjects().contains(proj));
    }

    @Test
    void testConstructorWithAllFields() {
        Set<Education> educations = new HashSet<>();
        Set<Skill> skills = new HashSet<>();
        Set<Experience> experiences = new HashSet<>();
        Set<Project> projects = new HashSet<>();

        JobSeekerProfile prof = new JobSeekerProfile(
                1L, "pic.jpg", "bio", "headline", JobSeekerProfile.Gender.FEMALE,
                location, LocalDate.of(1990, 1, 1), null, educations, skills, experiences, projects
        );

        assertEquals(1L, prof.getProfileId());
        assertEquals("pic.jpg", prof.getProfilePicUrl());
        assertEquals("bio", prof.getBio());
        assertEquals("headline", prof.getHeadline());
        assertEquals(JobSeekerProfile.Gender.FEMALE, prof.getGender());
        assertEquals(location, prof.getLocation());
        assertEquals(LocalDate.of(1990, 1, 1), prof.getBirthDate());
    }

    @Test
    void testDefaultConstructor() {
        JobSeekerProfile prof = new JobSeekerProfile();
        assertNull(prof.getProfileId());
        assertNull(prof.getProfilePicUrl());
        assertNull(prof.getBio());
        assertNull(prof.getHeadline());
        assertNull(prof.getGender());
        assertNull(prof.getLocation());
        assertNull(prof.getBirthDate());
        assertNotNull(prof.getEducations());
        assertNotNull(prof.getSkills());
        assertNotNull(prof.getExperiences());
        assertNotNull(prof.getProjects());
    }

    @Test
    void testInitialEmptyCollections() {
        JobSeekerProfile prof = new JobSeekerProfile();
        assertTrue(prof.getEducations().isEmpty());
        assertTrue(prof.getSkills().isEmpty());
        assertTrue(prof.getExperiences().isEmpty());
        assertTrue(prof.getProjects().isEmpty());
    }

    @Test
    void testGenderEnum() {
        assertEquals("MALE", JobSeekerProfile.Gender.MALE.toString());
        assertEquals("FEMALE", JobSeekerProfile.Gender.FEMALE.toString());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long profileId = 1L;
        String picUrl = "pic.jpg";
        String bio = "Bio";
        String headline = "Headline";
        JobSeekerProfile.Gender gender = JobSeekerProfile.Gender.MALE;
        LocalDate birthDate = LocalDate.of(1990, 1, 15);

        profile.setProfileId(profileId);
        profile.setProfilePicUrl(picUrl);
        profile.setBio(bio);
        profile.setHeadline(headline);
        profile.setGender(gender);
        profile.setLocation(location);
        profile.setBirthDate(birthDate);

        assertEquals(profileId, profile.getProfileId());
        assertEquals(picUrl, profile.getProfilePicUrl());
        assertEquals(bio, profile.getBio());
        assertEquals(headline, profile.getHeadline());
        assertEquals(gender, profile.getGender());
        assertEquals(location, profile.getLocation());
        assertEquals(birthDate, profile.getBirthDate());
    }
}
