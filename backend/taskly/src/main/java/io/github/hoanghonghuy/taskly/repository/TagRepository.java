package io.github.hoanghonghuy.taskly.repository;

import io.github.hoanghonghuy.taskly.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByOwnerId(Long ownerId);
    Optional<Tag> findByNameAndOwnerId(String name, Long ownerId);

    /**
     * Count tags by owner.
     */
    long countByOwnerId(Long ownerId);
}
