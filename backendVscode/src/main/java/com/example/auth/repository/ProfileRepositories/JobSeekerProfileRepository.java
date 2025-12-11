package com.example.auth.repository.ProfileRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import java.util.Optional;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {
    Optional<JobSeekerProfile> findByUser_Id(Long userId);
}
