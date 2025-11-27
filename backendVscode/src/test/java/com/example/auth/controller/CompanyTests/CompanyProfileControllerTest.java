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
import org.springframework.http.HttpStatus;
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

@ExtendWith(MockitoExtension.class)
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
        mockMvc = MockMvcBuilders.standaloneSetup(companyProfileController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        LocationDTO location = new LocationDTO(1L, "Cairo", "Egypt");
        testLocations = Arrays.asList(location);

        testCompanyDTO = new CompanyDTO();
        testCompanyDTO.setUserId(100L);
        testCompanyDTO.setCompanyName("Test Company");
        testCompanyDTO.setWebsite("https://test.com");
        testCompanyDTO.setIndustry("Technology");
        testCompanyDTO.setDescription("Test Description");
        testCompanyDTO.setLogoUrl("logo.png");
        testCompanyDTO.setCoverUrl("cover.png");
        testCompanyDTO.setNumberOfFollowers(10L);
        testCompanyDTO.setFounded(2020);
        testCompanyDTO.setIsFollowing(false);
        testCompanyDTO.setLocations(testLocations);
    }

    @Test
    void getCompanyProfile_WithUserId_Success() throws Exception {
        Long companyId = 1L;
        Long userId = 200L;

        when(companyProfileService.getCompanyProfile(companyId, userId)).thenReturn(testCompanyDTO);

        mockMvc.perform(get("/api/company/{companyId}", companyId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Test Company")))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.numberOfFollowers", is(10)))
                .andExpect(jsonPath("$.isFollowing", is(false)))
                .andExpect(jsonPath("$.website", is("https://test.com")))
                .andExpect(jsonPath("$.industry", is("Technology")));

        verify(companyProfileService, times(1)).getCompanyProfile(companyId, userId);
    }

    @Test
    void getCompanyProfile_WithoutUserId_Success() throws Exception {
        Long companyId = 1L;

        when(companyProfileService.getCompanyProfile(companyId, null)).thenReturn(testCompanyDTO);

        mockMvc.perform(get("/api/company/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Test Company")))
                .andExpect(jsonPath("$.userId", is(100)));

        verify(companyProfileService, times(1)).getCompanyProfile(companyId, null);
    }

    @Test
    void getCompanyProfile_NotFound() throws Exception {
        Long companyId = 999L;

        when(companyProfileService.getCompanyProfile(anyLong(), any()))
                .thenThrow(new RuntimeException("Company not found"));

        mockMvc.perform(get("/api/company/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR in get Profile"));

        verify(companyProfileService, times(1)).getCompanyProfile(companyId, null);
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
        updateDTO.setCompanyId(companyId);
        updateDTO.setCompanyName("Updated Company");
        updateDTO.setDescription("Updated Description");
        updateDTO.setWebsite("https://updated.com");
        updateDTO.setIndustry("Finance");
        updateDTO.setFounded(2021);

        testCompanyDTO.setCompanyName("Updated Company");

        when(companyProfileService.updateCompanyProfile(eq(companyId), any(CompanyUpdateDTO.class)))
                .thenReturn(testCompanyDTO);

        mockMvc.perform(put("/api/company/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Updated Company")))
                .andExpect(jsonPath("$.userId", is(100)));

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
        FollowRequest followRequest = new FollowRequest(200L);

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
        FollowRequest followRequest = new FollowRequest(200L);

        doThrow(new RuntimeException("Already following"))
                .when(companyProfileService).followCompany(anyLong(), anyLong());

        mockMvc.perform(post("/api/company/{companyId}/follow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error in follow"));

        verify(companyProfileService, times(1)).followCompany(companyId, 200L);
    }

    @Test
    void unfollowCompany_Success() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest(200L);

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
        FollowRequest followRequest = new FollowRequest(200L);

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
    void getCompanyProfile_WithNullCompanyId_NotFound() throws Exception {
        when(companyProfileService.getCompanyProfile(anyLong(), any()))
                .thenThrow(new RuntimeException("Invalid company ID"));

        mockMvc.perform(get("/api/company/{companyId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void followCompany_WithInvalidUserId_BadRequest() throws Exception {
        Long companyId = 1L;
        FollowRequest followRequest = new FollowRequest(null);

        doThrow(new RuntimeException("Invalid user ID"))
                .when(companyProfileService).followCompany(anyLong(), any());

        mockMvc.perform(post("/api/company/{companyId}/follow", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest());
    }
}
