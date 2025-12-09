package com.example.auth.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepo companyProfileRepo;
    private final CompanyFollowerRepo companyFollowerRepo;
    private final CompanyLocationRepo companyLocationRepo;
    private final LocationRepo locationRepo;


    private List<LocationDTO> getLocations(Long companyId){
        List<CompanyLocation> locations = companyLocationRepo.findByCompanyId(companyId);
        List<LocationDTO> locationDTOS = new ArrayList<>();
        for(CompanyLocation location : locations){
            final Long locationId = location.getLocationId();
            Location loc = locationRepo.findById(locationId).orElse(null);
            if(loc == null) continue;
            locationDTOS.add(new LocationDTO(locationId, loc.getCity(), loc.getCountry()));
        }
        return locationDTOS;
    }

    private CompanyDTO getCompanyDTO(CompanyProfile companyProfile){
        return CompanyDTO.builder()
                .companyName(companyProfile.getCompanyName())
                .founded(companyProfile.getFounded())
                .description(companyProfile.getDescription())
                .industry(companyProfile.getIndustry())
                .website(companyProfile.getWebsite())
                .numberOfFollowers(companyProfile.getNumberOfFollowers())
                .logoUrl(companyProfile.getLogoUrl())
                .coverUrl(companyProfile.getCoverImageUrl())
                .userId(companyProfile.getUserId())
                .build();
    }

    public CompanyDTO getCompanyByUserId(Long userId) {
        CompanyProfile companyProfile = companyProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Company profile not found for user"));
        
        CompanyDTO companyDTO = getCompanyDTO(companyProfile);
        companyDTO.setCompanyProfileId(companyProfile.getCompanyProfileId());
        companyDTO.setLocations(getLocations(companyProfile.getCompanyProfileId()));
        
        return companyDTO;
    }

    public CompanyDTO getCompanyProfile(Long companyId, Long userId){
        CompanyProfile companyProfile = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("company not found"));

        CompanyDTO companyDTO = getCompanyDTO(companyProfile);
        companyDTO.setCompanyProfileId(companyProfile.getCompanyProfileId());
        if (userId != null) {
            companyDTO.setIsFollowing(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, companyId));
        }
        companyDTO.setLocations(getLocations(companyId));

        return companyDTO;
    }

    @Transactional
    public CompanyDTO updateCompanyProfile(Long companyId, CompanyUpdateDTO request) {
        CompanyProfile company = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyProfile updated = company.toBuilder()
                .companyName(request.getCompanyName() != null ? request.getCompanyName() : company.getCompanyName())
                .description(request.getDescription() != null ? request.getDescription() : company.getDescription())
                .website(request.getWebsite() != null ? request.getWebsite() : company.getWebsite())
                .industry(request.getIndustry() != null ? request.getIndustry() : company.getIndustry())
                .founded(request.getFounded() != null ? request.getFounded() : company.getFounded())
                .logoUrl(request.getLogoUrl() != null ? request.getLogoUrl() : company.getLogoUrl())
                .coverImageUrl(request.getCoverImageUrl() != null ? request.getCoverImageUrl() : company.getCoverImageUrl())
                .build();

        CompanyProfile saved = companyProfileRepo.save(updated);

        if (request.getLocations() != null) {
            updateCompanyLocations(companyId, request.getLocations());
        }

        return getCompanyProfile(companyId, companyId);
    }

    @Transactional
    public void updateCompanyLocations(Long companyId, List<LocationDTO> locationUpdates) {

        companyLocationRepo.deleteByCompanyId(companyId);

        if (locationUpdates == null || locationUpdates.isEmpty()) {
            return;
        }

        // Add new locations
        for (LocationDTO loc : locationUpdates) {
            String country = loc.getCountry();
            String city = loc.getCity();

            if (country == null || city == null ||
                    country.trim().isEmpty() || city.trim().isEmpty()) {
                continue;
            }

            // Find or create the location
            Location location = locationRepo.findByCityAndCountry(city.trim(), country.trim())
                    .orElseGet(() -> {
                        Location newLoc = new Location();
                        newLoc.setCountry(country.trim());
                        newLoc.setCity(city.trim());
                        return locationRepo.save(newLoc);
                    });

            // Create new company-location association
            CompanyLocation companyLocation = new CompanyLocation();
            companyLocation.setCompanyId(companyId);
            companyLocation.setLocationId(location.getLocationId());
            companyLocationRepo.save(companyLocation);
        }
    }


    @Transactional
    public void followCompany(Long companyId, Long userId) {
        CompanyProfile company = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, company.getUserId())) {
            throw new RuntimeException("Already following this company");
        }

        CompanyFollower follower = new CompanyFollower();
        follower.setFollowerId(userId);
        follower.setCompanyId(company.getUserId());
        companyFollowerRepo.save(follower);

        company.setNumberOfFollowers(company.getNumberOfFollowers() + 1);
        companyProfileRepo.save(company);
    }

    @Transactional
    public void unfollowCompany(Long companyId, Long userId) {
        CompanyProfile company = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!companyFollowerRepo.existsByFollowerIdAndCompanyId(userId, company.getUserId())) {
            throw new RuntimeException("Not following this company");
        }

        companyFollowerRepo.deleteByFollowerIdAndCompanyId(userId, company.getUserId());

        company.setNumberOfFollowers(company.getNumberOfFollowers() - 1);
        companyProfileRepo.save(company);
    }
}