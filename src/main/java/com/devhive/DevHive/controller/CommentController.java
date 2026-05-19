package com.devhive.DevHive.controller;

import com.devhive.DevHive.dto.comment.CommentRequest;
import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ➕ Add comment to a task
    @PostMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }

    // 📥 Get all comments for a task
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER','VIEWER')")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(commentService.getComments(taskId));
    }


    // ❌ Delete comment (only ADMIN or owner - handled in service)
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    // ✏️ Update comment (optional)
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }
}