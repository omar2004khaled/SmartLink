package com.example.auth.entity.ProfileEntities;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.auth.entity.User;


@Entity
@Table(name = "JobSeekerProfile")

public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfileId")
    private Long profileId;

    @Column(name = "ProfilePicUrl", columnDefinition = "TEXT")
    private String profilePicUrl;

    @Column(name = "Bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "Headline")
    private String headline;

    @Column(name = "Gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "LocationId")
    private Location location;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> experiences = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "Works_On",
        joinColumns = @JoinColumn(name = "ProfileId"),
        inverseJoinColumns = @JoinColumn(name = "ProjectId"))
    private Set<Project> projects = new HashSet<>();

    public enum Gender { MALE, FEMALE }


    public Long getProfileId() {
        return this.profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfilePicUrl() {
        return this.profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Education> getEducations() {
        return this.educations;
    }

    public void setEducations(Set<Education> educations) {
        this.educations = educations;
    }

    public Set<Skill> getSkills() {
        return this.skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Set<Experience> getExperiences() {
        return this.experiences;
    }

    public void setExperiences(Set<Experience> experiences) {
        this.experiences = experiences;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }


    public JobSeekerProfile(Long profileId, String profilePicUrl, String bio, String headline, Gender gender, Location location, LocalDate birthDate, User user, Set<Education> educations, Set<Skill> skills, Set<Experience> experiences, Set<Project> projects) {
        this.profileId = profileId;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
        this.headline = headline;
        this.gender = gender;
        this.location = location;
        this.birthDate = birthDate;
        this.user = user;
        this.educations = educations;
        this.skills = skills;
        this.experiences = experiences;
        this.projects = projects;
    }

    public JobSeekerProfile() {
    }
}