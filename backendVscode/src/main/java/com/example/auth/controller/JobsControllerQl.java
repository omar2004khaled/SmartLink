package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobFilter;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.entity.Job;
import com.example.auth.entity.JobApplication;
import com.example.auth.repository.JobApplicationRepository;
import com.example.auth.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class JobsControllerQl {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    JobApplicationRepository jobApplicationRepository;

    @QueryMapping
    public List<JobResponse> allJobs(@Argument JobFilter filter) {
        if(filter != null) {
            System.out.println(filter.toString());
        }

        List<Job> jobs;

        if (filter == null || isFilterEmpty(filter)) {
            jobs = jobRepository.findAll();
        } else {
            jobs = jobRepository.findByFilter(
                    filter.getTitle(),
                    filter.getExperienceLevel(),
                    filter.getJobType(),
                    filter.getLocationType(),
                    filter.getJobLocation(),
                    filter.getMinSalary(),
                    filter.getMaxSalary()
            );
        }

        return mapJobsToResponses(jobs,filter.getUserId());
    }

    private List<JobResponse> mapJobsToResponses(List<Job> jobs,Long userId) {
        return jobs.stream()
                .map((job)->mapJobToResponse(job,userId))
                .collect(Collectors.toList());
    }

    private JobResponse mapJobToResponse(Job job,Long userId) {
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
                .isApplied(!jobApplicationRepository.getApplicationByUserAndJob(userId, job.getJobId()).isEmpty())
                .build();
    }
    private boolean isFilterEmpty(JobFilter filter) {
        return filter.getTitle() == null &&
                filter.getExperienceLevel() == null &&
                filter.getJobType() == null &&
                filter.getLocationType() == null &&
                filter.getJobLocation() == null &&
                filter.getMinSalary() == null &&
                filter.getMaxSalary() == null;
    }
}
