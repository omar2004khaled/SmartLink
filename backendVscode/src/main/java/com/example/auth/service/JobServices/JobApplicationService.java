package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.ApplicationDTO;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.entity.User;
import com.example.auth.enums.ApplicationStatus;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
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
    @Autowired
    public JobApplicationService(JobRepository jobRepository, UserRepository userRepository, JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }
    public Long savePost(ApplicationDTO applicationDTO){
            return jobApplicationRepository.save(jobApplication(applicationDTO)).getId();
    }
    public JobApplication jobApplication(ApplicationDTO applicationDTO){
        if(jobApplicationRepository.getApplicationByUserAndJob(applicationDTO.getUserId(),applicationDTO.getJobId()).isEmpty())
            throw new RuntimeException("you applied before");
        Optional<Job> opJob=jobRepository.findById(applicationDTO.getJobId());
        Optional<User> opUser=userRepository.findById(applicationDTO.getUserId());
        if(opJob.isEmpty()||opUser.isEmpty()) {
            throw new RuntimeException("there is no such job to be applied to or no such user to apply");
        }
        return JobApplication
                .builder()
                .job(opJob.get())
                .user(opUser.get())
                .cvURL(applicationDTO.getCvURL())
                .createdAt(Instant.now())
                .build();
    }
}
