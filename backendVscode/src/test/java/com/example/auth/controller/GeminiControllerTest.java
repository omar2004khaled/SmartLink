package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import com.example.auth.service.GeminiService.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiControllerTest {

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private GeminiController geminiController;

    private List<JobResponse> mockJobs;

    @BeforeEach
    void setUp() {
        mockJobs = List.of(
                JobResponse.builder()
                        .jobId(1L)
                        .title("Software Engineer")
                        .description("Develop backend services")
                        .companyName("Tech Corp")
                        .jobLocation("Remote")
                        .experienceLevel(ExperienceLevel.MID)
                        .jobType(JobType.FULL_TIME)
                        .locationType(LocationType.REMOTE)
                        .createdAt(Instant.now())
                        .salaryMin(80000)
                        .salaryMax(120000)
                        .deadline(Instant.now().plusSeconds(86400 * 30))
                        .build(),
                JobResponse.builder()
                        .jobId(2L)
                        .title("Frontend Developer")
                        .description("Build user interfaces")
                        .companyName("Web Inc")
                        .jobLocation("New York")
                        .experienceLevel(ExperienceLevel.JUNIOR)
                        .jobType(JobType.FULL_TIME)
                        .locationType(LocationType.HYBRID)
                        .createdAt(Instant.now())
                        .salaryMin(70000)
                        .salaryMax(100000)
                        .deadline(Instant.now().plusSeconds(86400 * 45))
                        .build()
        );
    }

    @Test
    void testGetJobs_Success() throws ExecutionException, InterruptedException {
        // Given
        Long profileId = 123L;
        when(geminiService.callGoogleApi(profileId)).thenReturn(mockJobs);

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);
        ResponseEntity<?> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);

        @SuppressWarnings("unchecked")
        List<JobResponse> responseJobs = (List<JobResponse>) response.getBody();
        assertEquals(2, responseJobs.size());
        assertEquals("Software Engineer", responseJobs.get(0).getTitle());
        assertEquals("Frontend Developer", responseJobs.get(1).getTitle());

        verify(geminiService, times(1)).callGoogleApi(profileId);
    }

    @Test
    void testGetJobs_EmptyList() throws ExecutionException, InterruptedException {
        // Given
        Long profileId = 456L;
        when(geminiService.callGoogleApi(profileId)).thenReturn(List.of());

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);
        ResponseEntity<?> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);

        @SuppressWarnings("unchecked")
        List<JobResponse> responseJobs = (List<JobResponse>) response.getBody();
        assertTrue(responseJobs.isEmpty());

        verify(geminiService, times(1)).callGoogleApi(profileId);
    }

    @Test
    void testGetJobs_ServiceThrowsException() throws ExecutionException, InterruptedException {
        // Given
        Long profileId = 789L;
        String errorMessage = "Service unavailable";
        when(geminiService.callGoogleApi(profileId))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);
        ResponseEntity<?> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to process request", errorBody.get("error"));
        assertEquals(errorMessage, errorBody.get("message"));

        verify(geminiService, times(1)).callGoogleApi(profileId);
    }

    @Test
    void testGetJobs_Timeout() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        Long profileId = 999L;

        // Mock a service that takes longer than timeout
        when(geminiService.callGoogleApi(profileId)).thenAnswer(invocation -> {
            Thread.sleep(130000); // Longer than 120 second timeout
            return mockJobs;
        });

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);

        // Try to get result with a shorter timeout than the controller's timeout
        try {
            future.get(1, TimeUnit.SECONDS);
            fail("Should have thrown TimeoutException");
        } catch (TimeoutException e) {
            // Expected - the future hasn't completed yet
        }

        // The controller should handle timeout internally
        // We need to wait for the timeout to occur
        Thread.sleep(125000); // Wait for timeout to happen

        // Now get the result
        ResponseEntity<?> response = future.get();

        // Then - should be timeout response
        assertNotNull(response);
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, String> timeoutBody = (Map<String, String>) response.getBody();
        assertEquals("Request timeout", timeoutBody.get("error"));
        assertEquals("The request took too long to process", timeoutBody.get("message"));
    }

    @Test
    void testGetJobs_NullProfileId() throws ExecutionException, InterruptedException {
        // Given
        Long profileId = null;

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);
        ResponseEntity<?> response = future.get();

        // Then - Service should handle null, controller should pass it through
        assertNotNull(response);
        // Either OK with empty list or error depending on service implementation
        verify(geminiService, times(1)).callGoogleApi(null);
    }

    @Test
    void testGetJobs_CompletableFutureCompletion() {
        // Given
        Long profileId = 123L;
        when(geminiService.callGoogleApi(profileId)).thenReturn(mockJobs);

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);

        // Then
        assertNotNull(future);
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());

        // Wait a bit for completion
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Should be done quickly since it's just a mock
        assertTrue(future.isDone());
    }

    @Test
    void testControllerInitialization() {
        // Given
        GeminiController controller = new GeminiController(geminiService);

        // Then
        assertNotNull(controller);
    }

    @Test
    void testGetJobs_WithZeroJobs() throws ExecutionException, InterruptedException {
        // Given
        Long profileId = 555L;
        when(geminiService.callGoogleApi(profileId)).thenReturn(List.of());

        // When
        CompletableFuture<ResponseEntity<?>> future = geminiController.getJobs(profileId);
        ResponseEntity<?> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        List<JobResponse> jobs = (List<JobResponse>) response.getBody();
        assertNotNull(jobs);
        assertEquals(0, jobs.size());
    }
}