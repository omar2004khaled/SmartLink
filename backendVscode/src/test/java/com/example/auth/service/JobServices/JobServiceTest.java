package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private JobService jobService;
    private User testCompany;
    private User testNonCompany;
    private Job testJob;
    private JobRequest jobRequest;
    private JobUpdateRequest jobUpdateRequest;

    @BeforeEach
    void setUp() {
        testCompany = new User();
        testCompany.setId(1L);
        testCompany.setFullName("Tech Corp");
        testCompany.setUserType("COMPANY");

        testNonCompany = new User();
        testNonCompany.setId(2L);
        testNonCompany.setFullName("John Doe");
        testNonCompany.setUserType("JOB_SEEKER");

        testJob = Job.builder()
                .jobId(1L)
                .company(testCompany)
                .title("Senior Java Developer")
                .description("Looking for an experienced Java developer")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.HYBRID)
                .jobLocation("New York, NY")
                .salaryMin(100000)
                .salaryMax(150000)
                .deadline(Instant.now().plus(30, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();

        jobRequest = JobRequest.builder()
                .companyId(1L)
                .title("Senior Java Developer")
                .description("Looking for an experienced Java developer")
                .experienceLevel(ExperienceLevel.SENIOR)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.HYBRID)
                .jobLocation("New York, NY")
                .salaryMin(100000)
                .salaryMax(150000)
                .deadline(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        jobUpdateRequest = JobUpdateRequest.builder()
                .title("Lead Java Developer")
                .description("Updated description")
                .experienceLevel(ExperienceLevel.LEAD)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .jobLocation("Remote")
                .salaryMin(120000)
                .salaryMax(180000)
                .deadline(Instant.now().plus(45, ChronoUnit.DAYS))
                .build();
    }

    @Test
    void createJob() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobResponse result = jobService.createJob(jobRequest);

        assertNotNull(result);
        assertEquals(testJob.getJobId(), result.getJobId());
        assertEquals(testJob.getTitle(), result.getTitle());
        assertEquals(testJob.getDescription(), result.getDescription());
        assertEquals(testCompany.getFullName(), result.getCompanyName());
        assertEquals(testJob.getJobLocation(), result.getJobLocation());
        assertEquals(testJob.getExperienceLevel(), result.getExperienceLevel());
        assertEquals(testJob.getJobType(), result.getJobType());
        assertEquals(testJob.getLocationType(), result.getLocationType());

        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void createJob_CompanyNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> jobService.createJob(jobRequest)
        );

        assertEquals("Company not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    void createJob_UserIsNotCompany() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(testNonCompany));
        jobRequest.setCompanyId(2L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> jobService.createJob(jobRequest)
        );

        assertEquals("User is not a company", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    void getCurrentJobs_ValidCompany() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Job> jobPage = new PageImpl<>(Arrays.asList(testJob));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobRepository.findByCompanyAndDeadlineAfter(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        )).thenReturn(jobPage);

        Page<JobResponse> result = jobService.getCurrentJobs(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testJob.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(testCompany.getFullName(), result.getContent().get(0).getCompanyName());

        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).findByCompanyAndDeadlineAfter(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        );
    }

    @Test
    void getCurrentJobs_CompanyNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> jobService.getCurrentJobs(1L, 0, 10)
        );

        assertEquals("Company not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, never()).findByCompanyAndDeadlineAfter(
                any(), any(), any()
        );
    }

    @Test
    void getCurrentJobs_NoJobs() {
        Page<Job> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobRepository.findByCompanyAndDeadlineAfter(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        Page<JobResponse> result = jobService.getCurrentJobs(1L, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).findByCompanyAndDeadlineAfter(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        );
    }

    @Test
    void getEndedJobs_ValidCompany() {
        Job endedJob = Job.builder()
                .jobId(2L)
                .company(testCompany)
                .title("Expired Position")
                .description("This job has ended")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.ONSITE)
                .jobLocation("Boston, MA")
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(Instant.now().minus(5, ChronoUnit.DAYS))
                .createdAt(Instant.now().minus(35, ChronoUnit.DAYS))
                .build();

        Page<Job> jobPage = new PageImpl<>(Arrays.asList(endedJob));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobRepository.findByCompanyAndDeadlineBefore(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        )).thenReturn(jobPage);

        Page<JobResponse> result = jobService.getEndedJobs(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(endedJob.getTitle(), result.getContent().get(0).getTitle());

        verify(userRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).findByCompanyAndDeadlineBefore(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        );
    }

    @Test
    void deleteJob_ValidJobId() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        doNothing().when(jobRepository).delete(testJob);

        jobService.deleteJob(1L);

        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).delete(testJob);
    }

    @Test
    void updateJob_ValidRequest() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobResponse result = jobService.updateJob(1L, jobUpdateRequest);

        assertNotNull(result);
        assertEquals(testJob.getJobId(), result.getJobId());
        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(testJob);

        assertEquals(jobUpdateRequest.getTitle(), testJob.getTitle());
        assertEquals(jobUpdateRequest.getDescription(), testJob.getDescription());
        assertEquals(jobUpdateRequest.getExperienceLevel(), testJob.getExperienceLevel());
        assertEquals(jobUpdateRequest.getJobType(), testJob.getJobType());
        assertEquals(jobUpdateRequest.getLocationType(), testJob.getLocationType());
        assertEquals(jobUpdateRequest.getJobLocation(), testJob.getJobLocation());
        assertEquals(jobUpdateRequest.getSalaryMin(), testJob.getSalaryMin());
        assertEquals(jobUpdateRequest.getSalaryMax(), testJob.getSalaryMax());
    }

    @Test
    void updateJob_PartialUpdate() {
        JobUpdateRequest partialUpdate = JobUpdateRequest.builder()
                .title("Updated Title Only")
                .description(testJob.getDescription())
                .experienceLevel(testJob.getExperienceLevel())
                .jobType(testJob.getJobType())
                .locationType(testJob.getLocationType())
                .jobLocation(testJob.getJobLocation())
                .salaryMin(testJob.getSalaryMin())
                .salaryMax(testJob.getSalaryMax())
                .deadline(testJob.getDeadline())
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        JobResponse result = jobService.updateJob(1L, partialUpdate);

        assertNotNull(result);
        assertEquals("Updated Title Only", testJob.getTitle());
        verify(jobRepository, times(1)).save(testJob);
    }

    @Test
    void getCurrentJobs_WithPagination() {
        Job job1 = Job.builder()
                .jobId(1L)
                .company(testCompany)
                .title("Job 1")
                .description("Description 1")
                .experienceLevel(ExperienceLevel.ENTRY)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.ONSITE)
                .jobLocation("Location 1")
                .deadline(Instant.now().plus(10, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();

        Job job2 = Job.builder()
                .jobId(2L)
                .company(testCompany)
                .title("Job 2")
                .description("Description 2")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.PART_TIME)
                .locationType(LocationType.REMOTE)
                .jobLocation("Location 2")
                .deadline(Instant.now().plus(20, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();

        Page<Job> jobPage = new PageImpl<>(Arrays.asList(job1, job2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobRepository.findByCompanyAndDeadlineAfter(
                eq(testCompany),
                any(Instant.class),
                any(Pageable.class)
        )).thenReturn(jobPage);

        Page<JobResponse> result = jobService.getCurrentJobs(1L, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Job 1", result.getContent().get(0).getTitle());
        assertEquals("Job 2", result.getContent().get(1).getTitle());
    }

}