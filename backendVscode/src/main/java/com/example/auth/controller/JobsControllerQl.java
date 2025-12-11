package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobFilter;
import com.example.auth.entity.Job;
import com.example.auth.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class JobsControllerQl {

    @Autowired
    private JobRepository jobRepository;

    @QueryMapping
    public List<Job> allJobs(@Argument JobFilter filter) {
        if(filter!=null)
            System.out.println(filter.toString());
        if (filter == null || isFilterEmpty(filter)) {
            return jobRepository.findAll();
        }

        return jobRepository.findByFilter(
                filter.getTitle(),
                filter.getExperienceLevel(),
                filter.getJobType(),
                filter.getLocationType(),
                filter.getJobLocation(),
                filter.getMinSalary(),
                filter.getMaxSalary()
        );
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

    // ... other methods
}
