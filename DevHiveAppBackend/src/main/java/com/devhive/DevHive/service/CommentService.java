package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.comment.CommentRequest;
import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.entity.Comment;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.CommentMapper;
import com.devhive.DevHive.respository.CommentRepository;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.UserRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    // ➕ ADD COMMENT
    public CommentResponse addComment(Long taskId, CommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        User user = getCurrentUser();

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

        // access control disabled while auth is turned off
        // if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
        //     throw new UnauthorizedException("You are not allowed to delete this comment");
        // }

        commentRepository.delete(comment);
    }

    // 🔍 GET CURRENT USER FROM JWT
    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return userRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("User", "default", "no user found"));
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
    }

    // 🔐 CHECK ADMIN ROLE
    private boolean isAdmin(User user) {
        if (user == null || user.getMemberships() == null) {
            return false;
        }
        return user.getMemberships().stream()
                .anyMatch(m -> m.getRole() != null && m.getRole().name().equals("ADMIN"));
    }

    public List<CommentResponse> getComments(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

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

        // access control disabled while auth is turned off
        // if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
        //     throw new UnauthorizedException("You are not allowed to update this comment");
        // }

        comment.setContent(request.getContent().trim());
        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }
}