package com.example.auth.repository;

import com.example.auth.entity.CompanyProfile;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyProfileRepo extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByUser_Id(Long userId);

    @Query("SELECT c FROM CompanyProfile c WHERE c.companyProfileId = :companyId")
    Optional<CompanyProfile> findByCompanyProfileId(@Param("companyId") Long companyId);

    boolean existsByUser_Id(Long userId);
}