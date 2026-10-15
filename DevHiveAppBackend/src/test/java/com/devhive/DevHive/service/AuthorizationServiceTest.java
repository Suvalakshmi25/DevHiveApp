package com.devhive.DevHive.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.respository.CodeSnippetRepository;
import com.devhive.DevHive.respository.MembershipRepository;
import com.devhive.DevHive.respository.ProjectRepository;
import com.devhive.DevHive.respository.TaskRepository;
import com.devhive.DevHive.respository.UserRepository;

class AuthorizationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final MembershipRepository membershipRepository = mock(MembershipRepository.class);
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final CodeSnippetRepository snippetRepository = mock(CodeSnippetRepository.class);
    private final AuthorizationService authorizationService = new AuthorizationService(
            userRepository, membershipRepository, projectRepository, taskRepository, snippetRepository);

    @BeforeEach
    void authenticateUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@example.com", null, List.of()));
        User user = mock(User.class);
        when(user.getId()).thenReturn(42L);
        when(userRepository.findByEmailWithMemberships("user@example.com")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsAnAdminToPerformAdminActions() {
        Membership membership = mock(Membership.class);
        when(membership.getRole()).thenReturn(Role.ADMIN);
        when(membershipRepository.findByUserIdAndOrganizationId(42L, 7L))
                .thenReturn(Optional.of(membership));

        assertSame(membership, authorizationService.requireOrganizationRole(7L, Role.ADMIN));
    }

    @Test
    void allowsADeveloperToPerformContributorActions() {
        Membership membership = mock(Membership.class);
        when(membership.getRole()).thenReturn(Role.DEVELOPER);
        when(membershipRepository.findByUserIdAndOrganizationId(42L, 7L))
                .thenReturn(Optional.of(membership));

        assertSame(membership,
                authorizationService.requireOrganizationRole(7L, Role.ADMIN, Role.DEVELOPER));
    }

    @Test
    void deniesAViewerAdminActions() {
        Membership membership = mock(Membership.class);
        when(membership.getRole()).thenReturn(Role.VIEWER);
        when(membershipRepository.findByUserIdAndOrganizationId(42L, 7L))
                .thenReturn(Optional.of(membership));

        assertThrows(UnauthorizedException.class,
                () -> authorizationService.requireOrganizationRole(7L, Role.ADMIN));
    }

    @Test
    void deniesUsersWhoAreNotOrganizationMembers() {
        when(membershipRepository.findByUserIdAndOrganizationId(42L, 7L))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authorizationService.requireOrganizationRole(7L, Role.ADMIN, Role.DEVELOPER, Role.VIEWER));
    }

    @Test
    void rejectsRequestsWithoutAnAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class,
                () -> authorizationService.requireOrganizationRole(7L, Role.ADMIN));
    }
}
