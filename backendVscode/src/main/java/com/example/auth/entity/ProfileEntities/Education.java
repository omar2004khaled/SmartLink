package com.example.auth.entity.ProfileEntities;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Education")
public class Education {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="EducationId")
    private Long educationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ProfileId")
    private JobSeekerProfile profile;

    @Column(name="School")
    private String school;

    @Column(name="FieldOfStudy")
    private String fieldOfStudy;

    @Column(name="StartDate")
    private LocalDate startDate;

    @Column(name="EndDate")
    private LocalDate endDate;

    @Column(name="Description", columnDefinition = "TEXT")
    private String description;


    public Long getEducationId() {
        return this.educationId;
    }

    public void setEducationId(Long educationId) {
        this.educationId = educationId;
    }

    public JobSeekerProfile getProfile() {
        return this.profile;
    }

    public void setProfile(JobSeekerProfile profile) {
        this.profile = profile;
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getFieldOfStudy() {
        return this.fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
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


    public Education() {
    }

    public Education(Long educationId, JobSeekerProfile profile, String school, String fieldOfStudy, LocalDate startDate, LocalDate endDate, String description) {
        this.educationId = educationId;
        this.profile = profile;
        this.school = school;
        this.fieldOfStudy = fieldOfStudy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
}