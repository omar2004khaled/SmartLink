package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.CompanyProfile;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.entity.User;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.repository.CompanyProfileRepo;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.NotificationService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {
    JobRepository jobRepository;
    UserRepository userRepository;
    JobApplicationRepository jobApplicationRepository;
    NotificationService notificationService;
    @Autowired
    public JobApplicationService(JobRepository jobRepository, UserRepository userRepository,
            JobApplicationRepository jobApplicationRepository,
            NotificationService notificationService,CompanyProfileRepo companyProfileRepo) {
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
                .coverLetter(applicationDTO.getCoverLetter())
                .createdAt(Instant.now())
                .applicationStatus(ApplicationStatus.PENDING)
                .build();
    }

    public List<ApplicationDTO> getByJobId(Long jobId){
        List<JobApplication> list = jobApplicationRepository.getApplicationByJobId(jobId).get();
        return list.stream()
                .map(this::toApplicationDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getByUserId(Long userId){
        List<JobApplication> list = jobApplicationRepository.getApplicationByUserId(userId).get();
        return list.stream()
                .map(this::toApplicationDTO)
                .collect(Collectors.toList());
    }

    public ApplicationDTO addComment(String comment, Long applicationId){

        JobApplication jobApp = helper(applicationId);
        List<String> tmpComments = jobApp.getComments();
        tmpComments.add(comment);
        jobApp.setComments(tmpComments);
        jobApplicationRepository.save(jobApp);

        return toApplicationDTO(jobApp);
    }

    private JobApplication helper(Long applicationId){
        Optional<JobApplication> optionalJobApplication = jobApplicationRepository.findById(applicationId);
        if(optionalJobApplication.isEmpty()) throw new RuntimeException("there is no such application");
        JobApplication jobApp = optionalJobApplication.get();
        return jobApp;
    }

    public ApplicationDTO changeStatus(String status, Long applicationId){
        JobApplication jobApp = helper(applicationId);
        jobApp.setApplicationStatus(ApplicationStatus.valueOf(status));
        jobApplicationRepository.save(jobApp);
        return toApplicationDTO(jobApp);
    }

    private ApplicationDTO toApplicationDTO(JobApplication jobApp) {
        return ApplicationDTO.builder()
                .id(jobApp.getId())
                .status(jobApp.getApplicationStatus().toString())
                .userId(jobApp.getUser().getId())
                .email(jobApp.getUser().getEmail())
                .name(jobApp.getUser().getFullName())
                .jobId(jobApp.getJob().getJobId())
                .cvURL(jobApp.getCvURL())
                .coverLetter(jobApp.getCoverLetter())
                .createdAt(LocalDateTime.ofInstant(jobApp.getCreatedAt(), ZoneId.systemDefault()))
                .comments(jobApp.getComments())
                .companyName(jobApp.getJob().getCompany().getFullName())
                .build();
    }
}