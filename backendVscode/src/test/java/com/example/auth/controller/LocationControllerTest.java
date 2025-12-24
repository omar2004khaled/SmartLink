package com.example.auth.controller;

import com.example.auth.controller.ProfileControllers.LocationController;
import com.example.auth.dto.ProfileDtos.LocationDto;
import com.example.auth.service.ProfileServices.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto();
        locationDto.setLocationId(1L);
        locationDto.setCountry("United States");
        locationDto.setCity("San Francisco");
    }

    @Test
    void testList() {
        List<LocationDto> locations = new ArrayList<>();
        locations.add(locationDto);

        when(locationService.listAll()).thenReturn(locations);

        ResponseEntity<List<LocationDto>> response = locationController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("United States", response.getBody().get(0).getCountry());
        verify(locationService, times(1)).listAll();
    }

    @Test
    void testListEmpty() {
        when(locationService.listAll()).thenReturn(new ArrayList<>());

        ResponseEntity<List<LocationDto>> response = locationController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(locationService, times(1)).listAll();
    }

    @Test
    void testGet() {
        when(locationService.getById(1L)).thenReturn(locationDto);

        ResponseEntity<LocationDto> response = locationController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("United States", response.getBody().getCountry());
        verify(locationService, times(1)).getById(1L);
    }

    @Test
    void testCreate() {
        when(locationService.create(locationDto)).thenReturn(locationDto);

        ResponseEntity<LocationDto> response = locationController.create(locationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getLocationId());
        verify(locationService, times(1)).create(locationDto);
    }

    @Test
    void testUpdate() {
        when(locationService.update(1L, locationDto)).thenReturn(locationDto);

        ResponseEntity<LocationDto> response = locationController.update(1L, locationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("United States", response.getBody().getCountry());
        verify(locationService, times(1)).update(1L, locationDto);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = locationController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(locationService, times(1)).delete(1L);
    }
}
