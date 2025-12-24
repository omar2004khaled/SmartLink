package com.example.auth.repository.ProfileRepositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Skill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByProfile_ProfileId(Long profileId);
    @Query("SELECT s FROM Skill s where s.profile.profileId = :profileId")
    List <Skill> findSkillNameOfSkill(@Param("profileId") Long profileId);
}
