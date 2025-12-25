package com.example.auth.service.GeminiService;

import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.JobServices.JobService;
import com.example.auth.service.NotificationService;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonUtilsTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CompanyFollowerRepo companyFollowerRepo;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobService jobService;


    private User company;
    private Job job;

    @BeforeEach
    void setUp() {
        company = User.builder()
                .id(1L)
                .fullName("Tech Corp")
                .userType("COMPANY")
                .build();

        job = Job.builder()
                .jobId(1L)
                .title("Software Engineer")
                .description("Develop software")
                .company(company)
                .jobLocation("Remote")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .createdAt(Instant.now())
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().plusSeconds(86400))
                .build();
    }

    @Test
    void mapToJobResponse_WithCompleteJob_ShouldReturnCorrectResponse() {
        // Act
        JobResponse response = jobService.mapToJobResponse(job);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getJobId());
        assertEquals("Software Engineer", response.getTitle());
        assertEquals("Develop software", response.getDescription());
        assertEquals("Tech Corp", response.getCompanyName());
        assertEquals("Remote", response.getJobLocation());
        assertEquals(ExperienceLevel.MID, response.getExperienceLevel());
        assertEquals(JobType.FULL_TIME, response.getJobType());
        assertEquals(LocationType.REMOTE, response.getLocationType());
        assertEquals(80000, response.getSalaryMin());
        assertEquals(120000, response.getSalaryMax());
    }

    @Test
    void mapToJobResponse_WithNullCompany_ShouldHandleGracefully() {
        // Arrange
        job.setCompany(null);

        // Act
        JobResponse response = jobService.mapToJobResponse(job);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getJobId());
        assertEquals("Software Engineer", response.getTitle());
        assertNull(response.getCompanyName()); // Company name should be null
    }

    @Test
    void createJob_WithValidRequest_ShouldCreateJob() {
        // Arrange
        com.example.auth.dto.JobDTO.JobRequest request =
                com.example.auth.dto.JobDTO.JobRequest.builder()
                        .companyId(1L)
                        .title("Software Engineer")
                        .description("Develop software")
                        .experienceLevel(ExperienceLevel.MID)
                        .jobType(JobType.FULL_TIME)
                        .locationType(LocationType.REMOTE)
                        .jobLocation("Remote")
                        .salaryMin(80000)
                        .salaryMax(120000)
                        .deadline(Instant.now().plusSeconds(86400))
                        .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(company));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        // Act
        JobResponse response = jobService.createJob(request);

        // Assert
        assertNotNull(response);
        assertEquals("Software Engineer", response.getTitle());
        assertEquals("Tech Corp", response.getCompanyName());
        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void createJob_WithNonCompanyUser_ShouldThrowException() {
        // Arrange
        com.example.auth.dto.JobDTO.JobRequest request =
                com.example.auth.dto.JobDTO.JobRequest.builder()
                        .companyId(1L)
                        .title("Software Engineer")
                        .build();

        User regularUser = User.builder()
                .id(1L)
                .fullName("Regular User")
                .userType("REGULAR") // Not a company
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobService.createJob(request));
        assertEquals("User is not a company", exception.getMessage());
    }

    @Test
    void getResponse_ShouldMapCorrectly() {
        // This tests the private getResponse method indirectly through createJob

        // Arrange
        com.example.auth.dto.JobDTO.JobRequest request =
                com.example.auth.dto.JobDTO.JobRequest.builder()
                        .companyId(1L)
                        .title("Test Job")
                        .description("Test Description")
                        .experienceLevel(ExperienceLevel.JUNIOR)
                        .jobType(JobType.PART_TIME)
                        .locationType(LocationType.HYBRID)
                        .jobLocation("Test Location")
                        .salaryMin(50000)
                        .salaryMax(80000)
                        .deadline(Instant.now().plusSeconds(86400))
                        .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(company));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        // Act
        JobResponse response = jobService.createJob(request);

        // Assert
        assertNotNull(response);
        assertEquals(job.getJobId(), response.getJobId());
        assertEquals(job.getTitle(), response.getTitle());
        assertEquals(company.getFullName(), response.getCompanyName());
    }
}