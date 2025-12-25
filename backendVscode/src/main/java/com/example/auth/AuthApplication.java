package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.entity.User;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.enums.Gender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
@EnableAsync
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


    @Bean
    CommandLineRunner createSuperAdmin(UserRepository userRepository, PasswordEncoder encoder, JobSeekerProfileRepository profileRepo) {
        return args -> {
            // Check if super admin already exists
            if (userRepository.findByEmail("BigBoss@example.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Big Boss");
                admin.setEmail("BigBoss@example.com");
                admin.setPassword(encoder.encode("BigBoss123!"));
                admin.setPhoneNumber("+10001000100");
                admin.setBirthDate(LocalDate.of(2004, 5, 9));
                admin.setGender(Gender.PREFER_NOT_TO_SAY);
                admin.setEnabled(true);
                admin.setRole("ADMIN");

                userRepository.save(admin);
                
                // Create profile for admin
                JobSeekerProfile profile = new JobSeekerProfile();
                profile.setUser(admin);
                profile.setBirthDate(admin.getBirthDate());
                profileRepo.save(profile);

                System.out.println("\n=== SUPER ADMIN CREATED ===");
                System.out.println("Email: BigBoss@example.com");
                System.out.println("Password: BigBoss123!");
                System.out.println("Role: ADMIN");
                System.out.println("===========================\n");
            } else {
                System.out.println("Super admin already exists: BigBoss@example.com");
            }
        };
    }
}