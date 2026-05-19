package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.project.CreateProjectRequest;
import com.devhive.DevHive.dto.project.ProjectResponse;
import com.devhive.DevHive.dto.project.UpdateProjectRequest;
import com.devhive.DevHive.entity.Organization;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.ProjectMapper;
import com.devhive.DevHive.respository.OrganizationRepository;
import com.devhive.DevHive.respository.ProjectRepository;
import com.devhive.DevHive.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponse createProject(CreateProjectRequest request) {

        User user = getCurrentUser();

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
        return projectRepository.findByOrganizationId(orgId).stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow();

        project.setName(request.getName());
        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}