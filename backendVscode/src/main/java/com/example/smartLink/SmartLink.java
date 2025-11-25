package com.example.smartLink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.smartLink.repository.UserRepository;
import com.example.smartLink.entity.User;
import com.example.smartLink.enums.Gender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class SmartLink {

    public static void main(String[] args) {
        SpringApplication.run(SmartLink.class, args);
    }

    @Bean
    CommandLineRunner createSuperAdmin(UserRepository userRepository, PasswordEncoder encoder) {
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
                admin.setEnabled(true);  // Admin doesn't need email verification
                admin.setRole("ADMIN");

                userRepository.save(admin);
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