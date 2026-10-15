package com.devhive.DevHive.service;

import java.util.Arrays;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.Project;
import com.devhive.DevHive.entity.CodeSnippet;
import com.devhive.DevHive.entity.Task;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.respository.MembershipRepository;
import com.devhive.DevHive.respository.ProjectRepository;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CodeSnippetRepository snippetRepository;

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException("Authentication is required");
        }

        return userRepository.findByEmailWithMemberships(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user was not found"));
    }

    public Membership requireOrganizationRole(Long organizationId, Role... allowedRoles) {
        User user = currentUser();
        Membership membership = membershipRepository.findByUserIdAndOrganizationId(user.getId(), organizationId)
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this organization"));

        if (Arrays.stream(allowedRoles).noneMatch(role -> role == membership.getRole())) {
            throw new UnauthorizedException("Your organization role does not allow this action");
        }
        return membership;
    }

    public Project requireProjectRole(Long projectId, Role... allowedRoles) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        requireOrganizationRole(project.getOrganization().getId(), allowedRoles);
        return project;
    }

    public Task requireTaskRole(Long taskId, Role... allowedRoles) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        requireOrganizationRole(task.getProject().getOrganization().getId(), allowedRoles);
        return task;
    }

    public CodeSnippet requireSnippetRole(Long snippetId, Role... allowedRoles) {
        CodeSnippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new ResourceNotFoundException("CodeSnippet", "id", snippetId));
        requireOrganizationRole(snippet.getProject().getOrganization().getId(), allowedRoles);
        return snippet;
    }

}
