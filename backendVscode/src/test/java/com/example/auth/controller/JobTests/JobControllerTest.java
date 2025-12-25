package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.service.JobServices.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    private JobRequest jobRequest;
    private JobUpdateRequest jobUpdateRequest;
    private JobResponse jobResponse;
    private Page<JobResponse> jobResponsePage;

    @BeforeEach
    void setUp() {
        // Setup test data using enums
        jobRequest = JobRequest.builder()
                .companyId(1L)
                .title("Software Engineer")
                .description("Develop software applications")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .jobLocation("New York")
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        jobUpdateRequest = JobUpdateRequest.builder()
                .title("Senior Software Engineer")
                .description("Senior development role")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.HYBRID)
                .jobLocation("San Francisco")
                .salaryMin(100000)
                .salaryMax(150000)
                .deadline(Instant.now().plusSeconds(172800))
                .build();

        jobResponse = JobResponse.builder()
                .jobId(1L)
                .title("Software Engineer")
                .description("Develop software applications")
                .companyName("Tech Corp")
                .jobLocation("New York")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .createdAt(Instant.now())
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        List<JobResponse> jobResponses = List.of(jobResponse);
        jobResponsePage = new PageImpl<>(jobResponses, PageRequest.of(0, 10), jobResponses.size());
    }

    @Test
    void createJob_Success() {
        // Arrange
        when(jobService.createJob(any(JobRequest.class))).thenReturn(jobResponse);

        // Act
        ResponseEntity<JobResponse> response = jobController.createJob(jobRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobResponse, response.getBody());
        assertEquals("Software Engineer", response.getBody().getTitle());
        assertEquals(ExperienceLevel.MID, response.getBody().getExperienceLevel());
        assertEquals(JobType.FULL_TIME, response.getBody().getJobType());
        assertEquals(LocationType.REMOTE, response.getBody().getLocationType());

        verify(jobService, times(1)).createJob(jobRequest);
    }

    @Test
    void createJob_WithDifferentEnums_Success() {
        // Arrange
        JobRequest contractJobRequest = JobRequest.builder()
                .companyId(2L)
                .title("Contract Developer")
                .description("Contract development work")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.CONTRACT)
                .locationType(LocationType.ONSITE)
                .jobLocation("London")
                .salaryMin(90000)
                .salaryMax(130000)
                .deadline(Instant.now().plusSeconds(259200))
                .build();

        JobResponse contractJobResponse = JobResponse.builder()
                .jobId(2L)
                .title("Contract Developer")
                .description("Contract development work")
                .companyName("Contract Corp")
                .jobLocation("London")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.CONTRACT)
                .locationType(LocationType.ONSITE)
                .createdAt(Instant.now())
                .salaryMin(90000)
                .salaryMax(130000)
                .deadline(Instant.now().plusSeconds(259200))
                .build();

        when(jobService.createJob(any(JobRequest.class))).thenReturn(contractJobResponse);

        // Act
        ResponseEntity<JobResponse> response = jobController.createJob(contractJobRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ExperienceLevel.SENIOR, response.getBody().getExperienceLevel());
        assertEquals(JobType.CONTRACT, response.getBody().getJobType());
        assertEquals(LocationType.ONSITE, response.getBody().getLocationType());

        verify(jobService, times(1)).createJob(contractJobRequest);
    }

    @Test
    void createJob_WithAllExperienceLevels_Success() {
        // Test all experience levels
        ExperienceLevel[] allLevels = ExperienceLevel.values();

        for (ExperienceLevel level : allLevels) {
            JobRequest request = JobRequest.builder()
                    .companyId(1L)
                    .title("Test Job - " + level)
                    .experienceLevel(level)
                    .jobType(JobType.FULL_TIME)
                    .locationType(LocationType.REMOTE)
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            JobResponse response = JobResponse.builder()
                    .jobId(1L)
                    .title("Test Job - " + level)
                    .experienceLevel(level)
                    .jobType(JobType.FULL_TIME)
                    .locationType(LocationType.REMOTE)
                    .companyName("Test Corp")
                    .createdAt(Instant.now())
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            when(jobService.createJob(request)).thenReturn(response);

            ResponseEntity<JobResponse> result = jobController.createJob(request);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(level, result.getBody().getExperienceLevel());
        }
    }

    @Test
    void createJob_WithAllJobTypes_Success() {
        // Test all job types
        JobType[] allTypes = JobType.values();

        for (JobType type : allTypes) {
            JobRequest request = JobRequest.builder()
                    .companyId(1L)
                    .title("Test Job - " + type)
                    .experienceLevel(ExperienceLevel.MID)
                    .jobType(type)
                    .locationType(LocationType.REMOTE)
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            JobResponse response = JobResponse.builder()
                    .jobId(1L)
                    .title("Test Job - " + type)
                    .experienceLevel(ExperienceLevel.MID)
                    .jobType(type)
                    .locationType(LocationType.REMOTE)
                    .companyName("Test Corp")
                    .createdAt(Instant.now())
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            when(jobService.createJob(request)).thenReturn(response);

            ResponseEntity<JobResponse> result = jobController.createJob(request);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(type, result.getBody().getJobType());
        }
    }

    @Test
    void createJob_WithAllLocationTypes_Success() {
        // Test all location types
        LocationType[] allLocationTypes = LocationType.values();

        for (LocationType locationType : allLocationTypes) {
            JobRequest request = JobRequest.builder()
                    .companyId(1L)
                    .title("Test Job - " + locationType)
                    .experienceLevel(ExperienceLevel.MID)
                    .jobType(JobType.FULL_TIME)
                    .locationType(locationType)
                    .jobLocation(locationType == LocationType.REMOTE ? "Remote" : "Office Location")
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            JobResponse response = JobResponse.builder()
                    .jobId(1L)
                    .title("Test Job - " + locationType)
                    .experienceLevel(ExperienceLevel.MID)
                    .jobType(JobType.FULL_TIME)
                    .locationType(locationType)
                    .jobLocation(locationType == LocationType.REMOTE ? "Remote" : "Office Location")
                    .companyName("Test Corp")
                    .createdAt(Instant.now())
                    .salaryMin(50000)
                    .salaryMax(100000)
                    .deadline(Instant.now().plusSeconds(86400))
                    .build();

            when(jobService.createJob(request)).thenReturn(response);

            ResponseEntity<JobResponse> result = jobController.createJob(request);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(locationType, result.getBody().getLocationType());
        }
    }

    @Test
    void getCurrentJobs_Success() {
        // Arrange
        Long companyId = 1L;
        int page = 0;
        int size = 10;

        when(jobService.getCurrentJobs(eq(companyId), eq(page), eq(size))).thenReturn(jobResponsePage);

        // Act
        ResponseEntity<Page<JobResponse>> response = jobController.getCurrentJobs(companyId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(ExperienceLevel.MID, response.getBody().getContent().get(0).getExperienceLevel());

        verify(jobService, times(1)).getCurrentJobs(companyId, page, size);
    }

    @Test
    void getEndedJobs_Success() {
        // Arrange
        Long companyId = 1L;
        int page = 0;
        int size = 10;

        when(jobService.getEndedJobs(eq(companyId), eq(page), eq(size))).thenReturn(jobResponsePage);

        // Act
        ResponseEntity<Page<JobResponse>> response = jobController.getEndedJobs(companyId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(JobType.FULL_TIME, response.getBody().getContent().get(0).getJobType());

        verify(jobService, times(1)).getEndedJobs(companyId, page, size);
    }

    @Test
    void deleteJob_Success() {
        // Arrange
        Long jobId = 1L;
        doNothing().when(jobService).deleteJob(jobId);

        // Act
        ResponseEntity<Void> response = jobController.deleteJob(jobId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(jobService, times(1)).deleteJob(jobId);
    }

    @Test
    void updateJob_Success() {
        // Arrange
        Long jobId = 1L;

        // Create an updated response with different enum values
        JobResponse updatedResponse = JobResponse.builder()
                .jobId(jobId)
                .title("Senior Software Engineer")
                .description("Senior development role")
                .companyName("Tech Corp")
                .jobLocation("San Francisco")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.HYBRID)
                .createdAt(Instant.now())
                .salaryMin(100000)
                .salaryMax(150000)
                .deadline(Instant.now().plusSeconds(172800))
                .build();

        when(jobService.updateJob(eq(jobId), any(JobUpdateRequest.class))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<JobResponse> response = jobController.updateJob(jobId, jobUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ExperienceLevel.SENIOR, response.getBody().getExperienceLevel());
        assertEquals(LocationType.HYBRID, response.getBody().getLocationType());

        verify(jobService, times(1)).updateJob(jobId, jobUpdateRequest);
    }

    @Test
    void updateJob_WithEnumChangesOnly() {
        // Arrange
        Long jobId = 1L;

        // Update only enum values
        JobUpdateRequest enumUpdateRequest = JobUpdateRequest.builder()
                .experienceLevel(ExperienceLevel.LEAD)
                .jobType(JobType.PART_TIME)
                .locationType(LocationType.ONSITE)
                .build();

        JobResponse updatedResponse = JobResponse.builder()
                .jobId(jobId)
                .title("Software Engineer") // Original title
                .companyName("Tech Corp")
                .experienceLevel(ExperienceLevel.LEAD)
                .jobType(JobType.PART_TIME)
                .locationType(LocationType.ONSITE)
                .createdAt(Instant.now())
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        when(jobService.updateJob(eq(jobId), any(JobUpdateRequest.class))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<JobResponse> response = jobController.updateJob(jobId, enumUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ExperienceLevel.LEAD, response.getBody().getExperienceLevel());
        assertEquals(JobType.PART_TIME, response.getBody().getJobType());
        assertEquals(LocationType.ONSITE, response.getBody().getLocationType());

        verify(jobService, times(1)).updateJob(jobId, enumUpdateRequest);
    }

    @Test
    void createJob_WithNullRequest_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> jobController.createJob(null));
    }

    @Test
    void getCurrentJobs_WithNegativePage() {
        // Arrange
        Long companyId = 1L;
        int page = -1;
        int size = 10;

        when(jobService.getCurrentJobs(eq(companyId), eq(page), eq(size))).thenReturn(jobResponsePage);

        // Act
        ResponseEntity<Page<JobResponse>> response = jobController.getCurrentJobs(companyId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(jobService, times(1)).getCurrentJobs(companyId, page, size);
    }

    @Test
    void deleteJob_WithNullJobId_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> jobController.deleteJob(null));
    }

    @Test
    void updateJob_WithNullJobId_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> jobController.updateJob(null, jobUpdateRequest));
    }

    @Test
    void updateJob_WithNullRequest_ShouldThrowException() {
        // Arrange
        Long jobId = 1L;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> jobController.updateJob(jobId, null));
    }

    @Test
    void testEnumSerializationInResponses() {
        // Arrange
        JobResponse responseWithEnums = JobResponse.builder()
                .jobId(1L)
                .title("Test Job")
                .experienceLevel(ExperienceLevel.EXECUTIVE)
                .jobType(JobType.INTERNSHIP)
                .locationType(LocationType.HYBRID)
                .companyName("Test Corp")
                .createdAt(Instant.now())
                .salaryMin(50000)
                .salaryMax(100000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        when(jobService.createJob(any(JobRequest.class))).thenReturn(responseWithEnums);

        // Act
        ResponseEntity<JobResponse> response = jobController.createJob(jobRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(ExperienceLevel.EXECUTIVE, response.getBody().getExperienceLevel());
        assertEquals(JobType.INTERNSHIP, response.getBody().getJobType());
        assertEquals(LocationType.HYBRID, response.getBody().getLocationType());
    }

    @Test
    void testMultipleJobsWithDifferentEnums() {
        // Arrange
        JobResponse job1 = JobResponse.builder()
                .jobId(1L)
                .title("Entry Level Developer")
                .experienceLevel(ExperienceLevel.ENTRY)
                .jobType(JobType.INTERNSHIP)
                .locationType(LocationType.REMOTE)
                .companyName("Startup Inc")
                .createdAt(Instant.now())
                .salaryMin(30000)
                .salaryMax(50000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();

        JobResponse job2 = JobResponse.builder()
                .jobId(2L)
                .title("Lead Architect")
                .experienceLevel(ExperienceLevel.LEAD)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.ONSITE)
                .companyName("Enterprise Corp")
                .createdAt(Instant.now())
                .salaryMin(150000)
                .salaryMax(250000)
                .deadline(Instant.now().plusSeconds(172800))
                .build();

        JobResponse job3 = JobResponse.builder()
                .jobId(3L)
                .title("Freelance Designer")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.FREELANCE)
                .locationType(LocationType.HYBRID)
                .companyName("Creative Agency")
                .createdAt(Instant.now())
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().plusSeconds(259200))
                .build();

        List<JobResponse> mixedJobs = List.of(job1, job2, job3);
        Page<JobResponse> mixedPage = new PageImpl<>(mixedJobs, PageRequest.of(0, 10), mixedJobs.size());

        Long companyId = 1L;
        when(jobService.getCurrentJobs(eq(companyId), eq(0), eq(10))).thenReturn(mixedPage);

        // Act
        ResponseEntity<Page<JobResponse>> response = jobController.getCurrentJobs(companyId, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getBody().getTotalElements());

        List<JobResponse> content = response.getBody().getContent();
        assertEquals(ExperienceLevel.ENTRY, content.get(0).getExperienceLevel());
        assertEquals(JobType.INTERNSHIP, content.get(0).getJobType());

        assertEquals(ExperienceLevel.LEAD, content.get(1).getExperienceLevel());
        assertEquals(LocationType.ONSITE, content.get(1).getLocationType());

        assertEquals(JobType.FREELANCE, content.get(2).getJobType());
        assertEquals(LocationType.HYBRID, content.get(2).getLocationType());
    }
}