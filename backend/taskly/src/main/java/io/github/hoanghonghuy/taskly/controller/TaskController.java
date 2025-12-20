package io.github.hoanghonghuy.taskly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String q) {
        return taskService.getTasks(completed, priority, q);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable long id) {
        return taskService.getTaskById(id);
    }

    @PatchMapping("/{id}") // Sử dụng PATCH để cập nhật một phần
    public TaskResponse updateTask(@PathVariable long id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
