package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.service.JobServices.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(jobController)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private JobResponse buildJobResponse(Long id) {
        return JobResponse.builder()
                .jobId(id)
                .title("Software Engineer")
                .description("Develop and maintain services")
                .companyName("Tech Corp")
                .jobLocation("Remote")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .salaryMin(5000)
                .salaryMax(9000)
                .createdAt(Instant.now())
                .deadline(Instant.now().plusSeconds(86400))
                .build();
    }



}
