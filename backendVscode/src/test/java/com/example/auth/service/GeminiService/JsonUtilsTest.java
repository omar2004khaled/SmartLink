package com.example.auth.service.GeminiService;

import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void testToJsonString_WithObject() {
        // Given
        TestObject obj = new TestObject("John", 30);

        // When
        String json = JsonUtils.toJsonString(obj);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("John"));
        assertTrue(json.contains("30"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("age"));
    }


    @Test
    void testToJsonString_WithIterable() {
        // Given
        TestObject obj1 = new TestObject("Alice", 25);
        TestObject obj2 = new TestObject("Bob", 30);
        Iterable<TestObject> iterable = java.util.List.of(obj1, obj2);

        // When
        String json = JsonUtils.toJsonString(iterable);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("Alice"));
        assertTrue(json.contains("Bob"));
        assertTrue(json.contains("25"));
        assertTrue(json.contains("30"));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    void testToJsonString_WithEmptyIterable() {
        // Given
        Iterable<TestObject> emptyIterable = java.util.List.of();

        // When
        String json = JsonUtils.toJsonString(emptyIterable);

        // Then
        assertEquals("[]", json);
    }

    @Test
    void testToJsonString_WithNullIterable() {
        // When
        String json = JsonUtils.toJsonString((Iterable<?>) null);

        // Then
        assertEquals("[]", json);
    }

    @Test
    void testMapper_WithCompleteJob() {
        // Given
        JsonUtils jsonUtils = new JsonUtils();
        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Software Engineer");
        job.setDescription("Develop software");
        job.setJobLocation("Remote");
        job.setExperienceLevel(ExperienceLevel.MID);
        job.setJobType(JobType.FULL_TIME);
        job.setLocationType(LocationType.REMOTE);
        job.setCreatedAt(Instant.now());
        job.setSalaryMin(80000);
        job.setSalaryMax(120000);
        job.setDeadline(Instant.now().plusSeconds(86400));

        // Create a mock company
        User company = new User();
        company.setFullName("Tech Corp");
        job.setCompany(company);

        // When
        JobResponse jobResponse = jsonUtils.mapper(job);

        // Then
        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Software Engineer", jobResponse.getTitle());
        assertEquals("Develop software", jobResponse.getDescription());
        assertEquals("Tech Corp", jobResponse.getCompanyName());
        assertEquals("Remote", jobResponse.getJobLocation());
        assertEquals(ExperienceLevel.MID, jobResponse.getExperienceLevel());
        assertEquals(JobType.FULL_TIME, jobResponse.getJobType());
        assertEquals(LocationType.REMOTE, jobResponse.getLocationType());
        assertEquals(80000, jobResponse.getSalaryMin());
        assertEquals(120000, jobResponse.getSalaryMax());
    }

    @Test
    void testMapper_WithNullCompany() {
        // Given
        JsonUtils jsonUtils = new JsonUtils();
        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Software Engineer");
        job.setCompany(null); // Company is null

        // When
        JobResponse jobResponse = jsonUtils.mapper(job);

        // Then
        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Software Engineer", jobResponse.getTitle());
        assertNull(jobResponse.getCompanyName());
    }

    @Test
    void testMapper_WithNullJob() {
        // Given
        JsonUtils jsonUtils = new JsonUtils();

        // When
        JobResponse jobResponse = jsonUtils.mapper(null);

        // Then
        assertNull(jobResponse);
    }

    @Test
    void testMapper_WithPartialJob() {
        // Given
        JsonUtils jsonUtils = new JsonUtils();
        Job job = new Job();
        job.setJobId(1L);
        job.setTitle("Partial Job");
        // Other fields are null

        // When
        JobResponse jobResponse = jsonUtils.mapper(job);

        // Then
        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJobId());
        assertEquals("Partial Job", jobResponse.getTitle());
        assertNull(jobResponse.getDescription());
        assertNull(jobResponse.getCompanyName());
        assertNull(jobResponse.getJobLocation());
        assertNull(jobResponse.getExperienceLevel());
        assertNull(jobResponse.getJobType());
        assertNull(jobResponse.getLocationType());
        assertNull(jobResponse.getSalaryMin());
        assertNull(jobResponse.getSalaryMax());
        assertNull(jobResponse.getDeadline());
    }

    @Test
    void testToJsonString_WithInstantField() {
        // Given
        Instant instant = Instant.parse("2024-01-01T00:00:00Z");
        InstantObject obj = new InstantObject(instant, "Test");

        // When
        String json = JsonUtils.toJsonString(obj);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("2024-01-01T00:00:00Z"));
        assertTrue(json.contains("Test"));
    }

    // Helper test classes
    static class TestObject {
        private String name;
        private int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }

    static class InstantObject {
        private Instant timestamp;
        private String name;

        public InstantObject(Instant timestamp, String name) {
            this.timestamp = timestamp;
            this.name = name;
        }

        public Instant getTimestamp() { return timestamp; }
        public String getName() { return name; }
    }
}