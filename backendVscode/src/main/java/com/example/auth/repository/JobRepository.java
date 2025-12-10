package com.example.auth.repository;

import com.example.auth.entity.Job;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByCompanyAndDeadlineAfter(User company, Instant now);
    List<Job> findByCompanyAndDeadlineBefore(User company, Instant now);

}
