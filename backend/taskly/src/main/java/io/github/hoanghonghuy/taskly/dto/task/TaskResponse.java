package io.github.hoanghonghuy.taskly.dto.task;

import io.github.hoanghonghuy.taskly.dto.project.ProjectResponse;
import io.github.hoanghonghuy.taskly.dto.tag.TagResponse;
import io.github.hoanghonghuy.taskly.entity.Priority;
import io.github.hoanghonghuy.taskly.entity.RecurrenceFrequency;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private ProjectResponse project;
    private Long parentId;
    private List<TaskResponse> subTasks;
    private List<TagResponse> tags;
    private RecurrenceFrequency recurrenceRule;
    private LocalDateTime reminderTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
