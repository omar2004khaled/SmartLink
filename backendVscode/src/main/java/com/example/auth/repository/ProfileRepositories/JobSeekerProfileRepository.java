package com.example.auth.repository.ProfileRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {
}
