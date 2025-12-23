package com.example.auth.controller;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
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
class AdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private VerificationTokenRepository tokenRepository;

        @Autowired
        private JobSeekerProfileRepository jobSeekerProfileRepository;

        @Autowired
        private com.example.auth.repository.CommentRepo commentRepo;

        @Autowired
        private com.example.auth.repository.PostRepository postRepository;

        @Autowired
        private com.example.auth.repository.ConnectionRepository connectionRepository;

        @Autowired
        private com.example.auth.repository.NotificationRepository notificationRepository;

        private User adminUser;
        private User regularUser;

        @BeforeEach
        void setUp() {
                // Delete child entities first to avoid FK constraint violations
                commentRepo.deleteAll();
                postRepository.deleteAll();
                connectionRepository.deleteAll();
                notificationRepository.deleteAll();
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
                userRepository.save(adminUser);

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
                userRepository.save(regularUser);
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
        void adminDashboard_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403
        }

        // ===== GET ALL USERS TESTS =====
        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithAdminRole_ShouldReturnAllUsers() throws Exception {
                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.users", hasSize(2)))
                                .andExpect(jsonPath("$.users[0].email",
                                                anyOf(is("admin@test.com"), is("user@test.com"))))
                                .andExpect(jsonPath("$.users[1].email",
                                                anyOf(is("admin@test.com"), is("user@test.com"))));
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

        @Test
        void getAllUsers_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isFound());
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
                                .andExpect(jsonPath("$.id", is(regularUser.getId().intValue())))
                                .andExpect(jsonPath("$.email", is("user@test.com")))
                                .andExpect(jsonPath("$.role", is("USER")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(get("/admin/users/99999"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_ShouldReturnCompleteUserData() throws Exception {
                mockMvc.perform(get("/admin/users/" + regularUser.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName", is("Regular User")))
                                .andExpect(jsonPath("$.birthDate", is("1995-05-20")))
                                .andExpect(jsonPath("$.phoneNumber", is("+209876543210")))
                                .andExpect(jsonPath("$.gender", is("FEMALE")))
                                .andExpect(jsonPath("$.enabled", is(true)));
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
                                .andExpect(jsonPath("$", containsString("promoted to ADMIN successfully")));

                // Verify user role was updated
                User promotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assert promotedUser != null;
                assert "ADMIN".equals(promotedUser.getRole());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/promote/99999"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_WithAlreadyAdminUser_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/promote/" + adminUser.getId()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("already an ADMIN")));
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
                unverifiedUser.setEnabled(false);
                unverifiedUser.setRole("USER");
                User savedUser = userRepository.save(unverifiedUser);

                mockMvc.perform(post("/admin/promote/" + savedUser.getId()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("must verify email before promotion")));
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

                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", containsString("demoted to USER successfully")));

                // Verify user role was updated
                User demotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assert demotedUser != null;
                assert "USER".equals(demotedUser.getRole());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/demote/99999"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("User not found")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithAlreadyRegularUser_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("already a regular USER")));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void demoteToUser_WithSuperAdmin_ShouldReturnBadRequest() throws Exception {
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

                mockMvc.perform(post("/admin/demote/" + savedSuperAdmin.getId()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$", containsString("Cannot demote super admin")));
        }

        @Test
        @WithMockUser(roles = "USER")
        void demoteToUser_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(post("/admin/demote/" + adminUser.getId()))
                                .andExpect(status().isForbidden());
        }

        // ========== SECURITY TESTS ==========

        @Test
        void adminEndpoints_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403

                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403

                mockMvc.perform(get("/admin/stats"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403

                mockMvc.perform(post("/admin/promote/1"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403

                mockMvc.perform(post("/admin/demote/1"))
                                .andExpect(status().isFound()); // OAuth2 redirects instead of 403
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoints_WithAdminRole_ShouldBeAccessible() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/admin/stats"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        void adminEndpoints_WithUserRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isForbidden());

                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isForbidden());

                mockMvc.perform(get("/admin/stats"))
                                .andExpect(status().isForbidden());

                mockMvc.perform(post("/admin/promote/1"))
                                .andExpect(status().isForbidden());

                mockMvc.perform(post("/admin/demote/1"))
                                .andExpect(status().isForbidden());
        }

        // ========== EDGE CASE TESTS ==========

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_WithEmptyDatabase_ShouldReturnEmptyArray() throws Exception {
                userRepository.deleteAll();

                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.users", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getStats_WithOnlyAdmins_ShouldReturnCorrectStats() throws Exception {
                userRepository.deleteAll();

                User admin1 = new User();
                admin1.setFullName("Admin 1");
                admin1.setEmail("admin1@test.com");
                admin1.setPassword("encodedPassword");
                admin1.setBirthDate(LocalDate.of(1990, 1, 1));
                admin1.setPhoneNumber("+201111111111");
                admin1.setGender(Gender.MALE);
                admin1.setEnabled(true);
                admin1.setRole("ADMIN");
                userRepository.save(admin1);

                User admin2 = new User();
                admin2.setFullName("Admin 2");
                admin2.setEmail("admin2@test.com");
                admin2.setPassword("encodedPassword");
                admin2.setBirthDate(LocalDate.of(1992, 3, 10));
                admin2.setPhoneNumber("+202222222222");
                admin2.setGender(Gender.FEMALE);
                admin2.setEnabled(true);
                admin2.setRole("ADMIN");
                userRepository.save(admin2);

                mockMvc.perform(get("/admin/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalUsers").value(2))
                                .andExpect(jsonPath("$.activeUsers").value(2))
                                .andExpect(jsonPath("$.adminUsers").value(2));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void promoteToAdmin_ThenDemote_ShouldUpdateRoleCorrectly() throws Exception {
                // Promote user to admin
                mockMvc.perform(post("/admin/promote/" + regularUser.getId()))
                                .andExpect(status().isOk());

                User promotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assert promotedUser != null;
                assert "ADMIN".equals(promotedUser.getRole());

                // Demote back to user
                mockMvc.perform(post("/admin/demote/" + regularUser.getId()))
                                .andExpect(status().isOk());

                User demotedUser = userRepository.findById(regularUser.getId()).orElse(null);
                assert demotedUser != null;
                assert "USER".equals(demotedUser.getRole());
        }
}
