package com.example.auth.dto.CompanyTests;

import com.example.auth.dto.AboutCompanyDTO;
import com.example.auth.dto.LocationDTO;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AboutCompanyDTOTest {

    @Test
    void testAboutCompanyDTOCreation() {
        AboutCompanyDTO dto = new AboutCompanyDTO();
        dto.setDescription("Test Description");
        dto.setWebsite("https://test.com");
        dto.setIndustry("Technology");
        dto.setFounded(2020);

        assertEquals("Test Description", dto.getDescription());
        assertEquals("https://test.com", dto.getWebsite());
        assertEquals("Technology", dto.getIndustry());
        assertEquals(2020, dto.getFounded());
    }

    @Test
    void testAboutCompanyDTOWithLocations() {
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location);

        AboutCompanyDTO dto = new AboutCompanyDTO();
        dto.setLocations(locations);

        assertNotNull(dto.getLocations());
        assertEquals(1, dto.getLocations().size());
        assertEquals("Cairo", dto.getLocations().get(0).getCity());
    }

    @Test
    void testAboutCompanyDTOAllArgsConstructor() {
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(location);

        AboutCompanyDTO dto = new AboutCompanyDTO(
                "Description",
                "https://test.com",
                "Technology",
                2020,
                locations
        );

        assertEquals("Description", dto.getDescription());
        assertEquals(1, dto.getLocations().size());
    }

    @Test
    void testAboutCompanyDTONoArgsConstructor() {
        AboutCompanyDTO dto = new AboutCompanyDTO();

        assertNull(dto.getDescription());
        assertNull(dto.getWebsite());
        assertNull(dto.getIndustry());
        assertNull(dto.getFounded());
        assertNull(dto.getLocations());
    }
}