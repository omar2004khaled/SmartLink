package com.example.auth.dto.CompanyTests;

import com.example.auth.dto.CompanyUpdateDTO;
import com.example.auth.dto.LocationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CompanyUpdateDTOTest {

    @Test
    void testCompanyUpdateDTOCreation() {
        CompanyUpdateDTO dto = new CompanyUpdateDTO();
        dto.setCompanyId(1L);
        dto.setCompanyName("Updated Company");
        dto.setDescription("Updated Description");
        dto.setWebsite("https://updated.com");
        dto.setIndustry("Finance");
        dto.setFounded(2021);
        dto.setLogoUrl("new-logo.png");
        dto.setCoverImageUrl("new-cover.png");

        assertEquals(1L, dto.getCompanyId());
        assertEquals("Updated Company", dto.getCompanyName());
        assertEquals("Updated Description", dto.getDescription());
        assertEquals("https://updated.com", dto.getWebsite());
        assertEquals("Finance", dto.getIndustry());
        assertEquals(2021, dto.getFounded());
        assertEquals("new-logo.png", dto.getLogoUrl());
        assertEquals("new-cover.png", dto.getCoverImageUrl());
    }

    @Test
    void testCompanyUpdateDTOWithLocations() {
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location);

        CompanyUpdateDTO dto = new CompanyUpdateDTO();
        dto.setLocations(locations);

        assertNotNull(dto.getLocations());
        assertEquals(1, dto.getLocations().size());
    }

    @Test
    void testCompanyUpdateDTOAllArgsConstructor() {
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location);

        CompanyUpdateDTO dto = new CompanyUpdateDTO(
                1L, "Test Company", "Description", "https://test.com",
                "Technology", 2020, "logo.png", "cover.png", locations
        );

        assertEquals(1L, dto.getCompanyId());
        assertEquals("Test Company", dto.getCompanyName());
        assertEquals(1, dto.getLocations().size());
    }

    @Test
    void testCompanyUpdateDTOPartialUpdate() {
        CompanyUpdateDTO dto = new CompanyUpdateDTO();
        dto.setCompanyName("New Name");

        assertEquals("New Name", dto.getCompanyName());
        assertNull(dto.getDescription());
        assertNull(dto.getWebsite());
    }
}
