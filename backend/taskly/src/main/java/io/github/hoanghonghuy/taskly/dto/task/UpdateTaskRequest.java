package io.github.hoanghonghuy.taskly.dto.task;

import java.time.LocalDate;
import java.util.List;

import io.github.hoanghonghuy.taskly.entity.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

    @Size(min = 1, max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Boolean completed;

    private Priority priority;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    private Long projectId;

    private Boolean removeProject;

    private List<Long> tagIds;

    private String recurrenceRule;
}
