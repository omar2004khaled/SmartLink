package com.example.auth.entity.CompanyTest;

import com.example.auth.entity.CompanyProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class CompanyProfileTest {

    @Test
    void testCompanyProfileCreation() {
        CompanyProfile profile = new CompanyProfile();
        profile.setCompanyProfileId(1L);
        profile.setUserId(100L);
        profile.setCompanyName("Test Company");
        profile.setWebsite("https://facebook.com");
        profile.setIndustry("Technology");
        profile.setDescription("Test Description");
        profile.setLogoUrl("testlogo.png");
        profile.setCoverImageUrl("testcover.png");
        profile.setFounded(2020);

        assertEquals(1L, profile.getCompanyProfileId());
        assertEquals(100L, profile.getUserId());
        assertEquals("Test Company", profile.getCompanyName());
        assertEquals("https://facebook.com", profile.getWebsite());
        assertEquals("Technology", profile.getIndustry());
        assertEquals("Test Description", profile.getDescription());
        assertEquals("testlogo.png", profile.getLogoUrl());
        assertEquals("testcover.png", profile.getCoverImageUrl());
        assertEquals(2020, profile.getFounded());
    }

    @Test
    void testOnCreate() {
        CompanyProfile profile = new CompanyProfile();
        profile.onCreate();

        assertNotNull(profile.getCreatedAt());
        assertNotNull(profile.getUpdatedAt());

        // Allow small tolerance (1 millisecond) due to nanosecond precision differences
        long millisDiff = ChronoUnit.MILLIS.between(profile.getCreatedAt(), profile.getUpdatedAt());
        assertTrue(millisDiff >= 0 && millisDiff <= 1,
                "createdAt and updatedAt should be equal or differ by at most 1 ms");
    }

    @Test
    void testOnUpdate() throws InterruptedException {
        CompanyProfile profile = new CompanyProfile();
        profile.onCreate();

        LocalDateTime createdAt = profile.getCreatedAt();
        Thread.sleep(10);

        profile.onUpdate();

        assertEquals(createdAt, profile.getCreatedAt());
        assertNotEquals(createdAt, profile.getUpdatedAt());
        assertTrue(profile.getUpdatedAt().isAfter(createdAt));
    }
}
