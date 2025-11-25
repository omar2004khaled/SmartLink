package com.example.auth.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ProjectId")
    private Long projectId;

    @Column(name="Title", nullable=false)
    private String title;

    @Column(name="Description", columnDefinition = "TEXT")
    private String description;

    @Column(name="ProjectUrl")
    private String projectUrl;

    @Column(name="StartDate")
    private LocalDate startDate;

    @Column(name="EndDate")
    private LocalDate endDate;


    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectUrl() {
        return this.projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
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


    public Project() {
    }

    public Project(Long projectId, String title, String description, String projectUrl, LocalDate startDate, LocalDate endDate) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.projectUrl = projectUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}