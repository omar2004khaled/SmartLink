package com.example.auth.entity.ProfileEntities;
import jakarta.persistence.*;

@Entity
@Table(name = "Skills")
public class Skill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SkillId")
    private Long skillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ProfileId")
    private JobSeekerProfile profile;

    @Column(name="SkillName")
    private String skillName;

    @Column(name="Proficiency")
    private String proficiency;


    public Long getSkillId() {
        return this.skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public JobSeekerProfile getProfile() {
        return this.profile;
    }

    public void setProfile(JobSeekerProfile profile) {
        this.profile = profile;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiency() {
        return this.proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }


    public Skill() {
    }
    
    public Skill(Long skillId, JobSeekerProfile profile, String skillName, String proficiency) {
        this.skillId = skillId;
        this.profile = profile;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }
}
