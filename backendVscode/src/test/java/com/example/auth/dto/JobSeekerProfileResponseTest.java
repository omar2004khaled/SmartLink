package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.JobSeekerProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JobSeekerProfileResponseTest {

    private JobSeekerProfileResponse response;

    @BeforeEach
    void setUp() {
        response = new JobSeekerProfileResponse();
    }

    @Test
    void testSetAndGetProfileId() {
        Long profileId = 1L;
        response.setProfileId(profileId);
        assertEquals(profileId, response.getProfileId());
    }

    @Test
    void testSetAndGetProfilePicUrl() {
        String url = "https://example.com/pic.jpg";
        response.setProfilePicUrl(url);
        assertEquals(url, response.getProfilePicUrl());
    }

    @Test
    void testSetAndGetBio() {
        String bio = "Passionate developer";
        response.setBio(bio);
        assertEquals(bio, response.getBio());
    }

    @Test
    void testSetAndGetHeadline() {
        String headline = "Senior Developer";
        response.setHeadline(headline);
        assertEquals(headline, response.getHeadline());
    }

    @Test
    void testSetAndGetGender() {
        String gender = "Female";
        response.setGender(gender);
        assertEquals(gender, response.getGender());
    }

    @Test
    void testSetAndGetBirthDate() {
        LocalDate birthDate = LocalDate.of(1992, 3, 20);
        response.setBirthDate(birthDate);
        assertEquals(birthDate, response.getBirthDate());
    }

    @Test
    void testSetAndGetUserId() {
        Long userId = 5L;
        response.setUserId(userId);
        assertEquals(userId, response.getUserId());
    }

    @Test
    void testSetAndGetUserName() {
        String userName = "john_doe";
        response.setUserName(userName);
        assertEquals(userName, response.getUserName());
    }

    @Test
    void testSetAndGetUserEmail() {
        String email = "john@example.com";
        response.setUserEmail(email);
        assertEquals(email, response.getUserEmail());
    }

    @Test
    void testSetAndGetLocationId() {
        Long locationId = 2L;
        response.setLocationId(locationId);
        assertEquals(locationId, response.getLocationId());
    }

    @Test
    void testSetAndGetCountry() {
        String country = "USA";
        response.setCountry(country);
        assertEquals(country, response.getCountry());
    }

    @Test
    void testSetAndGetCity() {
        String city = "New York";
        response.setCity(city);
        assertEquals(city, response.getCity());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long profileId = 1L;
        String profilePicUrl = "https://example.com/pic.jpg";
        String bio = "Bio";
        String headline = "Headline";
        String gender = "Female";
        LocalDate birthDate = LocalDate.of(1992, 3, 20);
        Long userId = 5L;
        String userName = "jane_doe";
        String userEmail = "jane@example.com";
        Long locationId = 2L;
        String country = "USA";
        String city = "New York";

        response.setProfileId(profileId);
        response.setProfilePicUrl(profilePicUrl);
        response.setBio(bio);
        response.setHeadline(headline);
        response.setGender(gender);
        response.setBirthDate(birthDate);
        response.setUserId(userId);
        response.setUserName(userName);
        response.setUserEmail(userEmail);
        response.setLocationId(locationId);
        response.setCountry(country);
        response.setCity(city);

        assertEquals(profileId, response.getProfileId());
        assertEquals(profilePicUrl, response.getProfilePicUrl());
        assertEquals(bio, response.getBio());
        assertEquals(headline, response.getHeadline());
        assertEquals(gender, response.getGender());
        assertEquals(birthDate, response.getBirthDate());
        assertEquals(userId, response.getUserId());
        assertEquals(userName, response.getUserName());
        assertEquals(userEmail, response.getUserEmail());
        assertEquals(locationId, response.getLocationId());
        assertEquals(country, response.getCountry());
        assertEquals(city, response.getCity());
    }

    @Test
    void testNullValues() {
        assertNull(response.getProfileId());
        assertNull(response.getProfilePicUrl());
        assertNull(response.getBio());
        assertNull(response.getHeadline());
        assertNull(response.getGender());
        assertNull(response.getBirthDate());
        assertNull(response.getUserId());
        assertNull(response.getUserName());
        assertNull(response.getUserEmail());
        assertNull(response.getLocationId());
        assertNull(response.getCountry());
        assertNull(response.getCity());
    }
}
