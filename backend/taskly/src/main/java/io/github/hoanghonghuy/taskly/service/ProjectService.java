package io.github.hoanghonghuy.taskly.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.hoanghonghuy.taskly.dto.project.CreateProjectRequest;
import io.github.hoanghonghuy.taskly.dto.project.ProjectResponse;
import io.github.hoanghonghuy.taskly.dto.project.UpdateProjectRequest;
import io.github.hoanghonghuy.taskly.entity.Project;
import io.github.hoanghonghuy.taskly.entity.User;
import io.github.hoanghonghuy.taskly.repository.ProjectRepository;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;
import io.github.hoanghonghuy.taskly.repository.UserRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
            TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }

    @Transactional
    public ProjectResponse createProject(long ownerId, CreateProjectRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setOwner(owner);
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(long ownerId) {
        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(long id, long ownerId) {
        Project project = projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(long id, long ownerId, UpdateProjectRequest request) {
        Project project = projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        return toResponse(project); // Auto-save by JPA dirty checking
    }

    @Transactional
    public void deleteProject(long id, long ownerId) {
        if (!projectRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // Chuyển tất cả task sang Inbox (project = null) trước khi xóa
        taskRepository.updateProjectToNullByProjectId(id);

        projectRepository.deleteById(id);
    }
}
