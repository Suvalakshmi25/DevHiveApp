package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.comment.CommentRequest;
import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.entity.Comment;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.mapper.CommentMapper;
import com.devhive.DevHive.respository.CommentRepository;
import com.devhive.DevHive.respository.TaskRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final AuthorizationService authorizationService;

    // ➕ ADD COMMENT
    public CommentResponse addComment(Long taskId, CommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        User user = authorizationService.currentUser();
        authorizationService.requireTaskRole(taskId, Role.ADMIN, Role.DEVELOPER);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(request.getContent().trim())
                .build();

        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    // ❌ DELETE COMMENT
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        authorizationService.requireTaskRole(comment.getTask().getId(), Role.ADMIN);

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getComments(Long taskId) {
        authorizationService.requireTaskRole(taskId, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);

        List<Comment> comments = commentRepository.findByTaskId(taskId);

        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        Task task = authorizationService.requireTaskRole(
                comment.getTask().getId(), Role.ADMIN, Role.DEVELOPER);
        var membership = authorizationService.requireOrganizationRole(
                task.getProject().getOrganization().getId(), Role.ADMIN, Role.DEVELOPER);
        if (membership.getRole() == Role.DEVELOPER
                && !comment.getUser().getId().equals(membership.getUser().getId())) {
            throw new UnauthorizedException("Developers can only edit their own comments");
        }

        comment.setContent(request.getContent().trim());
        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }
}