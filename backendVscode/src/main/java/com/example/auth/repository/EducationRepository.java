package com.example.auth.repository;

import com.example.auth.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByProfile_ProfileId(Long profileId);
}
