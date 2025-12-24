package com.example.auth.dto;

import com.example.auth.dto.ProfileDtos.LocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class LocationDtoTest {

    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto();
    }

    @Test
    void testSetAndGetLocationId() {
        Long id = 1L;
        locationDto.setLocationId(id);
        assertEquals(id, locationDto.getLocationId());
    }

    @Test
    void testSetAndGetCountry() {
        String country = "United States";
        locationDto.setCountry(country);
        assertEquals(country, locationDto.getCountry());
    }

    @Test
    void testSetAndGetCity() {
        String city = "San Francisco";
        locationDto.setCity(city);
        assertEquals(city, locationDto.getCity());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String country = "United States";
        String city = "San Francisco";

        locationDto.setLocationId(id);
        locationDto.setCountry(country);
        locationDto.setCity(city);

        assertEquals(id, locationDto.getLocationId());
        assertEquals(country, locationDto.getCountry());
        assertEquals(city, locationDto.getCity());
    }

    @Test
    void testNullValues() {
        assertNull(locationDto.getLocationId());
        assertNull(locationDto.getCountry());
        assertNull(locationDto.getCity());
    }

    @Test
    void testWithDifferentCountriesAndCities() {
        locationDto.setLocationId(1L);
        locationDto.setCountry("Egypt");
        locationDto.setCity("Cairo");

        assertEquals(1L, locationDto.getLocationId());
        assertEquals("Egypt", locationDto.getCountry());
        assertEquals("Cairo", locationDto.getCity());

        // Test with different values
        locationDto.setLocationId(2L);
        locationDto.setCountry("United Kingdom");
        locationDto.setCity("London");

        assertEquals(2L, locationDto.getLocationId());
        assertEquals("United Kingdom", locationDto.getCountry());
        assertEquals("London", locationDto.getCity());
    }
}
