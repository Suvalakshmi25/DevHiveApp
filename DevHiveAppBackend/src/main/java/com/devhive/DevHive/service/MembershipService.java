package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.membership.UpdateRoleRequest;
import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.respository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final AuthorizationService authorizationService;

    public void updateRole(Long orgId, Long userId, UpdateRoleRequest request) {
        authorizationService.requireOrganizationRole(orgId, Role.ADMIN);

        Membership membership = membershipRepository
                .findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "userId and organizationId", userId + "," + orgId));

        Role role;
        try {
            role = Role.valueOf(request.getRole());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Role must be ADMIN, DEVELOPER, or VIEWER");
        }
        if (membership.getRole() == Role.ADMIN && role != Role.ADMIN
                && membershipRepository.findByOrganizationId(orgId).stream()
                .filter(member -> member.getRole() == Role.ADMIN).count() == 1) {
            throw new UnauthorizedException("An organization must have at least one ADMIN");
        }
        membership.setRole(role);
        membershipRepository.save(membership);
    }

    public void removeMember(Long orgId, Long userId) {
        authorizationService.requireOrganizationRole(orgId, Role.ADMIN);
        Membership membership = membershipRepository
                .findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "userId and organizationId", userId + "," + orgId));

        if (membership.getRole() == Role.ADMIN
                && membershipRepository.findByOrganizationId(orgId).stream()
                .filter(member -> member.getRole() == Role.ADMIN).count() == 1) {
            throw new UnauthorizedException("An organization must have at least one ADMIN");
        }
        membershipRepository.delete(membership);
    }
}