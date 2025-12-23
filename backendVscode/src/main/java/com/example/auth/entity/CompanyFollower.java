package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@IdClass(CompanyFollower.CompanyFollowerId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Company_Followers")
public class CompanyFollower {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FollowerId", nullable = false)
    private User follower;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CompanyId", nullable = false)
    private User company;

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
        private Long follower;
        private Long company;
    }
}