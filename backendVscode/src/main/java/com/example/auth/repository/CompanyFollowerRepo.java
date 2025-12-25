package com.example.auth.repository;

import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyFollowerRepo extends JpaRepository<CompanyFollower, CompanyFollower.CompanyFollowerId> {

    boolean existsByFollowerAndCompany(User follower, User company);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM CompanyFollower cf WHERE cf.follower = :follower AND cf.company = :company")
    void deleteByFollowerAndCompany(@Param("follower") User follower, @Param("company") User company);

    @Query("SELECT COUNT(cf) FROM CompanyFollower cf WHERE cf.company = :company")
    Long countFollowersByCompany(@Param("company") User company);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByFollower_Id(Long followerId);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByCompany_Id(Long companyId);


    @Query("SELECT cf.follower FROM CompanyFollower cf WHERE cf.company.id = :companyId")
    List<User> findAllFollowersByCompanyId(@Param("companyId") Long companyId);
}