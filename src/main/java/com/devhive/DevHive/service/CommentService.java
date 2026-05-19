package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.comment.CommentRequest;
import com.devhive.DevHive.dto.comment.CommentResponse;
import com.devhive.DevHive.entity.Comment;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.mapper.CommentMapper;
import com.devhive.DevHive.respository.CommentRepository;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    // ➕ ADD COMMENT
    public CommentResponse addComment(Long taskId, CommentRequest request) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    // ❌ DELETE COMMENT
    public void deleteComment(Long commentId) {

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        // 🔐 SECURITY CHECK
        if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new UnauthorizedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // 🔍 GET CURRENT USER FROM JWT
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    // 🔐 CHECK ADMIN ROLE
    private boolean isAdmin(User user) {
        return user.getMemberships().stream()
                .anyMatch(m -> m.getRole().name().equals("ADMIN"));
    }
    public List<CommentResponse> getComments(Long taskId) {

        // 🔍 Check if task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        // 📥 Fetch all comments for the task
        List<Comment> comments = commentRepository.findByTaskId(taskId);

        // 🔄 Convert to DTO
        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
    }
    public CommentResponse updateComment(Long commentId, CommentRequest request) {

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        // 🔐 SECURITY CHECK (owner OR admin)
        if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new UnauthorizedException("You are not allowed to update this comment");
        }

        // ✏️ Update content
        comment.setContent(request.getContent());

        // (optional) update timestamp if you have field
        // comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }
}