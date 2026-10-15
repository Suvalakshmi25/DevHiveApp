package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.project.CreateProjectRequest;
import com.devhive.DevHive.dto.project.ProjectResponse;
import com.devhive.DevHive.dto.project.UpdateProjectRequest;
import com.devhive.DevHive.entity.Organization;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.ProjectMapper;
import com.devhive.DevHive.respository.OrganizationRepository;
import com.devhive.DevHive.respository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectMapper projectMapper;
    private final AuthorizationService authorizationService;

    public ProjectResponse createProject(CreateProjectRequest request) {

        authorizationService.requireOrganizationRole(request.getOrganizationId(), Role.ADMIN);
        var user = authorizationService.currentUser();

        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Org not found"));

        Project project = Project.builder()
                .name(request.getName())
                .organization(org)
                .createdBy(user)
                .build();

        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> getProjects(Long orgId) {
        authorizationService.requireOrganizationRole(orgId, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return projectRepository.findByOrganizationId(orgId).stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        authorizationService.requireProjectRole(id, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        authorizationService.requireProjectRole(id, Role.ADMIN);

        project.setName(request.getName());
        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        authorizationService.requireProjectRole(id, Role.ADMIN);
        projectRepository.delete(project);
    }
}