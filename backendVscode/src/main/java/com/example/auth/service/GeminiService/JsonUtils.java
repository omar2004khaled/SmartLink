package com.example.auth.service.GeminiService;

import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting object to JSON: " + e.getMessage());
            return "{}"; // Return empty JSON object on error
        }
    }

    public static String toJsonString(Iterable<?> objects) {
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting iterable to JSON: " + e.getMessage());
            return "[]"; // Return empty JSON array on error
        }
    }
    public JobResponse mapper(Job job) {
        // Manually map the Job entity to JobResponse DTO
        // This avoids lazy loading issues with the company field
        return JobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompany() != null ? job.getCompany().getFullName() : null)
                .jobLocation(job.getJobLocation())
                .experienceLevel(job.getExperienceLevel())
                .jobType(job.getJobType())
                .locationType(job.getLocationType())
                .createdAt(job.getCreatedAt())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .deadline(job.getDeadline())
                .build();
    }
}