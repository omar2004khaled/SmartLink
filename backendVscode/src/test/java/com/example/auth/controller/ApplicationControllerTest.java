package com.example.auth.controller;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.dto.JobDTO.CommentAppDTO;
import com.example.auth.dto.JobDTO.StatusChange;
import com.example.auth.service.JobServices.JobApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    // ========== POST /apply/post Tests ==========

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void testPostApplication_Success() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(1L);
        Long expectedApplicationId = 123L;

        when(jobApplicationService.savePost(any(ApplicationDTO.class)))
                .thenReturn(expectedApplicationId);

        // Act & Assert
        mockMvc.perform(post("/apply/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("123"));

        verify(jobApplicationService, times(1)).savePost(any(ApplicationDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testPostApplication_AsRegularUser() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        Long expectedApplicationId = 456L;

        when(jobApplicationService.savePost(any(ApplicationDTO.class)))
                .thenReturn(expectedApplicationId);

        // Act & Assert
        mockMvc.perform(post("/apply/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("456"));
    }

    // ========== GET /apply/{jobId} Tests ==========

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetApplicationsByJobId_Success() throws Exception {
        // Arrange
        Long jobId = 5L;
        ApplicationDTO app1 = new ApplicationDTO();
        app1.setId(1L);

        ApplicationDTO app2 = new ApplicationDTO();
        app2.setId(2L);

        List<ApplicationDTO> applications = Arrays.asList(app1, app2);

        when(jobApplicationService.getByJobId(jobId)).thenReturn(applications);

        // Act & Assert
        mockMvc.perform(get("/apply/{jobId}", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(jobApplicationService, times(1)).getByJobId(jobId);
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void testGetApplicationsByJobId_EmptyList() throws Exception {
        // Arrange
        Long jobId = 999L;
        List<ApplicationDTO> emptyList = Arrays.asList();

        when(jobApplicationService.getByJobId(jobId)).thenReturn(emptyList);

        // Act & Assert
        mockMvc.perform(get("/apply/{jobId}", jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ========== PATCH /apply/add/comment Tests ==========

    @Test
    @WithMockUser(username = "normalUser", roles = "USER")
    public void testAddComment_Success() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(123L);

        CommentAppDTO commentAppDTO = new CommentAppDTO();
        commentAppDTO.setComment("Great candidate!");
        commentAppDTO.setJobAppId(5L);

        when(jobApplicationService.addComment(eq("Great candidate!"), eq(5L)))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/add/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentAppDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(123L));

        verify(jobApplicationService, times(1)).addComment("Great candidate!", 5L);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddComment_AsAdmin() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(200L);

        CommentAppDTO commentAppDTO = new CommentAppDTO();
        commentAppDTO.setComment("Needs improvement");
        commentAppDTO.setJobAppId(10L);

        when(jobApplicationService.addComment(anyString(), anyLong()))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/add/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentAppDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200L));
    }

    // ========== PATCH /apply/status Tests ==========

    @Test
    @WithMockUser(username = "normalUser", roles = "USER")
    public void testChangeStatus_ToAccepted() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(123L);

        StatusChange statusChange = new StatusChange();
        statusChange.setStatus("ACCEPTED");
        statusChange.setJobApp(5L);

        when(jobApplicationService.changeStatus(eq("ACCEPTED"), eq(5L)))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusChange)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(123L));

        verify(jobApplicationService, times(1)).changeStatus("ACCEPTED", 5L);
    }

    @Test
    @WithMockUser(username = "hr", roles = "ADMIN")
    public void testChangeStatus_ToRejected() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(99L);

        StatusChange statusChange = new StatusChange();
        statusChange.setStatus("REJECTED");
        statusChange.setJobApp(15L);

        when(jobApplicationService.changeStatus(eq("REJECTED"), eq(15L)))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusChange)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L));
    }

    @Test
    @WithMockUser(username = "hr", roles = "ADMIN")
    public void testChangeStatus_ToPending() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(77L);

        StatusChange statusChange = new StatusChange();
        statusChange.setStatus("PENDING");
        statusChange.setJobApp(20L);

        when(jobApplicationService.changeStatus(anyString(), anyLong()))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusChange)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(77L));

        verify(jobApplicationService, times(1)).changeStatus("PENDING", 20L);
    }

    // ========== Error/Edge Case Tests ==========





    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAddComment_WithEmptyComment() throws Exception {
        // Arrange
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(123L);

        CommentAppDTO commentAppDTO = new CommentAppDTO();
        commentAppDTO.setComment("");
        commentAppDTO.setJobAppId(5L);

        when(jobApplicationService.addComment(eq(""), eq(5L)))
                .thenReturn(applicationDTO);

        // Act & Assert
        mockMvc.perform(patch("/apply/add/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentAppDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123L));
    }
}