package com.example.auth.service.ProfileServices;

import com.example.auth.dto.ProfileDtos.LocationDto;
import com.example.auth.entity.ProfileEntities.Location;
import com.example.auth.repository.ProfileRepositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepo;

    public LocationService(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Transactional(readOnly = true)
    public List<LocationDto> listAll() {
        return locationRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationDto getById(Long id) {
        Location loc = locationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id " + id));
        return toDto(loc);
    }

    @Transactional
    public LocationDto create(LocationDto dto) {
        Location loc = new Location();
        applyDtoToEntity(dto, loc);
        Location saved = locationRepo.save(loc);
        return toDto(saved);
    }

    @Transactional
    public LocationDto update(Long id, LocationDto dto) {
        Location loc = locationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id " + id));
        if (dto.getCountry() != null) 
            loc.setCountry(dto.getCountry());
        if (dto.getCity() != null) 
            loc.setCity(dto.getCity());
        Location saved = locationRepo.save(loc);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Location loc = locationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id " + id));
        locationRepo.delete(loc);
    }


    private LocationDto toDto(Location loc) {
        LocationDto dto = new LocationDto();
        dto.setLocationId(loc.getLocationId());
        dto.setCountry(loc.getCountry());
        dto.setCity(loc.getCity());
        return dto;
    }

    private void applyDtoToEntity(LocationDto dto, Location loc) {
        if (dto.getCountry() != null) loc.setCountry(dto.getCountry());
        if (dto.getCity() != null) loc.setCity(dto.getCity());
    }
}
