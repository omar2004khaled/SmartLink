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
        List<CompanyLocation> locations =companyLocationRepo.findByCompanyId(companyId);
        List<LocationDTO> locationDTOS=new ArrayList<>();
        for(CompanyLocation location:locations){
            final Long locationId = location.getLocationId();
            Location loc= locationRepo.findById(locationId).orElse(null);
            if(loc==null) continue;
            locationDTOS.add(new LocationDTO(locationId,loc.getCity(),loc.getCountry()));
        }
        return locationDTOS;

    }
    private CompanyDTO getCompanyDTO(CompanyProfile companyProfile){
        CompanyDTO companyDTO=new CompanyDTO();
        companyDTO.setCompanyName(companyProfile.getCompanyName());
        companyDTO.setFounded(companyProfile.getFounded());
        companyDTO.setDescription(companyProfile.getDescription());
        companyDTO.setIndustry(companyProfile.getIndustry());
        companyDTO.setWebsite(companyProfile.getWebsite());
        companyDTO.setNumberOfFollowers(companyProfile.getNumberOfFollowers());
        companyDTO.setUserId(companyProfile.getUserId());
        companyDTO.setLogoUrl(companyProfile.getLogoUrl());
        companyDTO.setCoverUrl(companyProfile.getCoverImageUrl());
        return companyDTO;

    }
    public CompanyDTO getCompanyProfile(Long companyId,Long userId){
//        System.out.println(1);
        CompanyProfile companyProfile=companyProfileRepo.findByUserId(companyId).orElseThrow(() -> new RuntimeException("company not found"));
//        System.out.println(2);
        CompanyDTO companyDTO=getCompanyDTO(companyProfile);
//        System.out.println(3);
        companyDTO.setIsFollowing(companyFollowerRepo.existsByFollowerIdAndCompanyId(userId,companyId));
        companyDTO.setLocations(getLocations(companyId));
//        System.out.println(4);
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

        CompanyProfile updated = companyProfileRepo.save(company);

        if (request.getLocations() != null && !request.getLocations().isEmpty()) {
            updateCompanyLocations(companyId, request.getLocations());
        }

        return getCompanyProfile(companyId, companyId);
    }

    @Transactional
    public void updateCompanyLocations(Long companyId, List<LocationDTO> locationUpdates) {
        for (LocationDTO loc : locationUpdates) {
            String country = loc.getCountry();
            String city = loc.getCity();

            if (country == null || city == null)
                continue;
            Location location = locationRepo.findByCityAndCountry(city, country)
                    .orElseGet(() -> {
                        Location newLoc = new Location();
                        newLoc.setCountry(country);
                        newLoc.setCity(city);
                        return locationRepo.save(newLoc);
                    });

            boolean exist = companyLocationRepo.existsByCompanyIdAndLocationId(
                    companyId,
                    location.getLocationId()
            );
            if (!exist) {
                CompanyLocation companyLocation = new CompanyLocation();
                companyLocation.setCompanyId(companyId);
                companyLocation.setLocationId(location.getLocationId());
                companyLocationRepo.save(companyLocation);
            }
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

        company.setNumberOfFollowers(company.getNumberOfFollowers()-1);
        companyProfileRepo.save(company);
    }


}
