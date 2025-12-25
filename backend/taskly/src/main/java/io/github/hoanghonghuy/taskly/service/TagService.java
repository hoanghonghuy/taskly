package io.github.hoanghonghuy.taskly.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.hoanghonghuy.taskly.dto.tag.CreateTagRequest;
import io.github.hoanghonghuy.taskly.dto.tag.TagResponse;
import io.github.hoanghonghuy.taskly.entity.Tag;
import io.github.hoanghonghuy.taskly.entity.User;
import io.github.hoanghonghuy.taskly.repository.TagRepository;
import io.github.hoanghonghuy.taskly.repository.UserRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    @Transactional
    public TagResponse createTag(Long ownerId, CreateTagRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (tagRepository.findByNameAndOwnerId(request.getName(), ownerId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setOwner(owner);
        Tag savedTag = tagRepository.save(tag);
        return toResponse(savedTag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTags(Long ownerId) {
        return tagRepository.findByOwnerId(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTag(Long id, Long ownerId) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        
        if (!tag.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your tag");
        }
        
        tagRepository.delete(tag);
    }
}
