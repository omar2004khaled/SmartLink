package com.example.auth.entity;

import com.example.auth.entity.ProfileEntities.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class LocationEntityTest {

    private Location location;

    @BeforeEach
    void setUp() {
        location = new Location();
    }

    @Test
    void testSetAndGetLocationId() {
        Long id = 1L;
        location.setLocationId(id);
        assertEquals(id, location.getLocationId());
    }

    @Test
    void testSetAndGetCountry() {
        String country = "Canada";
        location.setCountry(country);
        assertEquals(country, location.getCountry());
    }

    @Test
    void testSetAndGetCity() {
        String city = "Toronto";
        location.setCity(city);
        assertEquals(city, location.getCity());
    }

    @Test
    void testConstructorWithAllFields() {
        Long id = 1L;
        String country = "Canada";
        String city = "Toronto";

        location = new Location(id, country, city);

        assertEquals(id, location.getLocationId());
        assertEquals(country, location.getCountry());
        assertEquals(city, location.getCity());
    }

    @Test
    void testDefaultConstructor() {
        Location loc = new Location();
        assertNull(loc.getLocationId());
        assertNull(loc.getCountry());
        assertNull(loc.getCity());
    }

    @Test
    void testAllFieldsAtOnce() {
        Long id = 1L;
        String country = "United States";
        String city = "New York";

        location.setLocationId(id);
        location.setCountry(country);
        location.setCity(city);

        assertEquals(id, location.getLocationId());
        assertEquals(country, location.getCountry());
        assertEquals(city, location.getCity());
    }

    @Test
    void testMultipleLocations() {
        Location loc1 = new Location(1L, "USA", "New York");
        Location loc2 = new Location(2L, "Canada", "Toronto");

        assertEquals(1L, loc1.getLocationId());
        assertEquals("USA", loc1.getCountry());
        assertEquals("New York", loc1.getCity());

        assertEquals(2L, loc2.getLocationId());
        assertEquals("Canada", loc2.getCountry());
        assertEquals("Toronto", loc2.getCity());
    }

    @Test
    void testLocationWithoutCity() {
        location.setLocationId(1L);
        location.setCountry("France");

        assertEquals(1L, location.getLocationId());
        assertEquals("France", location.getCountry());
        assertNull(location.getCity());
    }
}
