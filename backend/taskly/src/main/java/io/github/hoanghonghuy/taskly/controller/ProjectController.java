package io.github.hoanghonghuy.taskly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoanghonghuy.taskly.dto.project.CreateProjectRequest;
import io.github.hoanghonghuy.taskly.dto.project.ProjectResponse;
import io.github.hoanghonghuy.taskly.dto.project.UpdateProjectRequest;
import io.github.hoanghonghuy.taskly.service.ProjectService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            JwtAuthenticationToken auth,
            @Valid @RequestBody CreateProjectRequest request) {
        long ownerId = Long.parseLong(auth.getName());
        ProjectResponse createdProject = projectService.createProject(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    public List<ProjectResponse> getProjects(JwtAuthenticationToken auth) {
        long ownerId = Long.parseLong(auth.getName());
        return projectService.getProjects(ownerId);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(JwtAuthenticationToken auth, @PathVariable long id) {
        long ownerId = Long.parseLong(auth.getName());
        return projectService.getProjectById(id, ownerId);
    }

    @PatchMapping("/{id}")
    public ProjectResponse updateProject(
            JwtAuthenticationToken auth,
            @PathVariable long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        long ownerId = Long.parseLong(auth.getName());
        return projectService.updateProject(id, ownerId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(JwtAuthenticationToken auth, @PathVariable long id) {
        long ownerId = Long.parseLong(auth.getName());
        projectService.deleteProject(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}