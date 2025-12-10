package com.example.auth.repository;

import com.example.auth.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user.id = :userId AND ja.job.id = :jobId")
    Optional<JobApplication> getApplicationByUserAndJob(@Param("userId") Long userId, @Param("jobId") Long jobId);
}
