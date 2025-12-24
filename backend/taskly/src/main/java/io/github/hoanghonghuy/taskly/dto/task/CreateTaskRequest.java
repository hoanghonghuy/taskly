package io.github.hoanghonghuy.taskly.dto.task;

import java.time.LocalDate;

import io.github.hoanghonghuy.taskly.entity.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")  
    private String description;

    private Priority priority;

    @FutureOrPresent(message = "Due date must be today or in the future")   
    private LocalDate dueDate;

    private Long projectId;
}
