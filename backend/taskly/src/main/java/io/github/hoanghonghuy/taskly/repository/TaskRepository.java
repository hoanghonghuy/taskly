package io.github.hoanghonghuy.taskly.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.hoanghonghuy.taskly.entity.Priority;
import io.github.hoanghonghuy.taskly.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompleted(boolean completed);

    /**
     * JPQL search cho danh sách Task.
     *
     * Quy ước filter:
     * - completed == null => không lọc theo completed
     * - priority == null => không lọc theo priority
     * - pattern == null => không search
     *
     * Quy ước search:
     * - pattern là chuỗi dạng "%q%" (substring match)
     * - không phân biệt hoa/thường: query lower(field) và service build pattern theo lowercase
     * - description có thể null nên dùng coalesce(description, '')
     */
    @Query("""
            select t from Task t
            where (:completed is null or t.completed = :completed)
              and (t.owner.id = :ownerId)
              and (:projectId is null or t.project.id = :projectId)
              and (:priority is null or t.priority = :priority)
              and (:dueFrom is null or t.dueDate >= :dueFrom)
              and (:dueTo is null or t.dueDate <= :dueTo)
              and (
                :pattern is null
                or lower(t.title) like :pattern
                or lower(coalesce(t.description, '')) like :pattern
              )
            """)
    Page<Task> searchTasks(
            @Param("ownerId") long ownerId,
            @Param("projectId") Long projectId,
            @Param("completed") Boolean completed,
            @Param("priority") Priority priority,
            @Param("dueFrom") LocalDate dueFrom,
            @Param("dueTo") LocalDate dueTo,
            @Param("pattern") String pattern,
            Pageable pageable);

    Optional<Task> findByIdAndOwnerId(long id, long ownerId);
    boolean existsByIdAndOwnerId(long id, long ownerId);

    @Query("SELECT t FROM Task t WHERE t.recurrenceRule != io.github.hoanghonghuy.taskly.entity.RecurrenceFrequency.NONE")
    List<Task> findRecurringTasks();

    @Modifying
    @Query("UPDATE Task t SET t.project = null WHERE t.project.id = :projectId")
    void updateProjectToNullByProjectId(@Param("projectId") long projectId);
}
