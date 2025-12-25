package io.github.hoanghonghuy.taskly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoanghonghuy.taskly.dto.tag.CreateTagRequest;
import io.github.hoanghonghuy.taskly.dto.tag.TagResponse;
import io.github.hoanghonghuy.taskly.service.TagService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tags")
@Validated
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateTagRequest request) {
        Long ownerId = Long.parseLong(jwt.getSubject());
        TagResponse response = tagService.createTag(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TagResponse> getTags(@AuthenticationPrincipal Jwt jwt) {
        Long ownerId = Long.parseLong(jwt.getSubject());
        return tagService.getTags(ownerId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        Long ownerId = Long.parseLong(jwt.getSubject());
        tagService.deleteTag(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
