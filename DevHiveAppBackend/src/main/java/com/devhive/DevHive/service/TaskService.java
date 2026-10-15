package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.task.AssignTaskRequest;
import com.devhive.DevHive.dto.task.CreateTaskRequest;
import com.devhive.DevHive.dto.task.TaskResponse;
import com.devhive.DevHive.dto.task.UpdateStatusRequest;
import com.devhive.DevHive.dto.task.UpdateTaskRequest;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.enums.Priority;
import com.devhive.DevHive.enums.Status;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.mapper.TaskMapper;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {


    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;
    private final TaskMapper taskMapper;
    private final AuthorizationService authorizationService;
    public TaskResponse createTask(CreateTaskRequest request) {
        Project project = authorizationService.requireProjectRole(
                request.getProjectId(), Role.ADMIN, Role.DEVELOPER);
        User user = authorizationService.currentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(Status.TODO)
                .priority(request.getPriority() != null ? Priority.valueOf(request.getPriority()) : Priority.MEDIUM)
                .project(project)
                .createdBy(user)
                .build();

        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getTasksByProject(Long projectId) {
        authorizationService.requireProjectRole(projectId, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = authorizationService.requireTaskRole(id, Role.ADMIN, Role.DEVELOPER);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(Priority.valueOf(request.getPriority()));

        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public void deleteTask(Long id) {
        Task task = authorizationService.requireTaskRole(id, Role.ADMIN);
        taskRepository.delete(task);
    }

    public TaskResponse updateStatus(Long id, UpdateStatusRequest request) {
        Task task = authorizationService.requireTaskRole(id, Role.ADMIN, Role.DEVELOPER);

        task.setStatus(Status.valueOf(request.getStatus()));
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }
    private final NotificationService notificationService;

    public TaskResponse assignTask(Long id, AssignTaskRequest request) {
        Task task = authorizationService.requireTaskRole(id, Role.ADMIN, Role.DEVELOPER);
        User user = null;
        if (request.getUserId() != null) {
            user = membershipRepository.findByUserIdAndOrganizationId(
                            request.getUserId(), task.getProject().getOrganization().getId())
                    .map(membership -> membership.getUser())
                    .orElseThrow(() -> new UnauthorizedException("Assignee must belong to the project organization"));
        }

        task.setAssignedTo(user);
        taskRepository.save(task);

        if (user != null) {
            notificationService.sendNotification(
                    user.getId(),
                    "You have been assigned a new task: " + task.getTitle()
            );
            notificationService.notifyTaskAssigned(user, task);
        }

        return taskMapper.toResponse(task);
    }

}