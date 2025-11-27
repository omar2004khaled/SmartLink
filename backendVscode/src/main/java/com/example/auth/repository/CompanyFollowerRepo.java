package com.example.auth.repository;

import com.example.auth.entity.CompanyFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyFollowerRepo extends JpaRepository<CompanyFollower, CompanyFollower.CompanyFollowerId> {

    boolean existsByFollowerIdAndCompanyId(Long followerId, Long companyId);

    @Modifying
    @Query("DELETE FROM CompanyFollower cf WHERE cf.followerId = :followerId AND cf.companyId = :companyId")
    void deleteByFollowerIdAndCompanyId(@Param("followerId") Long followerId, @Param("companyId") Long companyId);

//    @Query("SELECT COUNT(cf) FROM CompanyFollower cf WHERE cf.companyId = :companyId")
//    Long countFollowersByCompanyId(@Param("companyId") Long companyId);
}