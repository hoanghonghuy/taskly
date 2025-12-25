package io.github.hoanghonghuy.taskly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.hoanghonghuy.taskly.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByOwnerId(Long ownerId);
    Optional<Tag> findByNameAndOwnerId(String name, Long ownerId);
}
