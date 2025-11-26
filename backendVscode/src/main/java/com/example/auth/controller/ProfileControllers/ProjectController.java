package com.example.auth.controller.ProfileControllers;

import com.example.auth.dto.ProfileDtos.ProjectDto;
import com.example.auth.service.ProfileServices.ProjectService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProjectDto> list(@PathVariable Long profileId) {
        return service.getProjectsForProfile(profileId);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> add(@PathVariable Long profileId,
                                          @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(service.addProjectToProfile(profileId, dto));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update(@PathVariable Long profileId,
                                             @PathVariable Long projectId,
                                             @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(service.updateProjectForProfile(profileId, projectId, dto));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable Long profileId,
                                       @PathVariable Long projectId) {
        service.deleteProjectFromProfile(profileId, projectId);
        return ResponseEntity.noContent().build();
    }
}
