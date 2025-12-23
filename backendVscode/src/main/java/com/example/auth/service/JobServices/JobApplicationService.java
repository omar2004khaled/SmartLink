package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.entity.User;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.NotificationService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JobApplicationService {
    JobRepository jobRepository;
    UserRepository userRepository;
    JobApplicationRepository jobApplicationRepository;
    NotificationService notificationService;

    @Autowired
    public JobApplicationService(JobRepository jobRepository, UserRepository userRepository,
            JobApplicationRepository jobApplicationRepository,
            NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.notificationService = notificationService;
    }

    public Long savePost(ApplicationDTO applicationDTO) {
        JobApplication application = jobApplication(applicationDTO);
        Long applicationId = jobApplicationRepository.save(application).getId();

        // Create notification for job poster (company)
        try {
            Job job = jobRepository.findById(applicationDTO.getJobId()).orElseThrow();
            User applicant = userRepository.findById(applicationDTO.getUserId()).orElseThrow();

            notificationService.createNotification(
                    job.getCompany().getId(), // Job poster (company)
                    com.example.auth.enums.NotificationType.JOB_APPLICATION,
                    "New Job Application",
                    applicant.getFullName() + " applied for " + job.getTitle(),
                    applicationDTO.getJobId(),
                    "JOB");
        } catch (Exception e) {
            // Log error but don't fail the application operation
            System.err.println("Failed to create job application notification: " + e.getMessage());
        }

        return applicationId;
    }

    public JobApplication jobApplication(ApplicationDTO applicationDTO) {
        Optional<Job> opJob = jobRepository.findById(applicationDTO.getJobId());
        Optional<User> opUser = userRepository.findById(applicationDTO.getUserId());
        if (opJob.isEmpty() || opUser.isEmpty()) {
            throw new RuntimeException("there is no such job to be applied to or no such user to apply");
        }
        if (!jobApplicationRepository.getApplicationByUserAndJob(applicationDTO.getUserId(), applicationDTO.getJobId())
                .isEmpty())
            throw new RuntimeException("you applied before");
        return JobApplication
                .builder()
                .job(opJob.get())
                .user(opUser.get())
                .cvURL(applicationDTO.getCvURL())
                .createdAt(Instant.now())
                .build();
    }
}
