package io.github.hoanghonghuy.taskly.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTagRequest {
    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name must not exceed 50 characters")
    private String name;
}
