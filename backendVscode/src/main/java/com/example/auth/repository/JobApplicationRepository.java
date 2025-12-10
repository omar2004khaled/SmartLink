package com.example.auth.repository;

import com.example.auth.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
}
