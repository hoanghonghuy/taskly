package io.github.hoanghonghuy.taskly.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.hoanghonghuy.taskly.dto.task.CreateTaskRequest;
import io.github.hoanghonghuy.taskly.dto.task.TaskResponse;
import io.github.hoanghonghuy.taskly.dto.task.UpdateTaskRequest;
import io.github.hoanghonghuy.taskly.entity.Priority;
import io.github.hoanghonghuy.taskly.entity.Task;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() == null) {
            task.setPriority(Priority.NONE);
        } else {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        Task savedTask = taskRepository.save(task);
        
        return toResponse(savedTask);
    }

    @Transactional(readOnly = true) // Chỉ đọc dữ liệu
    public List<TaskResponse> getTasks(Boolean completed, Priority priority, String q) {
        String pattern = null;
        if (q != null && !q.isBlank()) {
            String qNormalized = q.trim().toLowerCase();
            pattern = "%" + qNormalized + "%";
        }

        List<Task> tasks = taskRepository.searchTasks(completed, priority, pattern);
        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
        if (request.getTitle() != null && request.getTitle().isBlank()) // chặn blank nhưng cho null để không update
        { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be blank");
        }
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        // Task updatedTask = taskRepository.save(task);
        return toResponse(task); // vì entity đã được quản lý, không cần gọi save()
    }

    @Transactional
    public void deleteTask(long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

}