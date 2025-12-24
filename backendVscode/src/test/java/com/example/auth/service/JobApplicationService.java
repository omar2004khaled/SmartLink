package com.example.auth.service;


import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.entity.User;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.JobServices.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private ApplicationDTO applicationDTO;
    private Job mockJob;
    private User mockUser;
    private JobApplication mockJobApplication;

    @BeforeEach
    void setUp() {
        // Setup ApplicationDTO
        applicationDTO = new ApplicationDTO();
        applicationDTO.setUserId(1L);
        applicationDTO.setJobId(100L);
        applicationDTO.setCvURL("https://example.com/cv.pdf");

        // Setup mock Job
        mockJob = new Job();
        mockJob.setJobId(100L);
        mockJob.setTitle("Software Engineer");

        // Setup mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("testuser");

        // Setup mock JobApplication
        mockJobApplication = JobApplication.builder()
                .id(1L)
                .job(mockJob)
                .user(mockUser)
                .cvURL("https://example.com/cv.pdf")
                .applicationStatus(ApplicationStatus.PENDING)
                .createdAt(Instant.now())
                .comments(new ArrayList<>(Arrays.asList("a","v")))
                .build();
    }

    @Test
    void savePost_withValidData_shouldReturnApplicationId() {
        when(jobApplicationRepository.getApplicationByUserAndJob(eq(1L), eq(100L)))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.of(mockJob));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenReturn(mockJobApplication);

        Long result = jobApplicationService.savePost(applicationDTO);
        assertNotNull(result);
        assertEquals(1L, result);
        verify(jobApplicationRepository, times(1)).getApplicationByUserAndJob(eq(1L), eq(100L));
        verify(jobRepository, times(1)).findById(eq(100L));
        verify(userRepository, times(1)).findById(eq(1L));
        verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    void savePost_whenUserAlreadyApplied_shouldThrowRuntimeException() {
        when(jobApplicationRepository.getApplicationByUserAndJob(eq(1L), eq(100L)))
                .thenReturn(Optional.of(mockJobApplication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(jobRepository.findById(100L)).thenReturn(Optional.of(mockJob));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobApplicationService.savePost(applicationDTO);
        });
        assertEquals("you applied before", exception.getMessage());
        verify(jobApplicationRepository, times(1)).getApplicationByUserAndJob(eq(1L), eq(100L));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void savePost_whenJobNotFound_shouldThrowRuntimeException() {
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.empty());
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobApplicationService.savePost(applicationDTO);
        });
        assertEquals("there is no such job to be applied to or no such user to apply", exception.getMessage());
        verify(jobRepository, times(1)).findById(eq(100L));
        verify(userRepository, times(1)).findById(eq(1L));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void savePost_whenUserNotFound_shouldThrowRuntimeException() {
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.of(mockJob));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobApplicationService.savePost(applicationDTO);
        });
        assertEquals("there is no such job to be applied to or no such user to apply", exception.getMessage());
        verify(jobRepository, times(1)).findById(eq(100L));
        verify(userRepository, times(1)).findById(eq(1L));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void savePost_whenBothJobAndUserNotFound_shouldThrowRuntimeException() {
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.empty());
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobApplicationService.savePost(applicationDTO);
        });
        assertEquals("there is no such job to be applied to or no such user to apply", exception.getMessage());
        verify(jobRepository, times(1)).findById(eq(100L));
        verify(userRepository, times(1)).findById(eq(1L));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void jobApplication_withValidData_shouldReturnJobApplication() {
        when(jobApplicationRepository.getApplicationByUserAndJob(eq(1L), eq(100L)))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.of(mockJob));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        JobApplication result = jobApplicationService.jobApplication(applicationDTO);
        assertNotNull(result);
        assertEquals(mockJob, result.getJob());
        assertEquals(mockUser, result.getUser());
        assertEquals("https://example.com/cv.pdf", result.getCvURL());
        assertNotNull(result.getCreatedAt());
        verify(jobApplicationRepository, times(1)).getApplicationByUserAndJob(eq(1L), eq(100L));
        verify(jobRepository, times(1)).findById(eq(100L));
        verify(userRepository, times(1)).findById(eq(1L));
    }

    @Test
    void jobApplication_withNullCvURL_shouldCreateApplicationWithNullCV() {
        applicationDTO.setCvURL(null);
        when(jobApplicationRepository.getApplicationByUserAndJob(eq(1L), eq(100L)))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.of(mockJob));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        JobApplication result = jobApplicationService.jobApplication(applicationDTO);
        assertNotNull(result);
        assertNull(result.getCvURL());
        assertEquals(mockJob, result.getJob());
        assertEquals(mockUser, result.getUser());
    }

    @Test
    void jobApplication_shouldSetCreatedAtToCurrentTime() {
        when(jobApplicationRepository.getApplicationByUserAndJob(eq(1L), eq(100L)))
                .thenReturn(Optional.empty());
        when(jobRepository.findById(eq(100L))).thenReturn(Optional.of(mockJob));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        Instant beforeCall = Instant.now();
        JobApplication result = jobApplicationService.jobApplication(applicationDTO);
        Instant afterCall = Instant.now();
        assertNotNull(result.getCreatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCall.minusSeconds(1)));
        assertTrue(result.getCreatedAt().isBefore(afterCall.plusSeconds(1)));
    }

    @Test
    void addCommentWithFalseJobId(){
        when(jobApplicationRepository.findById(eq(1L))).thenReturn(Optional.empty());
        RuntimeException exception=assertThrows(RuntimeException.class ,()->{
            jobApplicationService.addComment("a",1L);
        });
        verify(jobApplicationRepository,times(1)).findById(1L);
        assertEquals("there is no such application",exception.getMessage());
    }

    @Test
    void addCommentWithTrueJobId(){
        when(jobApplicationRepository.findById(eq(1L))).thenReturn(Optional.of(mockJobApplication));

        when(jobApplicationRepository.save(mockJobApplication)).thenReturn(null);
        ApplicationDTO result=jobApplicationService.addComment("a",1L);
        assertEquals(100L, result.getJobId());
        assertEquals(1L, result.getUserId());
        assertEquals("https://example.com/cv.pdf", result.getCvURL());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void changeStatusWithFalseId(){
        when(jobApplicationRepository.findById(eq(1L))).thenReturn(Optional.empty());
        RuntimeException exception=assertThrows(RuntimeException.class ,()->{
            jobApplicationService.addComment("a",1L);
        });
        verify(jobApplicationRepository,times(1)).findById(1L);
        assertEquals("there is no such application",exception.getMessage());
    }

    @Test
    void changeStatusWithTrueJobId(){
        when(jobApplicationRepository.findById(eq(1L))).thenReturn(Optional.of(mockJobApplication));
        when(jobApplicationRepository.save(mockJobApplication)).thenReturn(null);
        ApplicationDTO result=jobApplicationService.changeStatus("PENDING",1L);
        assertEquals(100L, result.getJobId());
        assertEquals(1L, result.getUserId());
        assertEquals("https://example.com/cv.pdf", result.getCvURL());
        assertNotNull(result.getCreatedAt());
    }

}