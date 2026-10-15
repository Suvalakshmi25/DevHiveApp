package com.devhive.DevHive.controller;

import com.devhive.DevHive.dto.codesnippet.CreateSnippetRequest;
import com.devhive.DevHive.dto.codesnippet.SnippetCommentRequest;
import com.devhive.DevHive.service.SnippetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
public class SnippetController {

    private final SnippetService snippetService;

    @PostMapping
    public ResponseEntity<?> createSnippet(@RequestBody CreateSnippetRequest request) {
        return ResponseEntity.ok(snippetService.createSnippet(request));
    }

    @GetMapping
    public ResponseEntity<?> getSnippets(@RequestParam Long projectId) {
        return ResponseEntity.ok(snippetService.getSnippets(projectId));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id,
                                        @RequestBody SnippetCommentRequest request) {
        return ResponseEntity.ok(snippetService.addComment(id, request));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(snippetService.getComments(id));
    }
}