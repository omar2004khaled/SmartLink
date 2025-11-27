package com.example.auth.entity.ProfileEntities;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Experience")
public class Experience {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ExperienceId")
    private Long experienceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ProfileId")
    private JobSeekerProfile profile;

    @Column(name="CompanyName")
    private String companyName;

    @Column(name="Title")
    private String title;

    @Column(name="Location")
    private String location;

    @Column(name="StartDate")
    private LocalDate startDate;

    @Column(name="EndDate")
    private LocalDate endDate;

    @Column(name="Description", columnDefinition = "TEXT")
    private String description;


    public Long getExperienceId() {
        return this.experienceId;
    }

    public void setExperienceId(Long experienceId) {
        this.experienceId = experienceId;
    }

    public JobSeekerProfile getProfile() {
        return this.profile;
    }

    public void setProfile(JobSeekerProfile profile) {
        this.profile = profile;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Experience() {
    }

    public Experience(Long experienceId, JobSeekerProfile profile, String companyName, String title, String location, LocalDate startDate, LocalDate endDate, String description) {
        this.experienceId = experienceId;
        this.profile = profile;
        this.companyName = companyName;
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
}