package com.example.auth.controller;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.JobApplication;
import com.example.auth.repository.JobRepository;
import com.example.auth.service.JobServices.JobApplicationService;
import com.example.auth.service.JobServices.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockitoBean
    JobApplicationService jobApplicationService;
    @Test
    @WithMockUser(username = "lio messi", roles = "admin")
    public void repoWorkingWell() throws Exception{
        ApplicationDTO applicationDTO =new ApplicationDTO();
        Long expectedApplicationId = 123L;

        when(jobApplicationService.savePost(any(ApplicationDTO.class)))
                .thenReturn(expectedApplicationId);

        mockMvc.perform(post("/apply/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(applicationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    }


