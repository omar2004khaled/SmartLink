package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Company_Followers")
@IdClass(CompanyFollower.CompanyFollowerId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFollower {

    @Id
    @Column(name = "FollowerId")
    private Long followerId;

    @Id
    @Column(name = "CompanyId")
    private Long companyId;

    @Column(name = "FollowedAt")
    private LocalDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        followedAt = LocalDateTime.now();
    }

    // Composite Key Class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyFollowerId implements Serializable {
        private Long followerId;
        private Long companyId;
    }
}