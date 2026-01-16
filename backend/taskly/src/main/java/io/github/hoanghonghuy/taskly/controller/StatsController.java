package io.github.hoanghonghuy.taskly.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoanghonghuy.taskly.repository.ProjectRepository;
import io.github.hoanghonghuy.taskly.repository.TagRepository;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("isAuthenticated()")
public class StatsController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    public StatsController(TaskRepository taskRepository,
                          ProjectRepository projectRepository,
                          TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public StatsResponse getStats() {
        Long totalTasks = taskRepository.count();
        Long completedTasks = taskRepository.countByCompleted(true);
        Long totalProjects = projectRepository.count();
        Long totalTags = tagRepository.count();

        return new StatsResponse(totalTasks, completedTasks, totalProjects, totalTags);
    }

    record StatsResponse(
        Long totalTasks,
        Long completedTasks,
        Long totalProjects,
        Long totalTags
    ) {}
}