package com.devhive.DevHive.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devhive.DevHive.dto.comment.CommentRequest;
import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ➕ Add comment to a task
    @PostMapping("/{taskId}")
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }

    // 📥 Get all comments for a task
    @GetMapping("/{taskId}")
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER','VIEWER')")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(commentService.getComments(taskId));
    }


    // ❌ Delete comment (only ADMIN or owner - handled in service)
    @DeleteMapping("/{commentId}")
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    // ✏️ Update comment (optional)
    @PutMapping("/{commentId}")
    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }
}