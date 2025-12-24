package io.github.hoanghonghuy.taskly.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.github.hoanghonghuy.taskly.entity.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Priority priority;
    private LocalDate dueDate;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
