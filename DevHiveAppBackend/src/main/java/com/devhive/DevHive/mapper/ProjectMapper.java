package com.devhive.DevHive.mapper;
import com.devhive.DevHive.dto.project.ProjectResponse;
import com.devhive.DevHive.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .build();
    }
}