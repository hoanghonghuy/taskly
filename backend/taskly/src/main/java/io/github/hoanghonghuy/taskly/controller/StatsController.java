package io.github.hoanghonghuy.taskly.controller;

import io.github.hoanghonghuy.taskly.repository.ProjectRepository;
import io.github.hoanghonghuy.taskly.repository.TagRepository;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("isAuthenticated()")
public class StatsController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    public StatsController(
        TaskRepository taskRepository,
        ProjectRepository projectRepository,
        TagRepository tagRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public StatsResponse getStats(JwtAuthenticationToken auth) {
        long ownerId = Long.parseLong(auth.getName());

        Long totalTasks = taskRepository.countByOwnerId(ownerId);
        Long completedTasks = taskRepository.countByOwnerIdAndCompleted(
            ownerId,
            true
        );
        Long totalProjects = projectRepository.countByOwnerId(ownerId);
        Long totalTags = tagRepository.countByOwnerId(ownerId);

        return new StatsResponse(
            totalTasks,
            completedTasks,
            totalProjects,
            totalTags
        );
    }

    record StatsResponse(
        Long totalTasks,
        Long completedTasks,
        Long totalProjects,
        Long totalTags
    ) {}
}
