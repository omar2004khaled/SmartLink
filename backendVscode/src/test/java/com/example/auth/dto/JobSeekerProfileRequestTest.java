package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.JobSeekerProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class JobSeekerProfileRequestTest {

    private JobSeekerProfileRequest request;

    @BeforeEach
    void setUp() {
        request = new JobSeekerProfileRequest();
    }

    @Test
    void testSetAndGetProfilePicUrl() {
        String url = "https://example.com/pic.jpg";
        request.setProfilePicUrl(url);
        assertEquals(url, request.getProfilePicUrl());
    }

    @Test
    void testSetAndGetBio() {
        String bio = "Passionate software developer";
        request.setBio(bio);
        assertEquals(bio, request.getBio());
    }

    @Test
    void testSetAndGetHeadline() {
        String headline = "Senior Full Stack Developer";
        request.setHeadline(headline);
        assertEquals(headline, request.getHeadline());
    }

    @Test
    void testSetAndGetGender() {
        String gender = "Male";
        request.setGender(gender);
        assertEquals(gender, request.getGender());
    }

    @Test
    void testSetAndGetBirthDate() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        request.setBirthDate(birthDate);
        assertEquals(birthDate, request.getBirthDate());
    }

    @Test
    void testSetAndGetUserId() {
        Long userId = 1L;
        request.setUserId(userId);
        assertEquals(userId, request.getUserId());
    }

    @Test
    void testSetAndGetLocationId() {
        Long locationId = 1L;
        request.setLocationId(locationId);
        assertEquals(locationId, request.getLocationId());
    }

    @Test
    void testAllFieldsAtOnce() {
        String profilePicUrl = "https://example.com/pic.jpg";
        String bio = "Bio";
        String headline = "Headline";
        String gender = "Male";
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Long userId = 1L;
        Long locationId = 1L;

        request.setProfilePicUrl(profilePicUrl);
        request.setBio(bio);
        request.setHeadline(headline);
        request.setGender(gender);
        request.setBirthDate(birthDate);
        request.setUserId(userId);
        request.setLocationId(locationId);

        assertEquals(profilePicUrl, request.getProfilePicUrl());
        assertEquals(bio, request.getBio());
        assertEquals(headline, request.getHeadline());
        assertEquals(gender, request.getGender());
        assertEquals(birthDate, request.getBirthDate());
        assertEquals(userId, request.getUserId());
        assertEquals(locationId, request.getLocationId());
    }

    @Test
    void testNullValues() {
        assertNull(request.getProfilePicUrl());
        assertNull(request.getBio());
        assertNull(request.getHeadline());
        assertNull(request.getGender());
        assertNull(request.getBirthDate());
        assertNull(request.getUserId());
        assertNull(request.getLocationId());
    }
}
