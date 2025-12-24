package io.github.hoanghonghuy.taskly.dto.project;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequest {
    @Size(min = 1, max = 100, message = "Project name must not exceed 100 characters")
    private String name;
}
