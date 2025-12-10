package com.example.auth.service.JobServices;

import com.example.auth.dto.JobDTO.JobRequest;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.JobUpdateRequest;
import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;


    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    private Job getJob(User company,JobRequest request){
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
    private JobResponse getResponse(User company,Job saved){
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
    private List<JobResponse> getResponse(User company,List<Job> jobs){
        List<JobResponse> currentJobs=new ArrayList<>();
        for(Job j:jobs){
            currentJobs.add(getResponse(company,j));
        }
        return currentJobs;

    }
    public JobResponse createJob(JobRequest request) {
        User company = userRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.getUserType().equals("COMPANY")) {
            throw new RuntimeException("User is not a company");
        }
        Job job=getJob(company,request);
        Job saved = jobRepository.save(job);
        return getResponse(company,saved);


    }


    public List<JobResponse> getCurrentJobs(Long companyId) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        List<Job> jobs = jobRepository.findByCompanyAndDeadlineAfter(company, Instant.now());
        return getResponse(company,jobs);
    }

    public List<JobResponse> getEndedJobs(Long companyId) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        List<Job> jobs = jobRepository.findByCompanyAndDeadlineBefore(company, Instant.now());
        return getResponse(company,jobs);
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(job);
    }

    private void getJob(Job job,JobUpdateRequest request){
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
        getJob(job,request);
        Job updated = jobRepository.save(job);
        return getResponse(updated.getCompany(),updated);
    }
}