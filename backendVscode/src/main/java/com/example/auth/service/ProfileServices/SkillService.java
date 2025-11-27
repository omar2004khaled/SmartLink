package com.example.auth.service.ProfileServices;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.auth.dto.ProfileDtos.SkillDto;
import com.example.auth.entity.ProfileEntities.JobSeekerProfile;
import com.example.auth.entity.ProfileEntities.Skill;
import com.example.auth.repository.ProfileRepositories.*;
import jakarta.transaction.Transactional;

@Service
public class SkillService {

    private final SkillRepository skillRepo;
    private final JobSeekerProfileRepository profileRepo;

    public SkillService(SkillRepository skillRepo, JobSeekerProfileRepository profileRepo) {
        this.skillRepo = skillRepo;
        this.profileRepo = profileRepo;
    }

    @Transactional
    public List<SkillDto> getSkillsForProfile(Long profileId) {
        List<Skill> skills = skillRepo.findByProfile_ProfileId(profileId);
        return skills.stream().map(this::toDto).toList();
    }

    @Transactional
    public SkillDto addSkillToProfile(Long profileId, SkillDto dto) {
        JobSeekerProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Skill skill = new Skill();
        skill.setSkillName(dto.getSkillName());
        skill.setProficiency(dto.getProficiency());
        skill.setProfile(profile);

        Skill saved = skillRepo.save(skill);
        return toDto(saved);
    }

    @Transactional
    public SkillDto updateSkill(Long profileId, Long skillId, SkillDto dto) {
        Skill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Skill does not belong to profile");
        }

        if (dto.getSkillName() != null)    skill.setSkillName(dto.getSkillName());
        if (dto.getProficiency() != null)  skill.setProficiency(dto.getProficiency());

        Skill saved = skillRepo.save(skill);
        return toDto(saved);
    }

    @Transactional
    public void deleteSkill(Long profileId, Long skillId) {
        Skill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getProfile().getProfileId().equals(profileId)) {
            throw new RuntimeException("Skill does not belong to profile");
        }

        skillRepo.delete(skill);
    }

    private SkillDto toDto(Skill s) {
        SkillDto dto = new SkillDto();
        dto.setId(s.getSkillId());
        dto.setSkillName(s.getSkillName());
        dto.setProficiency(s.getProficiency());
        return dto;
    }
}
