package com.example.auth.controller;

import com.example.auth.dto.ExperienceDto;
import com.example.auth.service.ExperienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/experience")
public class ExperienceController {

    private final ExperienceService service;

    public ExperienceController(ExperienceService service) {
        this.service = service;
    }

    @GetMapping
    public List<ExperienceDto> list(@PathVariable Long profileId) {
        return service.getExperienceForProfile(profileId);
    }

    @PostMapping
    public ResponseEntity<ExperienceDto> add(@PathVariable Long profileId,
                                             @RequestBody ExperienceDto dto) {
        return ResponseEntity.ok(service.addExperience(profileId, dto));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ExperienceDto> update(@PathVariable Long profileId,
                                                @PathVariable Long experienceId,
                                                @RequestBody ExperienceDto dto) {
        return ResponseEntity.ok(service.updateExperience(profileId, experienceId, dto));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> delete(@PathVariable Long profileId,
                                       @PathVariable Long experienceId) {
        service.deleteExperience(profileId, experienceId);
        return ResponseEntity.noContent().build();
    }
}
