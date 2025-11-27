package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "CompanyProfile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompanyProfileId")
    private Long companyProfileId;

    @Column(name = "UserId")
    private Long userId;

    @Column(name = "CompanyName")
    private String companyName;

    @Column(name = "Website")
    private String website;

    @Column(name = "Industry")
    private String industry;

    @Column(name = "Description")
    private String description;

    @Column(name = "LogoUrl")
    private String logoUrl;

    @Column(name = "CoverImageUrl")
    private String coverImageUrl;

    @Column(name = "numberOfFollowers")
    private Long numberOfFollowers = 0L;

    @Column(name = "Founded")
    private Integer founded;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}