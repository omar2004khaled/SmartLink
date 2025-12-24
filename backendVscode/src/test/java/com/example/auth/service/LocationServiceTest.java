package com.example.auth.service;

import com.example.auth.dto.ProfileDtos.LocationDto;
import com.example.auth.entity.ProfileEntities.Location;
import com.example.auth.repository.ProfileRepositories.LocationRepository;
import com.example.auth.service.ProfileServices.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setLocationId(1L);
        location.setCountry("United States");
        location.setCity("San Francisco");

        locationDto = new LocationDto();
        locationDto.setLocationId(1L);
        locationDto.setCountry("United States");
        locationDto.setCity("San Francisco");
    }

    @Test
    void testListAll() {
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        when(locationRepository.findAll()).thenReturn(locations);

        List<LocationDto> result = locationService.listAll();

        assertEquals(1, result.size());
        assertEquals("United States", result.get(0).getCountry());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void testListAllEmpty() {
        when(locationRepository.findAll()).thenReturn(new ArrayList<>());

        List<LocationDto> result = locationService.listAll();

        assertTrue(result.isEmpty());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationDto result = locationService.getById(1L);

        assertNotNull(result);
        assertEquals("United States", result.getCountry());
        assertEquals("San Francisco", result.getCity());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> locationService.getById(1L));
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDto result = locationService.create(locationDto);

        assertNotNull(result);
        assertEquals(1L, result.getLocationId());
        assertEquals("United States", result.getCountry());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void testUpdate() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDto updateDto = new LocationDto();
        updateDto.setCountry("Canada");
        updateDto.setCity("Toronto");

        LocationDto result = locationService.update(1L, updateDto);

        assertNotNull(result);
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void testUpdateNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> locationService.update(1L, locationDto));
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        locationService.delete(1L);

        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    void testDeleteNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> locationService.delete(1L));
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, never()).delete(any());
    }
}
