package com.example.auth.repository;

import com.example.auth.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByProfile_ProfileId(Long profileId);
}
