package io.github.hoanghonghuy.taskly.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.hoanghonghuy.taskly.dto.task.CreateTaskRequest;
import io.github.hoanghonghuy.taskly.dto.task.TaskResponse;
import io.github.hoanghonghuy.taskly.dto.task.UpdateTaskRequest;
import io.github.hoanghonghuy.taskly.entity.Priority;
import io.github.hoanghonghuy.taskly.entity.Project;
import io.github.hoanghonghuy.taskly.entity.Task;
import io.github.hoanghonghuy.taskly.entity.User;
import io.github.hoanghonghuy.taskly.repository.ProjectRepository;
import io.github.hoanghonghuy.taskly.repository.TaskRepository;
import io.github.hoanghonghuy.taskly.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
        }
        if (task.getParent() != null) {
            response.setParentId(task.getParent().getId());
        }
        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            response.setSubTasks(task.getSubTasks().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()));
        }
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    @Transactional
    public TaskResponse createTask(long ownerId, 
    CreateTaskRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

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
        
        if (request.getProjectId() != null) {
            Project project = projectRepository.findByIdAndOwnerId(request.getProjectId(), ownerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
            task.setProject(project);
        }

        if (request.getParentId() != null) {
            Task parent = taskRepository.findByIdAndOwnerId(request.getParentId(), ownerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent task not found"));
            
            // Validate: Không cho phép tạo subtask của subtask (độ sâu tối đa 1 cấp)
            if (parent.getParent() != null) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum subtask depth is 1");
            }

            task.setParent(parent);
            // Kế thừa project từ parent
            task.setProject(parent.getProject());
        }
        
        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    @Transactional(readOnly = true) // Chỉ đọc dữ liệu
    public Page<TaskResponse> getTasks(
            long ownerId,
            Long projectId,
            Boolean completed,
            Priority priority,
            String q,
            String view,
            LocalDate dueFrom,
            LocalDate dueTo,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        
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

        // Kiểm tra tính hợp lệ của khoảng ngày, nếu cả hai đều không null và from > to thì lỗi
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.isAfter(effectiveTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid due date range: dueFrom is after dueTo");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks = taskRepository.searchTasks(ownerId, 
            projectId,
            completed, 
            priority, 
            effectiveFrom, 
            effectiveTo, 
            pattern, 
            pageable);
        return tasks.map(this::toResponse);
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
        
        if (Boolean.TRUE.equals(request.getRemoveProject())) {
            task.setProject(null);
        } else if (request.getProjectId() != null) {
             Project project = projectRepository.findByIdAndOwnerId(request.getProjectId(), ownerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
            task.setProject(project);
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
