package io.github.hoanghonghuy.taskly.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagResponse {
    private Long id;
    private String name;
}
