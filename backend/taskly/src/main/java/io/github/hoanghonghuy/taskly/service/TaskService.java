package io.github.hoanghonghuy.taskly.service;

import java.time.LocalDate;
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
import io.github.hoanghonghuy.taskly.entity.User;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;
import io.github.hoanghonghuy.taskly.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
    public TaskResponse createTask(long ownerId, 
    CreateTaskRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + ownerId));

        Task task = new Task();
        task.setOwner(owner);
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
    public List<TaskResponse> getTasks(
            long ownerId,
            Boolean completed,
            Priority priority,
            String q,
            String view,
            LocalDate dueFrom,
            LocalDate dueTo) {
        String pattern = null;
        if (q != null && !q.isBlank()) {
            String qNormalized = q.trim().toLowerCase();
            pattern = "%" + qNormalized + "%";
        }

        LocalDate baseFrom = null;
        LocalDate baseTo = null;
        if (view != null && !view.isBlank()) {
            LocalDate today = LocalDate.now();
            String v = view.trim().toLowerCase();

            if (completed == null) {
                completed = false; // Mặc định chỉ lấy công việc chưa hoàn thành khi sử dụng view
            }
            switch (v) {
                case "today":
                    baseFrom = today;
                    baseTo = today;
                    break;
                case "upcoming":
                    baseFrom = today;
                    baseTo = null;
                    break;
                case "overdue":
                    baseFrom = null;
                    baseTo = today.minusDays(1);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid view parameter: " + view);
            }
        }

        LocalDate effectiveFrom = baseFrom;
        LocalDate effectiveTo = baseTo;

        if (dueFrom != null) {
            effectiveFrom = dueFrom;
        }
        if (dueTo != null) {
            effectiveTo = dueTo;
        }

        // Kiểm tra tính hợp lệ của khoảng ngày, nếu cả hai đều không null và from > to
        // thì lỗi
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.isAfter(effectiveTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid due date range: dueFrom is after dueTo");
        }

        List<Task> tasks = taskRepository.searchTasks(
            ownerId, 
            completed, 
            priority, 
            effectiveFrom, 
            effectiveTo, 
            pattern);
        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(long id, long ownerId) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(long id, long ownerId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
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
    public void deleteTask(long id, long ownerId) {
        if (!taskRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

}