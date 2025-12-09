package com.example.auth.dto.CompanyTests;

import com.example.auth.dto.CompanyDTO;
import com.example.auth.dto.LocationDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyDTOTest {
    @Test
    void testCompanyDTOCreation() {
        CompanyDTO dto = new CompanyDTO();
        dto.setCompanyProfileId(1L);
        dto.setUserId(100L);
        dto.setCompanyName("Test Company");
        dto.setWebsite("https://test.com");
        dto.setIndustry("Technology");
        dto.setDescription("Test Description");
        dto.setLogoUrl("logo.png");
        dto.setCoverUrl("cover.png");
        dto.setNumberOfFollowers(10L);
        dto.setFounded(2020);
        dto.setIsFollowing(true);

        assertEquals(1L, dto.getCompanyProfileId());
        assertEquals(100L, dto.getUserId());
        assertEquals("Test Company", dto.getCompanyName());
        assertEquals("https://test.com", dto.getWebsite());
        assertEquals("Technology", dto.getIndustry());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("logo.png", dto.getLogoUrl());
        assertEquals("cover.png", dto.getCoverUrl());
        assertEquals(10L, dto.getNumberOfFollowers());
        assertEquals(2020, dto.getFounded());
        assertTrue(dto.getIsFollowing());
    }

    @Test
    void testCompanyDTOWithLocations() {
        LocationDTO location1 = new LocationDTO(1L, "Cairo", "Egypt");
        LocationDTO location2 = new LocationDTO(2L, "Alexandria", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location1, location2);

        CompanyDTO dto = new CompanyDTO();
        dto.setLocations(locations);

        assertNotNull(dto.getLocations());
        assertEquals(2, dto.getLocations().size());
        assertEquals("Cairo", dto.getLocations().get(0).getCity());
        assertEquals("Alexandria", dto.getLocations().get(1).getCity());
    }

    @Test
    void testCompanyDTOAllArgsConstructor() {
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location);

        CompanyDTO dto = new CompanyDTO(
                1L, 100L, "Test Company", "https://test.com", "Technology",
                "Description", "logo.png", "cover.png", 10L, 2020,
                locations, true
        );

        assertEquals(1L, dto.getCompanyProfileId());
        assertEquals(100L, dto.getUserId());
        assertEquals("Test Company", dto.getCompanyName());
        assertEquals(1, dto.getLocations().size());
        assertTrue(dto.getIsFollowing());
    }

    @Test
    void testCompanyDTONoArgsConstructor() {
        CompanyDTO dto = new CompanyDTO();

        assertNull(dto.getCompanyProfileId());
        assertNull(dto.getUserId());
        assertNull(dto.getCompanyName());
        assertNull(dto.getIsFollowing());
    }
}