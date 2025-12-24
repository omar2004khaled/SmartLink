package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.Experience;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class ExperienceEntityTest {

    private Experience experience;
    private JobSeekerProfile profile;

    @BeforeEach
    void setUp() {
        experience = new Experience();
        profile = new JobSeekerProfile();
    }

    @Test
    void testSetAndGetExperienceId() {
        Long id = 1L;
        experience.setExperienceId(id);
        assertEquals(id, experience.getExperienceId());
    }

    @Test
    void testSetAndGetProfile() {
        experience.setProfile(profile);
        assertEquals(profile, experience.getProfile());
    }

    @Test
    void testSetAndGetCompanyName() {
        String company = "Microsoft";
        experience.setCompanyName(company);
        assertEquals(company, experience.getCompanyName());
    }

    @Test
    void testSetAndGetTitle() {
        String title = "Lead Developer";
        experience.setTitle(title);
        assertEquals(title, experience.getTitle());
    }

    @Test
    void testSetAndGetLocation() {
        String location = "Seattle, WA";
        experience.setLocation(location);
        assertEquals(location, experience.getLocation());
    }

    @Test
    void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        experience.setStartDate(startDate);
        assertEquals(startDate, experience.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        experience.setEndDate(endDate);
        assertEquals(endDate, experience.getEndDate());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "Led development team";
        experience.setDescription(description);
        assertEquals(description, experience.getDescription());
    }

    @Test
    void testConstructorWithAllFields() {
        Long id = 1L;
        String company = "Microsoft";
        String title = "Developer";
        String location = "Seattle";
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String description = "Role";

        experience = new Experience(id, profile, company, title, location, startDate, endDate, description);

        assertEquals(id, experience.getExperienceId());
        assertEquals(profile, experience.getProfile());
        assertEquals(company, experience.getCompanyName());
        assertEquals(title, experience.getTitle());
        assertEquals(location, experience.getLocation());
        assertEquals(startDate, experience.getStartDate());
        assertEquals(endDate, experience.getEndDate());
        assertEquals(description, experience.getDescription());
    }

    @Test
    void testDefaultConstructor() {
        Experience exp = new Experience();
        assertNull(exp.getExperienceId());
        assertNull(exp.getProfile());
        assertNull(exp.getCompanyName());
        assertNull(exp.getTitle());
        assertNull(exp.getLocation());
        assertNull(exp.getStartDate());
        assertNull(exp.getEndDate());
        assertNull(exp.getDescription());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String company = "Google";
        String title = "Engineer";
        String location = "CA";
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String description = "Role";

        experience.setExperienceId(id);
        experience.setProfile(profile);
        experience.setCompanyName(company);
        experience.setTitle(title);
        experience.setLocation(location);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.setDescription(description);

        assertEquals(id, experience.getExperienceId());
        assertEquals(profile, experience.getProfile());
        assertEquals(company, experience.getCompanyName());
        assertEquals(title, experience.getTitle());
        assertEquals(location, experience.getLocation());
        assertEquals(startDate, experience.getStartDate());
        assertEquals(endDate, experience.getEndDate());
        assertEquals(description, experience.getDescription());
    }
}
