package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobFilter;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobsControllerQlTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobsControllerQl jobsControllerQl;

    private List<Job> mockJobs;

    @BeforeEach
    void setUp() {
        // Create sample job data for testing
        Job job1 = new Job();
        job1.setJobId(3L);
        job1.setTitle("Software Engineer");

        Job job2 = new Job();
        job2.setJobId(2L);
        job2.setTitle("Senior Developer");

        mockJobs = Arrays.asList(job1, job2);
    }

    @Test
    void allJobs_withNullFilter_shouldReturnAllJobs() {
        // Arrange
        when(jobRepository.findAll()).thenReturn(mockJobs);

        // Act
        List<JobResponse> result = jobsControllerQl.allJobs(null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jobRepository, times(1)).findAll();
        verify(jobRepository, never()).findByFilter(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void allJobs_withEmptyFilter_shouldReturnAllJobs() {
        JobFilter emptyFilter = new JobFilter();
        when(jobRepository.findAll()).thenReturn(mockJobs);

        List<JobResponse> result = jobsControllerQl.allJobs(emptyFilter);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jobRepository, times(1)).findAll();
        verify(jobRepository, never()).findByFilter(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void allJobs_withTitleFilter_shouldUseRepositoryFilter() {
        // Arrange
        JobFilter filter = new JobFilter();
        filter.setTitle("Engineer");

        Job filteredJob = new Job();
        filteredJob.setJobId(1L);
        filteredJob.setTitle("Software Engineer");
        List<Job> filteredJobs = Arrays.asList(filteredJob);

        when(jobRepository.findByFilter(
                eq("Engineer"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(filteredJobs);

        // Act
        List<JobResponse> result = jobsControllerQl.allJobs(filter);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Software Engineer", result.get(0).getTitle());
        verify(jobRepository, times(1)).findByFilter(
                eq("Engineer"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
        verify(jobRepository, never()).findAll();
    }

    @Test
    void allJobs_whenRepositoryReturnsEmptyList_shouldReturnEmptyList() {
        // Arrange
        JobFilter filter = new JobFilter();
        filter.setTitle("NonExistentJob");

        when(jobRepository.findByFilter(
                eq("NonExistentJob"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(Arrays.asList());

        // Act
        List<JobResponse> result = jobsControllerQl.allJobs(filter);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jobRepository, times(1)).findByFilter(
                eq("NonExistentJob"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }
}


