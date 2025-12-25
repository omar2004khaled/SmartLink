package com.example.auth.service;

import com.example.auth.dto.CompanyDTO;
import com.example.auth.dto.CompanyUpdateDTO;
import com.example.auth.dto.LocationDTO;
import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.CompanyLocation;
import com.example.auth.entity.CompanyProfile;
import com.example.auth.entity.Location;
import com.example.auth.entity.User;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.CompanyLocationRepo;
import com.example.auth.repository.CompanyProfileRepo;
import com.example.auth.repository.LocationRepo;
import com.example.auth.repository.UserRepository;
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
    private final UserRepository userRepository;


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
                .userId(companyProfile.getUser().getId())
                .companyProfileId(companyProfile.getCompanyProfileId())
                .build();
    }

    public CompanyDTO getCompanyByUserId(Long userId, Long viewerId) {
        CompanyProfile companyProfile = companyProfileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Company profile not found for user"));
        
        CompanyDTO companyDTO = getCompanyDTO(companyProfile);
        companyDTO.setLocations(getLocations(companyProfile.getCompanyProfileId()));

        if (viewerId != null) {
            User viewer = userRepository.findById(viewerId)
                    .orElseThrow(() -> new RuntimeException("Viewer not found"));
            User company = companyProfile.getUser();

            boolean isFollowing = companyFollowerRepo.existsByFollowerAndCompany(viewer, company);
            companyDTO.setIsFollowing(isFollowing);
        } else {
            companyDTO.setIsFollowing(false);
        }
        return companyDTO;
    }

    public CompanyDTO getCompanyProfile(Long companyId, Long userId){
        CompanyProfile companyProfile = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        CompanyDTO companyDTO = getCompanyDTO(companyProfile);
        if (userId != null) {
            User followerUser = userRepository.findById(userId).orElse(null);
            User companyUser = companyProfile.getUser();
            if (followerUser != null && companyUser != null) {
                companyDTO.setIsFollowing(companyFollowerRepo.existsByFollowerAndCompany(followerUser, companyUser));
            }
        } else {
            companyDTO.setIsFollowing(false);
        }
        companyDTO.setLocations(getLocations(companyId));

        return companyDTO;
    }

    @Transactional
    public CompanyDTO updateCompanyProfile(Long companyId, CompanyUpdateDTO request) {
        CompanyProfile company = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (request.getCompanyName() != null) {
            company.setCompanyName(request.getCompanyName());
        }
        if (request.getDescription() != null) {
            company.setDescription(request.getDescription());
        }
        if (request.getWebsite() != null) {
            company.setWebsite(request.getWebsite());
        }
        if (request.getIndustry() != null) {
            company.setIndustry(request.getIndustry());
        }
        if (request.getFounded() != null) {
            company.setFounded(request.getFounded());
        }
        if (request.getLogoUrl() != null) {
            company.setLogoUrl(request.getLogoUrl());
        }
        if (request.getCoverImageUrl() != null) {
            company.setCoverImageUrl(request.getCoverImageUrl());
        }

        CompanyProfile saved = companyProfileRepo.save(company);

        if (request.getLocations() != null) {
            updateCompanyLocations(companyId, request.getLocations());
        }

        return getCompanyProfile(companyId, null);
    }
    @Transactional
    boolean userFollowsAccount(Long userId,Long companyId){
        return companyFollowerRepo.existsByFollowerAndCompany(userRepository.findById(userId).get(),userRepository.findById(companyId).get());
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

        User followerUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User companyUser = company.getUser();

        if (companyFollowerRepo.existsByFollowerAndCompany(followerUser, companyUser)) {
            throw new RuntimeException("Already following this company");
        }

        CompanyFollower follower = new CompanyFollower();
        follower.setFollower(followerUser);
        follower.setCompany(companyUser);
        companyFollowerRepo.save(follower);

        company.setNumberOfFollowers(company.getNumberOfFollowers() + 1);
        companyProfileRepo.save(company);
    }

    @Transactional
    public void unfollowCompany(Long companyId, Long userId) {
        CompanyProfile company = companyProfileRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        User followerUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User companyUser = company.getUser();

        if (!companyFollowerRepo.existsByFollowerAndCompany(followerUser, companyUser)) {
            throw new RuntimeException("Not following this company");
        }

        companyFollowerRepo.deleteByFollowerAndCompany(followerUser, companyUser);

        company.setNumberOfFollowers(Math.max(0, company.getNumberOfFollowers() - 1));
        companyProfileRepo.save(company);
    }
}