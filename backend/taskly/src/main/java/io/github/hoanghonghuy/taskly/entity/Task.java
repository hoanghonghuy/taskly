package io.github.hoanghonghuy.taskly.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Nhiều Task thuộc về một User
    @JoinColumn(name = "owner_id", nullable = false) // khóa ngoại tới bảng users
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY) // Nhiều Task thuộc về một Project (có thể null nếu là Inbox)
    @JoinColumn(name = "project_id") // khóa ngoại tới bảng projects
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Task parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> subTasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceFrequency recurrenceRule = RecurrenceFrequency.NONE;

    private LocalDate lastRecurrenceDate;

    private LocalDateTime reminderTime;
}
