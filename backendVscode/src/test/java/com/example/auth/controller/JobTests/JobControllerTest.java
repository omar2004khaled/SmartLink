package com.example.auth.controller.JobTests;

import com.example.auth.controller.JobController;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
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
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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

    @Test
    public void testCreateJob() throws Exception {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setCompanyId(1L);
        jobRequest.setTitle("Software Engineer");
        jobRequest.setDescription("Backend work");
        jobRequest.setExperienceLevel(ExperienceLevel.MID);
        jobRequest.setJobType(JobType.FULL_TIME);
        jobRequest.setLocationType(LocationType.REMOTE);

        JobResponse mockResponse = buildJobResponse(1L);

        when(jobService.createJob(any(JobRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/jobs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(1))
                .andExpect(jsonPath("$.title").value("Software Engineer"))
                .andExpect(jsonPath("$.companyName").value("Tech Corp"));
    }



    @Test
    public void testDeleteJob() throws Exception {
        doNothing().when(jobService).deleteJob(1L);

        mockMvc.perform(delete("/jobs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateJob() throws Exception {
        JobUpdateRequest request = new JobUpdateRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated description");
        request.setExperienceLevel(ExperienceLevel.SENIOR);
        request.setJobType(JobType.FULL_TIME);
        request.setLocationType(LocationType.HYBRID);

        JobResponse updatedResponse = buildJobResponse(1L);
        updatedResponse.setTitle("Updated Title");

        when(jobService.updateJob(eq(1L), any(JobUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }
}
