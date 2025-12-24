package com.example.auth.repository.ProfileRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Education;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByProfile_ProfileId(Long profileId);
    @Query("SELECT e FROM Education e where e.profile.profileId = :profileId")
    List <Education> findDescriptionAndFieldOfEducation(@Param("profileId") Long profileId);
}
