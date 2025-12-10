package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    // Add constructor for dependency injection
    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public JobResponse createJob(JobRequest request) {
        User company = userRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.getUserType().equals("COMPANY")) {
            throw new RuntimeException("User is not a company");
        }

        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .experienceLevel(request.getExperienceLevel())
                .jobType(request.getJobType())
                .locationType(request.getLocationType())
                .jobLocation(request.getJobLocation())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .deadline(request.getDeadline())
                .build();

        Job saved = jobRepository.save(job);

        return JobResponse.builder()
                .jobId(saved.getJobId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .companyName(company.getFullName())
                .jobLocation(saved.getJobLocation())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}