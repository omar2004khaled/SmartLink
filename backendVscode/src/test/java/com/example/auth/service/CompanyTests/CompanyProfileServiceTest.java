package com.example.auth.service.CompanyTests;

import com.example.auth.dto.CompanyDTO;
import com.example.auth.dto.CompanyUpdateDTO;
import com.example.auth.dto.LocationDTO;
import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.CompanyLocation;
import com.example.auth.entity.CompanyProfile;
import com.example.auth.entity.Location;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.CompanyLocationRepo;
import com.example.auth.repository.CompanyProfileRepo;
import com.example.auth.repository.LocationRepo;
import com.example.auth.service.CompanyProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceTest {

    @Mock
    private CompanyProfileRepo companyProfileRepo;

    @Mock
    private CompanyFollowerRepo companyFollowerRepo;

    @Mock
    private CompanyLocationRepo companyLocationRepo;

    @Mock
    private LocationRepo locationRepo;

    @InjectMocks
    private CompanyProfileService companyProfileService;

    private CompanyProfile testCompany;
    private Location testLocation;
    private CompanyLocation testCompanyLocation;

    @BeforeEach
    void setUp() {
        testCompany = new CompanyProfile();
        testCompany.setCompanyProfileId(1L);
        testCompany.setUserId(100L);
        testCompany.setCompanyName("Test Company");
        testCompany.setWebsite("https://test.com");
        testCompany.setIndustry("Technology");
        testCompany.setDescription("Test Description");
        testCompany.setLogoUrl("logo.png");
        testCompany.setCoverImageUrl("cover.png");
        testCompany.setNumberOfFollowers(10L);
        testCompany.setFounded(2020);
        testCompany.setCreatedAt(LocalDateTime.now());
        testCompany.setUpdatedAt(LocalDateTime.now());

        testLocation = new Location();
        testLocation.setLocationId(1L);
        testLocation.setCity("Cairo");
        testLocation.setCountry("Egypt");

        testCompanyLocation = new CompanyLocation();
        testCompanyLocation.setCompanyId(100L);
        testCompanyLocation.setLocationId(1L);
    }







    @Test
    void getCompanyProfile_Success() {
        Long companyId = 1L;
        Long userId = 200L;
        testCompany.setCompanyProfileId(companyId);
        testCompany.setUserId(100L);

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, companyId)).thenReturn(true);
        when(companyLocationRepo.findByCompanyId(companyId)).thenReturn(Arrays.asList(testCompanyLocation));
        when(locationRepo.findById(1L)).thenReturn(Optional.of(testLocation));

        CompanyDTO result = companyProfileService.getCompanyProfile(companyId, userId);

        assertNotNull(result);
        assertEquals("Test Company", result.getCompanyName());
        assertEquals(100L, result.getUserId());
        assertEquals(true, result.getIsFollowing());
        assertEquals(1, result.getLocations().size());
        verify(companyProfileRepo).findById(companyId);
    }

    @Test
    void getCompanyProfile_CompanyNotFound() {
        Long companyId = 999L;
        Long userId = 2L;
        
        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            companyProfileService.getCompanyProfile(companyId, userId);
        });
        
        verify(companyProfileRepo).findById(companyId);
    }

    @Test
    void updateCompanyProfile_Success() {
        Long companyId = 1L;
        testCompany.setCompanyProfileId(companyId);
        testCompany.setUserId(companyId); // Make userId consistent
        
        CompanyUpdateDTO updateDTO = new CompanyUpdateDTO();
        updateDTO.setCompanyName("Updated Company");
        updateDTO.setDescription("Updated Description");
        updateDTO.setWebsite("https://updated.com");
        updateDTO.setIndustry("Finance");
        updateDTO.setFounded(2021);
        updateDTO.setLogoUrl("new-logo.png");
        updateDTO.setCoverImageUrl("new-cover.png");

        List<LocationDTO> locations = Arrays.asList(
                new LocationDTO(null, "Alexandria", "Egypt")
        );
        updateDTO.setLocations(locations);

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyProfileRepo.save(any(CompanyProfile.class))).thenReturn(testCompany);
        when(companyLocationRepo.findByCompanyId(companyId)).thenReturn(new ArrayList<>());
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(eq(companyId), eq(companyId))).thenReturn(false);
        when(locationRepo.findByCityAndCountry("Alexandria", "Egypt")).thenReturn(Optional.empty());
        when(locationRepo.save(any(Location.class))).thenReturn(testLocation);

        CompanyDTO result = companyProfileService.updateCompanyProfile(companyId, updateDTO);

        assertNotNull(result);
        verify(companyProfileRepo).save(any(CompanyProfile.class));
        verify(companyLocationRepo).deleteByCompanyId(companyId);
        verify(locationRepo).save(any(Location.class));
        verify(companyLocationRepo).save(any(CompanyLocation.class));
    }

    @Test
    void updateCompanyProfile_CompanyNotFound() {
        when(companyProfileRepo.findById(anyLong())).thenReturn(Optional.empty());

        CompanyUpdateDTO updateDTO = new CompanyUpdateDTO();
        assertThrows(RuntimeException.class, () -> {
            companyProfileService.updateCompanyProfile(1L, updateDTO);
        });
    }

    @Test
    void updateCompanyLocations_NewLocation() {
        Long companyId = 100L;
        LocationDTO locationDTO = new LocationDTO(null, "Giza", "Egypt");
        List<LocationDTO> locations = Arrays.asList(locationDTO);

        Location newLocation = new Location(2L, "Egypt", "Giza");
        when(locationRepo.findByCityAndCountry("Giza", "Egypt")).thenReturn(Optional.empty());
        when(locationRepo.save(any(Location.class))).thenReturn(newLocation);

        companyProfileService.updateCompanyLocations(companyId, locations);

        verify(companyLocationRepo).deleteByCompanyId(companyId);
        verify(locationRepo).save(any(Location.class));
        verify(companyLocationRepo).save(any(CompanyLocation.class));
    }

    @Test
    void updateCompanyLocations_ExistingLocation() {
        Long companyId = 100L;
        LocationDTO locationDTO = new LocationDTO(1L, "Cairo", "Egypt");
        List<LocationDTO> locations = Arrays.asList(locationDTO);

        when(locationRepo.findByCityAndCountry("Cairo", "Egypt")).thenReturn(Optional.of(testLocation));

        companyProfileService.updateCompanyLocations(companyId, locations);

        verify(companyLocationRepo).deleteByCompanyId(companyId);
        verify(locationRepo, never()).save(any(Location.class));
        verify(companyLocationRepo).save(any(CompanyLocation.class));
    }

    @Test
    void updateCompanyLocations_EmptyList() {
        Long companyId = 100L;
        List<LocationDTO> locations = new ArrayList<>();

        companyProfileService.updateCompanyLocations(companyId, locations);

        verify(companyLocationRepo).deleteByCompanyId(companyId);
        verify(locationRepo, never()).save(any(Location.class));
        verify(companyLocationRepo, never()).save(any(CompanyLocation.class));
    }

    @Test
    void updateCompanyLocations_NullList() {
        Long companyId = 100L;

        companyProfileService.updateCompanyLocations(companyId, null);

        verify(companyLocationRepo).deleteByCompanyId(companyId);
        verify(locationRepo, never()).save(any(Location.class));
        verify(companyLocationRepo, never()).save(any(CompanyLocation.class));
    }

    @Test
    void followCompany_Success() {
        Long companyId = 1L;
        Long userId = 200L;

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, testCompany.getUserId())).thenReturn(false);
        when(companyFollowerRepo.save(any(CompanyFollower.class))).thenReturn(new CompanyFollower());

        companyProfileService.followCompany(companyId, userId);

        verify(companyFollowerRepo).save(any(CompanyFollower.class));
        verify(companyProfileRepo).save(testCompany);
        assertEquals(11L, testCompany.getNumberOfFollowers());
    }

    @Test
    void followCompany_AlreadyFollowing() {
        Long companyId = 1L;
        Long userId = 200L;

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, testCompany.getUserId())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            companyProfileService.followCompany(companyId, userId);
        });

        verify(companyFollowerRepo, never()).save(any(CompanyFollower.class));
    }

    @Test
    void followCompany_CompanyNotFound() {
        when(companyProfileRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            companyProfileService.followCompany(1L, 2L);
        });
    }

    @Test
    void unfollowCompany_Success() {
        Long companyId = 1L;
        Long userId = 200L;

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, testCompany.getUserId())).thenReturn(true);

        companyProfileService.unfollowCompany(companyId, userId);

        verify(companyFollowerRepo).deleteByFollowerIdAndCompanyId(userId, testCompany.getUserId());
        verify(companyProfileRepo).save(testCompany);
        assertEquals(9L, testCompany.getNumberOfFollowers());
    }

    @Test
    void unfollowCompany_NotFollowing() {
        Long companyId = 1L;
        Long userId = 200L;

        when(companyProfileRepo.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, testCompany.getUserId())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            companyProfileService.unfollowCompany(companyId, userId);
        });

        verify(companyFollowerRepo, never()).deleteByFollowerIdAndCompanyId(anyLong(), anyLong());
    }

    @Test
    void unfollowCompany_CompanyNotFound() {
        when(companyProfileRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            companyProfileService.unfollowCompany(1L, 2L);
        });
    }
}