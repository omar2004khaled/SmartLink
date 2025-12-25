package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)

class AuthControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

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

    @BeforeEach
    void setUp() {
        // Delete child entities first to avoid FK constraint violation
        commentRepo.deleteAll();
        postRepository.deleteAll();
        connectionRepository.deleteAll();
        notificationRepository.deleteAll();
        jobSeekerProfileRepository.deleteAll();
        userRepository.deleteAll();

        // Ensure a test user exists for login tests
        User testUser = new User();
        testUser.setFullName("Regular User");
        testUser.setEmail("user@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setBirthDate(LocalDate.of(1995, 5, 20));
        testUser.setPhoneNumber("+209876543210");
        testUser.setGender(Gender.FEMALE);
        testUser.setEnabled(true);
        testUser.setRole("USER");
        userRepository.save(testUser);
    }

    @Test
    void login_WithValidCredentials_Returns200AndToken() throws Exception {
        String email = "user@test.com";
        String password = "Pass123!";
        String token = "jwt-xyz";

        when(authService.login(email, password)).thenReturn(token);

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void login_WithInvalidCredentials_Returns401() throws Exception {
        String email = "user@test.com";
        String password = "BadPass";

        when(authService.login(email, password)).thenThrow(new IllegalArgumentException("Invalid password"));

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }
}
