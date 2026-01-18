package io.github.hoanghonghuy.taskly.repository;

import io.github.hoanghonghuy.taskly.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(long ownerId);
    Optional<Project> findByIdAndOwnerId(long id, long ownerId);
    boolean existsByIdAndOwnerId(long id, long ownerId);

    /**
     * Count projects by owner.
     */
    long countByOwnerId(long ownerId);
}
