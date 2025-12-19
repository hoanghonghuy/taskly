package io.github.hoanghonghuy.taskly.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Table(name = "tasks")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Enumerated(EnumType.STRING) // lưu trữ dưới dạng chuỗi trong cơ sở dữ liệu
    @Column(nullable = false)
    private Priority priority = Priority.NONE;

    private LocalDate dueDate;

    @CreationTimestamp // tự động gán thời gian tạo khi bản ghi được tạo
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // tự động cập nhật thời gian sửa đổi khi bản ghi được cập nhật
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
