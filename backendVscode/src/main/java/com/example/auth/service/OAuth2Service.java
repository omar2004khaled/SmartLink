package com.example.auth.service;

import com.example.auth.config.JwtService;
import com.example.auth.entity.User;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    public OAuth2Service(
            UserRepository userRepository,
            JwtService jwtService,
            JobSeekerProfileRepository jobSeekerProfileRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }

    public String processOAuth2User(OAuth2User oauth2User, String provider) {

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(name, email, provider, providerId);
                    User savedUser = userRepository.save(newUser);

                    JobSeekerProfile profile = new JobSeekerProfile();
                    profile.setUser(savedUser);
                    profile.setBirthDate(newUser.getBirthDate());
                    profile.setGender(JobSeekerProfile.Gender.MALE);

                    jobSeekerProfileRepository.save(profile);
                    return savedUser;
                });

        if ("JOB_SEEKER".equals(user.getUserType())) {
            boolean hasProfile = jobSeekerProfileRepository.findByUser_Id(user.getId()).isPresent();

            if (!hasProfile) {
                JobSeekerProfile profile = new JobSeekerProfile();
                profile.setUser(user);
                profile.setBirthDate(user.getBirthDate());
                profile.setGender(JobSeekerProfile.Gender.MALE);
                jobSeekerProfileRepository.save(profile);
            }
        }

        if (!user.getProvider().equals(provider)) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getUserType());
    }
}
