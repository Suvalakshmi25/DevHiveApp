package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.membership.MemberResponse;
import com.devhive.DevHive.dto.organization.CreateOrgRequest;
import com.devhive.DevHive.dto.organization.InviteRequest;
import com.devhive.DevHive.dto.organization.OrganizationResponse;
import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.Organization;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.mapper.OrganizationMapper;
import com.devhive.DevHive.respository.MembershipRepository;
import com.devhive.DevHive.respository.OrganizationRepository;
import com.devhive.DevHive.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationResponse createOrganization(CreateOrgRequest request) {
        User user = getCurrentUser();

        Organization org = Organization.builder()
                .name(request.getName())
                .createdBy(user)
                .build();
        organizationRepository.save(org);

        // ✅ Creator gets ADMIN membership
        Membership membership = Membership.builder()
                .user(user)
                .organization(org)
                .role(Role.ADMIN)
                .build();
        membershipRepository.save(membership);

        return organizationMapper.toResponse(org);
    }

    public List<OrganizationResponse> getUserOrganizations() {
        User user = getCurrentUser();
        return membershipRepository.findByUser(user).stream()
                .map(m -> organizationMapper.toResponse(m.getOrganization()))
                .toList();
    }

    public void inviteUser(Long orgId, InviteRequest request) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser, orgId)) {
            throw new UnauthorizedException("Only ADMIN can invite users");
        }

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        // ✅ FIXED: invite the requested user, not currentUser
        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // ✅ FIXED: use role from request, not hardcoded ADMIN
        Membership membership = Membership.builder()
                .user(invitedUser)
                .organization(org)
                .role(Role.valueOf(request.getRole()))
                .build();
        membershipRepository.save(membership);
    }

    public List<MemberResponse> getMembers(Long orgId) {
        return membershipRepository.findByOrganizationId(orgId).stream()
                .map(m -> MemberResponse.builder()
                        .userId(m.getUser().getId())
                        .name(m.getUser().getName())
                        .email(m.getUser().getEmail())
                        .role(m.getRole().name())
                        .build())
                .toList();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    private boolean isAdmin(User user, Long orgId) {
        return membershipRepository.findByUser(user).stream()
                .anyMatch(m -> m.getOrganization().getId().equals(orgId)
                        && m.getRole() == Role.ADMIN);
    }
}