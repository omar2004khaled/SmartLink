package com.example.auth.controller;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.auth.entity.User;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional // Add transactional to rollback database changes
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset context after each test
class AdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private VerificationTokenRepository tokenRepository;

        @Autowired
        private JobSeekerProfileRepository jobSeekerProfileRepository;

        private User adminUser;
        private User regularUser;

        @BeforeEach
        void setUp() {
                // Clear repositories in correct order to avoid FK violations
                jobSeekerProfileRepository.deleteAll();
                tokenRepository.deleteAll();
                userRepository.deleteAll();

                // Create admin user
                adminUser = new User();
                adminUser.setFullName("Admin User");
                adminUser.setEmail("admin@test.com");
                adminUser.setPassword("encodedPassword");
                adminUser.setBirthDate(LocalDate.of(1990, 1, 1));
                adminUser.setPhoneNumber("+201234567890");
                adminUser.setGender(Gender.MALE);
                adminUser.setEnabled(true);
                adminUser.setRole("ADMIN");
                adminUser = userRepository.save(adminUser); // Save and get persisted entity

                // Create regular user
                regularUser = new User();
                regularUser.setFullName("Regular User");
                regularUser.setEmail("user@test.com");
                regularUser.setPassword("encodedPassword");
                regularUser.setBirthDate(LocalDate.of(1995, 5, 20));
                regularUser.setPhoneNumber("+209876543210");
                regularUser.setGender(Gender.FEMALE);
                regularUser.setEnabled(true);
                regularUser.setRole("USER");
                regularUser = userRepository.save(regularUser); // Save and get persisted entity

                // Refresh entities to ensure they're managed
                userRepository.flush();
        }

        // ===== DASHBOARD TESTS =====
        @Test
        @WithMockUser(roles = "ADMIN")
        void adminDashboard_WithAdminRole_ShouldReturnWelcomeMessage() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Welcome to Admin Dashboard!"));
        }

        @Test
        @WithMockUser(roles = "USER")
        void adminDashboard_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                        .andExpect(status().isForbidden());
        }

        @Test
        void adminDashboard_WithoutAuthentication_ShouldRedirect() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                        .andExpect(status().isFound()); // Redirects to login
        }

        // ===== GET ALL USERS TESTS =====
        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithAdminRole_ShouldReturnAllUsers() throws Exception {
                mockMvc.perform(get("/admin/users"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.users", hasSize(2)))
                        .andExpect(jsonPath("$.users[*].email",
                                containsInAnyOrder("admin@test.com", "user@test.com")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithAdminRole_ShouldReturnUserDetails() throws Exception {
                mockMvc.perform(get("/admin/users"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.users[*].id", hasItems(notNullValue())))
                        .andExpect(jsonPath("$.users[*].fullName", hasItems(notNullValue())))
                        .andExpect(jsonPath("$.users[*].email", hasItems(notNullValue())))
                        .andExpect(jsonPath("$.users[*].role", hasItems(notNullValue())));
        }

        @Test
        @WithMockUser(roles = "USER")
        void getAllUsers_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/users"))
                        .andExpect(status().isForbidden());
        }

        // ===== GET STATISTICS TESTS =====
        @Test
        @WithMockUser(roles = "ADMIN")
        void getStats_WithAdminRole_ShouldReturnCorrectStatistics() throws Exception {
                mockMvc.perform(get("/admin/stats"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.totalUsers").value(2))
                        .andExpect(jsonPath("$.activeUsers").value(2))
                        .andExpect(jsonPath("$.adminUsers").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getStats_WithDisabledUser_ShouldCountCorrectly() throws Exception {
                // Create disabled user
                User disabledUser = new User();
                disabledUser.setFullName("Disabled User");
                disabledUser.setEmail("disabled@test.com");
                disabledUser.setPassword("encodedPassword");
                disabledUser.setBirthDate(LocalDate.of(1998, 3, 15));
                disabledUser.setPhoneNumber("+201111111111");
                disabledUser.setGender(Gender.MALE);
                disabledUser.setEnabled(false);
                disabledUser.setRole("USER");
                userRepository.save(disabledUser);
                userRepository.flush();

                mockMvc.perform(get("/admin/stats"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.totalUsers").value(3))
                        .andExpect(jsonPath("$.activeUsers").value(2))
                        .andExpect(jsonPath("$.adminUsers").value(1));
        }

        @Test
        @WithMockUser(roles = "USER")
        void getStats_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/stats"))
                        .andExpect(status().isForbidden());
        }

        // ===== GET USER BY ID TESTS =====
        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_WithValidId_ShouldReturnUser() throws Exception {
                mockMvc.perform(get("/admin/users/" + regularUser.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(regularUser.getId().intValue()))
                        .andExpect(jsonPath("$.email").value("user@test.com"))
                        .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(get("/admin/users/99999"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "USER")
        void getUserById_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/users/" + regularUser.getId()))
                        .andExpect(status().isForbidden());
        }

        // ========== PROMOTE TO ADMIN TESTS ==========
        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithValidUserId_ShouldPromoteUser() throws Exception {
                mockMvc.perform(post("/admin/promote/" + regularUser.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message", containsString("promoted to ADMIN successfully")));

                // Verify user role was updated using JUnit assertions
                User promotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assertNotNull(promotedUser);
                assertEquals("ADMIN", promotedUser.getRole());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/promote/99999"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithAlreadyAdminUser_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/promote/" + adminUser.getId()))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("already an ADMIN")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithUnverifiedEmail_ShouldReturnBadRequest() throws Exception {
                User unverifiedUser = new User();
                unverifiedUser.setFullName("Unverified User");
                unverifiedUser.setEmail("unverified@test.com");
                unverifiedUser.setPassword("encodedPassword");
                unverifiedUser.setBirthDate(LocalDate.of(2000, 1, 1));
                unverifiedUser.setPhoneNumber("+202222222222");
                unverifiedUser.setGender(Gender.MALE);
                unverifiedUser.setEnabled(false); // Not enabled/verified
                unverifiedUser.setRole("USER");
                User savedUser = userRepository.save(unverifiedUser);
                userRepository.flush();

                mockMvc.perform(post("/admin/promote/" + savedUser.getId()))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("must verify email before promotion")));
        }

        @Test
        @WithMockUser(roles = "USER")
        void promoteToAdmin_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(post("/admin/promote/" + regularUser.getId()))
                        .andExpect(status().isForbidden());
        }

        // ========== DEMOTE TO USER TESTS ==========
        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithValidAdminId_ShouldDemoteUser() throws Exception {
                // First promote regular user to admin
                regularUser.setRole("ADMIN");
                userRepository.save(regularUser);
                userRepository.flush();

                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message", containsString("demoted to USER successfully")));

                // Verify user role was updated
                User demotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assertNotNull(demotedUser);
                assertEquals("USER", demotedUser.getRole());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/demote/99999"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithAlreadyRegularUser_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("already a regular USER")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithSuperAdmin_ShouldReturnBadRequest() throws Exception {
                // Assuming super admin has a specific email or flag
                User superAdmin = new User();
                superAdmin.setFullName("Super Admin");
                superAdmin.setEmail("BigBoss@example.com");
                superAdmin.setPassword("encodedPassword");
                superAdmin.setBirthDate(LocalDate.of(1985, 6, 15));
                superAdmin.setPhoneNumber("+203333333333");
                superAdmin.setGender(Gender.MALE);
                superAdmin.setEnabled(true);
                superAdmin.setRole("ADMIN");
                User savedSuperAdmin = userRepository.save(superAdmin);
                userRepository.flush();

                mockMvc.perform(post("/admin/demote/" + savedSuperAdmin.getId()))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("Cannot demote super admin")));
        }

        // ========== EDGE CASE TESTS ==========
        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithEmptyDatabase_ShouldReturnEmptyArray() throws Exception {
                // Clear database
                userRepository.deleteAll();
                userRepository.flush();

                mockMvc.perform(get("/admin/users"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.users", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_ThenDemote_ShouldUpdateRoleCorrectly() throws Exception {
                // Promote user to admin
                mockMvc.perform(post("/admin/promote/" + regularUser.getId()))
                        .andExpect(status().isOk());

                User promotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assertNotNull(promotedUser);
                assertEquals("ADMIN", promotedUser.getRole());

                // Demote back to user
                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                        .andExpect(status().isOk());

                User demotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assertNotNull(demotedUser);
                assertEquals("USER", demotedUser.getRole());
        }
}