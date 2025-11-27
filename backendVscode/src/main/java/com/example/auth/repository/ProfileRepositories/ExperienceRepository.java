package com.example.auth.repository.ProfileRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Experience;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByProfile_ProfileId(Long profileId);
}
