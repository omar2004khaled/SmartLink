package com.example.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.*;
import com.example.auth.service.SkillService;

@RestController
@RequestMapping("/api/profiles/{profileId}/skills")
public class SkillController {

    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    @GetMapping
    public List<SkillDto> list(@PathVariable Long profileId) {
        return service.getSkillsForProfile(profileId);
    }

    @PostMapping
    public ResponseEntity<SkillDto> add(@PathVariable Long profileId,
                                        @RequestBody SkillDto dto) {
        return ResponseEntity.ok(service.addSkillToProfile(profileId, dto));
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<SkillDto> update(@PathVariable Long profileId,
                                           @PathVariable Long skillId,
                                           @RequestBody SkillDto dto) {
        return ResponseEntity.ok(service.updateSkill(profileId, skillId, dto));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> delete(@PathVariable Long profileId,
                                       @PathVariable Long skillId) {
        service.deleteSkill(profileId, skillId);
        return ResponseEntity.noContent().build();
    }
}

