package io.github.hoanghonghuy.taskly.dto.project;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequest {

    @Size(
        min = 1,
        max = 100,
        message = "Project name must not exceed 100 characters"
    )
    private String name;

    @Size(
        max = 500,
        message = "Project description must not exceed 500 characters"
    )
    private String description;

    @Size(max = 7, message = "Color must be a valid hex code")
    private String color;
}
