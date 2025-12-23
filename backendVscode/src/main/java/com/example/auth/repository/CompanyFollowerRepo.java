package com.example.auth.repository;

import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyFollowerRepo extends JpaRepository<CompanyFollower, CompanyFollower.CompanyFollowerId> {

    boolean existsByFollowerAndCompany(User follower, User company);

    @Modifying
    @Query("DELETE FROM CompanyFollower cf WHERE cf.follower = :follower AND cf.company = :company")
    void deleteByFollowerAndCompany(@Param("follower") User follower, @Param("company") User company);

    @Query("SELECT COUNT(cf) FROM CompanyFollower cf WHERE cf.company = :company")
    Long countFollowersByCompany(@Param("company") User company);
}