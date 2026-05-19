package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.task.TaskResponse;
import com.devhive.DevHive.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .assignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .build();
    }
}