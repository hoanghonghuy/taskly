package io.github.hoanghonghuy.taskly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.hoanghonghuy.taskly.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
}
