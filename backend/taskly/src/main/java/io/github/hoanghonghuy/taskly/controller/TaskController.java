package io.github.hoanghonghuy.taskly.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoanghonghuy.taskly.dto.task.CreateTaskRequest;
import io.github.hoanghonghuy.taskly.dto.task.TaskResponse;
import io.github.hoanghonghuy.taskly.dto.task.UpdateTaskRequest;
import io.github.hoanghonghuy.taskly.entity.Priority;
import io.github.hoanghonghuy.taskly.service.TaskService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            JwtAuthenticationToken auth,
            @Valid @RequestBody CreateTaskRequest request) {
        long ownerId = Long.parseLong(auth.getName());
        TaskResponse createdTask = taskService.createTask(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public Page<TaskResponse> getTasks(
            JwtAuthenticationToken auth,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        long ownerId = Long.parseLong(auth.getName());
        return taskService.getTasks(ownerId, completed, priority, q, view, dueFrom, dueTo, page, size, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(JwtAuthenticationToken auth, @PathVariable long id) {
        long ownerId = Long.parseLong(auth.getName());
        return taskService.getTaskById(id, ownerId);
    }

    @PatchMapping("/{id}") // Sử dụng PATCH để cập nhật một phần
    public TaskResponse updateTask(
            JwtAuthenticationToken auth,
            @PathVariable long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        long ownerId = Long.parseLong(auth.getName());
        return taskService.updateTask(id, ownerId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(JwtAuthenticationToken auth, @PathVariable long id) {
        long ownerId = Long.parseLong(auth.getName());
        taskService.deleteTask(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
