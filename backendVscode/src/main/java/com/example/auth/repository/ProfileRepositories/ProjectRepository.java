package com.example.auth.repository.ProfileRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProfile_ProfileId(Long profileId);
}
