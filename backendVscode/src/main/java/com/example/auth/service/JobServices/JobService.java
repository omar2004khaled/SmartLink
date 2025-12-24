package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.entity.User;
import com.example.auth.repository.CompanyFollowerRepo;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CompanyFollowerRepo companyFollowerRepo;
    private final JobApplicationRepository jobApplicationRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository,
            NotificationService notificationService, CompanyFollowerRepo companyFollowerRepo,
            JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.companyFollowerRepo = companyFollowerRepo;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    private Job getJob(User company, JobRequest request) {
        return Job.builder()
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

    }

    private JobResponse getResponse(User company, Job saved) {
        return JobResponse.builder()
                .jobId(saved.getJobId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .companyName(company.getFullName())
                .jobLocation(saved.getJobLocation())
                .experienceLevel(saved.getExperienceLevel())
                .jobType(saved.getJobType())
                .locationType(saved.getLocationType())
                .createdAt(saved.getCreatedAt())
                .salaryMin(saved.getSalaryMin())
                .salaryMax(saved.getSalaryMax())
                .deadline(saved.getDeadline())
                .build();
    }

    private JobResponse mapToJobResponse(Job job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompany().getFullName())
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

    public JobResponse createJob(JobRequest request) {
        User company = userRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.getUserType().equals("COMPANY")) {
            throw new RuntimeException("User is not a company");
        }
        Job job = getJob(company, request);
        Job saved = jobRepository.save(job);

        // Notify all followers about the new job post
        try {
            List<CompanyFollower> followers = companyFollowerRepo.findByCompany(company);
            for (CompanyFollower follower : followers) {
                try {
                    notificationService.createNewJobPostNotification(
                            follower.getFollower().getId(),
                            company.getFullName(),
                            saved.getTitle(),
                            saved.getJobId());
                } catch (Exception e) {
                    logger.error("Failed to create job post notification for follower {}: {}",
                            follower.getFollower().getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to notify followers about new job post: {}", e.getMessage());
        }

        return getResponse(company, saved);

    }

    public Page<JobResponse> getCurrentJobs(Long companyId, int page, int size) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Job> jobPage = jobRepository.findByCompanyAndDeadlineAfter(company, Instant.now(), pageable);
        return jobPage.map(this::mapToJobResponse);
    }

    public Page<JobResponse> getEndedJobs(Long companyId, int page, int size) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("deadline").ascending());
        Page<Job> jobPage = jobRepository.findByCompanyAndDeadlineBefore(company, Instant.now(), pageable);

        return jobPage.map(this::mapToJobResponse);
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Delete all applications for this job first to avoid foreign key constraint
        // violation
        List<JobApplication> applications = jobApplicationRepository.getApplicationByJobId(jobId)
                .orElse(List.of());

        if (!applications.isEmpty()) {
            jobApplicationRepository.deleteAll(applications);
        }

        // Now delete the job
        jobRepository.delete(job);
    }

    private void getJob(Job job, JobUpdateRequest request) {
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setJobType(request.getJobType());
        job.setLocationType(request.getLocationType());
        job.setJobLocation(request.getJobLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setDeadline(request.getDeadline());

    }

    public JobResponse updateJob(Long jobId, JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        getJob(job, request);
        Job updated = jobRepository.save(job);
        return getResponse(updated.getCompany(), updated);
    }
}