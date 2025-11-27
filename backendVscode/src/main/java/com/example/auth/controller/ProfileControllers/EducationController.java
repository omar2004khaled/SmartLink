package com.example.auth.controller.ProfileControllers;

import com.example.auth.dto.ProfileDtos.EducationDto;
import com.example.auth.service.ProfileServices.EducationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/education")
public class EducationController {

    private final EducationService service;

    public EducationController(EducationService service) {
        this.service = service;
    }

    @GetMapping
    public List<EducationDto> list(@PathVariable Long profileId) {
        return service.getEducationForProfile(profileId);
    }

    @PostMapping
    public ResponseEntity<EducationDto> add(@PathVariable Long profileId,
                                            @RequestBody EducationDto dto) {
        return ResponseEntity.ok(service.addEducation(profileId, dto));
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<EducationDto> update(@PathVariable Long profileId,
                                               @PathVariable Long educationId,
                                               @RequestBody EducationDto dto) {
        return ResponseEntity.ok(service.updateEducation(profileId, educationId, dto));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<Void> delete(@PathVariable Long profileId,
                                       @PathVariable Long educationId) {
        service.deleteEducation(profileId, educationId);
        return ResponseEntity.noContent().build();
    }
}
