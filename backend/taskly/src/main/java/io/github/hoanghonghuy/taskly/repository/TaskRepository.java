package io.github.hoanghonghuy.taskly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.hoanghonghuy.taskly.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompleted(boolean completed);
    
    /**
     * JPQL search cho danh sách Task.
     *
     * Quy ước filter:
     * - completed == null => không lọc theo completed
     * - q == null/blank => không search
     *
     * Quy ước search:
     * - match theo substring (LIKE %q%) trên title và description
     * - không phân biệt hoa/thường (lower(...))
     * - description có thể null nên dùng coalesce(description, '')
     */

    @Query("""
            select t from Task t
            where (:completed is null or t.completed = :completed)
              and (
                :pattern is null
                or lower(t.title) like :pattern
                or lower(coalesce(t.description, '')) like :pattern
              )
            """)
    List<Task> searchTasks(@Param("completed") Boolean completed, @Param("pattern") String pattern);
}
