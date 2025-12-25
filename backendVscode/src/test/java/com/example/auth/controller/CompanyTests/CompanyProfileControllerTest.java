package com.example.auth.controller.CompanyTests;

import com.example.auth.controller.CompanyProfileController;
import com.example.auth.dto.*;
import com.example.auth.service.CompanyProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// REMOVE these annotations:
// @SpringBootTest
// @ActiveProfiles("test")
// @AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)  // Keep this one
class CompanyProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompanyProfileService companyProfileService;

    @InjectMocks
    private CompanyProfileController companyProfileController;

    private ObjectMapper objectMapper;
    private CompanyDTO testCompanyDTO;
    private List<LocationDTO> testLocations;

    @BeforeEach
    void setUp() {
        // Use standalone setup for unit tests (no Spring context)
        mockMvc = MockMvcBuilders.standaloneSetup(companyProfileController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        // FIX: LocationDTO constructor might have changed. Check the actual constructor
        // If LocationDTO has different constructor parameters, adjust accordingly
        LocationDTO location = new LocationDTO();
        location.setLocationId(1L);
        location.setCity("Cairo");
        location.setCountry("Egypt");

        testLocations = Arrays.asList(location);

        testCompanyDTO = CompanyDTO.builder()
                .companyProfileId(1L)
                .userId(100L)
                .companyName("Test Company")
                .website("https://test.com")
                .industry("Technology")
                .description("Test Description")
                .logoUrl("logo.png")
                .coverUrl("cover.png")
                .numberOfFollowers(10L)
                .founded(2020)
                .isFollowing(false)
                .locations(testLocations)
                .build();
    }

    @Test
    void getCompanyByUserId_WithViewerId_Success() throws Exception {
        Long userId = 100L;
        Long viewerId = 200L;

        when(companyProfileService.getCompanyByUserId(userId, viewerId)).thenReturn(testCompanyDTO);

        mockMvc.perform(get("/api/company/user/{userId}", userId)
                        .param("viewerId", viewerId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Test Company")))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.numberOfFollowers", is(10)))
                .andExpect(jsonPath("$.isFollowing", is(false)))
                .andExpect(jsonPath("$.website", is("https://test.com")))
                .andExpect(jsonPath("$.industry", is("Technology")));

        verify(companyProfileService, times(1)).getCompanyByUserId(userId, viewerId);
    }

    @Test
    void getCompanyByUserId_WithoutViewerId_Success() throws Exception {
        Long userId = 100L;

        when(companyProfileService.getCompanyByUserId(userId, null)).thenReturn(testCompanyDTO);

        mockMvc.perform(get("/api/company/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Test Company")))
                .andExpect(jsonPath("$.userId", is(100)));

        verify(companyProfileService, times(1)).getCompanyByUserId(userId, null);
    }

    @Test
    void getCompanyByUserId_NotFound() throws Exception {
        Long userId = 999L;

        when(companyProfileService.getCompanyByUserId(anyLong(), any()))
                .thenThrow(new RuntimeException("Company profile not found for user"));

        mockMvc.perform(get("/api/company/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Company profile not found for user"));

        verify(companyProfileService, times(1)).getCompanyByUserId(userId, null);
    }

    @Test
    void getCompanyAbout_Success() throws Exception {
        Long companyId = 1L;

        when(companyProfileService.getCompanyProfile(companyId, null)).thenReturn(testCompanyDTO);

        mockMvc.perform(get("/api/company/{companyId}/about", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.website", is("https://test.com")))
                .andExpect(jsonPath("$.industry", is("Technology")))
                .andExpect(jsonPath("$.founded", is(2020)))
                .andExpect(jsonPath("$.locations[0].city", is("Cairo")))
                .andExpect(jsonPath("$.locations[0].country", is("Egypt")));

        verify(companyProfileService, times(1)).getCompanyProfile(companyId, null);
    }

    @Test
    void getCompanyAbout_NotFound() throws Exception {
        Long companyId = 999L;

        when(companyProfileService.getCompanyProfile(anyLong(), any()))
                .thenThrow(new RuntimeException("Company not found"));

        mockMvc.perform(get("/api/company/{companyId}/about", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR in get about data"));

        verify(companyProfileService, times(1)).getCompanyProfile(companyId, null);
    }

    @Test
    void getCompanyPosts_Success() throws Exception {
        Long companyId = 1L;

        mockMvc.perform(get("/api/company/{companyId}/posts", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void updateCompanyProfile_Success() throws Exception {
        Long companyId = 1L;
        CompanyUpdateDTO updateDTO = new CompanyUpdateDTO();
        updateDTO.setCompanyName("Updated Company");
        updateDTO.setDescription("Updated Description");

        // Mock the service to return updated DTO
        CompanyDTO updatedDTO = CompanyDTO.builder()
                .companyProfileId(1L)
                .companyName("Updated Company")
                .description("Updated Description")
                .build();

        when(companyProfileService.updateCompanyProfile(anyLong(), any(CompanyUpdateDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/company/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Updated Company")));

        verify(companyProfileService, times(1)).updateCompanyProfile(eq(companyId), any(CompanyUpdateDTO.class));
    }

    @Test
    void updateCompanyProfile_BadRequest() throws Exception {
        Long companyId = 1L;
        CompanyUpdateDTO updateDTO = new CompanyUpdateDTO();
        updateDTO.setCompanyName("Updated Company");

        when(companyProfileService.updateCompanyProfile(anyLong(), any(CompanyUpdateDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/company/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error in update"));

        verify(companyProfileService, times(1)).updateCompanyProfile(eq(companyId), any(CompanyUpdateDTO.class));
    }

    @Test
    void followCompany_Success() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest();
        followRequest.setUserId(200L);  // Use setter if constructor doesn't exist

        doNothing().when(companyProfileService).followCompany(companyId, 200L);

        mockMvc.perform(post("/api/company/{companyId}/follow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully followed company"));

        verify(companyProfileService, times(1)).followCompany(companyId, 200L);
    }

    @Test
    void followCompany_BadRequest() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest();
        followRequest.setUserId(200L);

        doThrow(new RuntimeException("Already following"))
                .when(companyProfileService).followCompany(anyLong(), anyLong());

        mockMvc.perform(post("/api/company/{companyId}/follow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Already following"));

        verify(companyProfileService, times(1)).followCompany(companyId, 200L);
    }

    @Test
    void unfollowCompany_Success() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest();
        followRequest.setUserId(200L);

        doNothing().when(companyProfileService).unfollowCompany(companyId, 200L);

        mockMvc.perform(post("/api/company/{companyId}/unfollow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully unfollowed company"));

        verify(companyProfileService, times(1)).unfollowCompany(companyId, 200L);
    }

    @Test
    void unfollowCompany_BadRequest() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest();
        followRequest.setUserId(200L);

        doThrow(new RuntimeException("Not following"))
                .when(companyProfileService).unfollowCompany(anyLong(), anyLong());

        mockMvc.perform(post("/api/company/{companyId}/unfollow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error in unfollow"));

        verify(companyProfileService, times(1)).unfollowCompany(companyId, 200L);
    }

    @Test
    void getCompanyByUserId_WithNullUserId_NotFound() throws Exception {
        when(companyProfileService.getCompanyByUserId(anyLong(), any()))
                .thenThrow(new RuntimeException("Invalid user ID"));

        mockMvc.perform(get("/api/company/user/{userId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void followCompany_WithInvalidUserId_BadRequest() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest();
        // Leave userId null

        mockMvc.perform(post("/api/company/{companyId}/follow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest());
    }
}