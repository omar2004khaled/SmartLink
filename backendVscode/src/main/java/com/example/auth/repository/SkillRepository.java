package com.example.auth.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auth.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByProfile_ProfileId(Long profileId);
}
