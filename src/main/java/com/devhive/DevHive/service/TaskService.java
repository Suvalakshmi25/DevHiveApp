package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.task.*;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Priority;
import com.devhive.DevHive.enums.Status;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.mapper.TaskMapper;
import com.devhive.DevHive.respository.ProjectRepository;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {


    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    public TaskResponse createTask(CreateTaskRequest request) {

        User user = getCurrentUser();

        if (!hasAccess(user, request.getProjectId())) {
            throw new UnauthorizedException("You are not allowed to create task");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        Task task = Task.builder()
                .title(request.getTitle())
                .project(project)
                .createdBy(user)
                .build();

        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(Priority.valueOf(request.getPriority()));

        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public TaskResponse updateStatus(Long id, UpdateStatusRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();

        task.setStatus(Status.valueOf(request.getStatus()));
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }
    private final NotificationService notificationService;

    public TaskResponse assignTask(Long id, AssignTaskRequest request) {

        Task task = taskRepository.findById(id).orElseThrow();

        User user = userRepository.findById(request.getUserId()).orElseThrow();

        task.setAssignedTo(user);
        taskRepository.save(task);

        // 🔥 REAL-TIME NOTIFICATION
        notificationService.sendNotification(
                user.getId(),
                "You have been assigned a new task: " + task.getTitle()
        );
        notificationService.notifyTaskAssigned(user, task);

        return taskMapper.toResponse(task);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
    private boolean hasAccess(User user, Long projectId) {
        return true; // you can expand later
    }
}