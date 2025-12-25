package com.example.auth.dto;

import com.example.auth.dto.JobDTO.JobResponse;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;

public class JobResponseDTOTest {

    @Test
    void testJobResponseBuilderAndGetters() {
        // Given
        Instant now = Instant.now();
        Instant deadline = now.plusSeconds(86400); // 1 day later

        // When
        JobResponse jobResponse = JobResponse.builder()
                .jobId(1L)
                .title("Software Engineer")
                .description("Develop software applications")
                .companyName("Tech Corp")
                .jobLocation("San Francisco, CA")
                .experienceLevel(ExperienceLevel.MID)
                .jobType(JobType.FULL_TIME)
                .locationType(LocationType.REMOTE)
                .createdAt(now)
                .salaryMin(80000)
                .salaryMax(120000)
                .deadline(deadline)
                .isApplied(false)  // <- Add this line
                .build();

        // Then
        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Software Engineer", jobResponse.getTitle());
        assertEquals("Develop software applications", jobResponse.getDescription());
        assertEquals("Tech Corp", jobResponse.getCompanyName());
        assertEquals("San Francisco, CA", jobResponse.getJobLocation());
        assertEquals(ExperienceLevel.MID, jobResponse.getExperienceLevel());
        assertEquals(JobType.FULL_TIME, jobResponse.getJobType());
        assertEquals(LocationType.REMOTE, jobResponse.getLocationType());
        assertEquals(now, jobResponse.getCreatedAt());
        assertEquals(80000, jobResponse.getSalaryMin());
        assertEquals(120000, jobResponse.getSalaryMax());
        assertEquals(deadline, jobResponse.getDeadline());
        assertFalse(jobResponse.isApplied());  // <- Add this assertion
    }

    @Test
    void testNoArgsConstructor() {
        // When
        JobResponse jobResponse = new JobResponse();

        // Then
        assertNotNull(jobResponse);
        assertNull(jobResponse.getJobId());
        assertNull(jobResponse.getTitle());
        assertNull(jobResponse.getDescription());
        assertNull(jobResponse.getCompanyName());
        assertNull(jobResponse.getJobLocation());
        assertNull(jobResponse.getExperienceLevel());
        assertNull(jobResponse.getJobType());
        assertNull(jobResponse.getLocationType());
        assertNull(jobResponse.getCreatedAt());
        assertNull(jobResponse.getSalaryMin());
        assertNull(jobResponse.getSalaryMax());
        assertNull(jobResponse.getDeadline());
        assertFalse(jobResponse.isApplied());  // Default should be false
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Instant now = Instant.now();
        Instant deadline = now.plusSeconds(86400);

        // When - Updated to include isApplied parameter
        JobResponse jobResponse = new JobResponse(
                1L,
                "Software Engineer",
                "Develop software applications",
                "Tech Corp",
                "San Francisco, CA",
                ExperienceLevel.MID,
                JobType.FULL_TIME,
                LocationType.REMOTE,
                now,
                80000,
                120000,
                deadline,
                false  // <- Add this parameter
        );

        // Then
        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Software Engineer", jobResponse.getTitle());
        assertEquals(ExperienceLevel.MID, jobResponse.getExperienceLevel());
        assertFalse(jobResponse.isApplied());  // <- Verify the new field
    }

    @Test
    void testSetterMethods() {
        // Given
        JobResponse jobResponse = new JobResponse();
        Instant now = Instant.now();

        // When
        jobResponse.setJobId(1L);
        jobResponse.setTitle("Software Engineer");
        jobResponse.setDescription("Develop software");
        jobResponse.setCompanyName("Tech Corp");
        jobResponse.setJobLocation("NYC");
        jobResponse.setExperienceLevel(ExperienceLevel.SENIOR);
        jobResponse.setJobType(JobType.CONTRACT);
        jobResponse.setLocationType(LocationType.HYBRID);
        jobResponse.setCreatedAt(now);
        jobResponse.setSalaryMin(90000);
        jobResponse.setSalaryMax(150000);
        jobResponse.setDeadline(now.plusSeconds(86400));
        jobResponse.setApplied(true);  // <- Add this line

        // Then
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Software Engineer", jobResponse.getTitle());
        assertEquals(ExperienceLevel.SENIOR, jobResponse.getExperienceLevel());
        assertEquals(JobType.CONTRACT, jobResponse.getJobType());
        assertEquals(LocationType.HYBRID, jobResponse.getLocationType());
        assertEquals(90000, jobResponse.getSalaryMin());
        assertEquals(150000, jobResponse.getSalaryMax());
        assertTrue(jobResponse.isApplied());  // <- Add this assertion
    }

    @Test
    void testToString() {
        // Given
        JobResponse jobResponse = JobResponse.builder()
                .jobId(1L)
                .title("Test Job")
                .isApplied(true)  // <- Add this line
                .build();

        // When
        String toString = jobResponse.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Test Job"));
        assertTrue(toString.contains("isApplied"));  // May check for field in toString
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        JobResponse job1 = JobResponse.builder()
                .jobId(1L)
                .title("Job A")
                .isApplied(false)  // <- Add this line
                .build();

        JobResponse job2 = JobResponse.builder()
                .jobId(1L)
                .title("Job A")
                .isApplied(false)  // <- Add this line
                .build();

        JobResponse job3 = JobResponse.builder()
                .jobId(2L)
                .title("Job B")
                .isApplied(true)   // <- Different value
                .build();

        // Then
        assertEquals(job1, job2);
        assertNotEquals(job1, job3);
        assertEquals(job1.hashCode(), job2.hashCode());
        assertNotEquals(job1.hashCode(), job3.hashCode());
    }

    @Test
    void testIsAppliedDefaultValue() {
        // When using builder without specifying isApplied
        JobResponse jobResponse = JobResponse.builder()
                .jobId(1L)
                .title("Test Job")
                .build();

        // Then - Should default to false
        assertFalse(jobResponse.isApplied());
    }

    @Test
    void testIsAppliedTrue() {
        // When
        JobResponse jobResponse = JobResponse.builder()
                .jobId(1L)
                .title("Test Job")
                .isApplied(true)
                .build();

        // Then
        assertTrue(jobResponse.isApplied());
    }
}