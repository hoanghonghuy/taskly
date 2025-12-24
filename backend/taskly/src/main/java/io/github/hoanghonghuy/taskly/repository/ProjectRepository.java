package io.github.hoanghonghuy.taskly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.hoanghonghuy.taskly.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(long ownerId);
    Optional<Project> findByIdAndOwnerId(long id, long ownerId);
    boolean existsByIdAndOwnerId(long id, long ownerId);
}
