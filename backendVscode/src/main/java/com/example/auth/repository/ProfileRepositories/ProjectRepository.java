package com.example.auth.repository.ProfileRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
