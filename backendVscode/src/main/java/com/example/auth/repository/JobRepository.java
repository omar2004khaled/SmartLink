package com.example.auth.repository;

import com.example.auth.entity.Job;
import com.example.auth.enums.ExperienceLevel;
import com.example.auth.enums.JobType;
import com.example.auth.enums.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
        @Query("SELECT j FROM Job j WHERE " +
                "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
                "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
                "(:jobType IS NULL OR j.jobType = :jobType) AND " +
                "(:locationType IS NULL OR j.locationType = :locationType) AND " +
                "(:jobLocation IS NULL OR LOWER(j.jobLocation) LIKE LOWER(CONCAT('%', :jobLocation, '%'))) AND " +
                "(:minSalary IS NULL OR j.salaryMax >= :minSalary) AND " +
                "(:maxSalary IS NULL OR j.salaryMin <= :maxSalary)")
        List<Job> findByFilter(
                @Param("title") String title,
                @Param("experienceLevel") ExperienceLevel experienceLevel,
                @Param("jobType") JobType jobType,
                @Param("locationType") LocationType locationType,
                @Param("jobLocation") String jobLocation,
                @Param("minSalary") Integer minSalary,
                @Param("maxSalary") Integer maxSalary
        );

}
