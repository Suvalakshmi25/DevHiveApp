package com.devhive.DevHive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devhive.DevHive.dto.membership.MemberResponse;
import com.devhive.DevHive.dto.organization.CreateOrgRequest;
import com.devhive.DevHive.dto.organization.InviteRequest;
import com.devhive.DevHive.dto.organization.OrganizationResponse;
import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.Organization;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.mapper.OrganizationMapper;
import com.devhive.DevHive.respository.MembershipRepository;
import com.devhive.DevHive.respository.OrganizationRepository;
import com.devhive.DevHive.respository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final OrganizationMapper organizationMapper;
    private final AuthorizationService authorizationService;

    public OrganizationResponse createOrganization(CreateOrgRequest request) {
        User user = authorizationService.currentUser();

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
        User user = authorizationService.currentUser();
        return membershipRepository.findByUser(user).stream()
                .map(m -> organizationMapper.toResponse(m.getOrganization()))
                .toList();
    }

    public void inviteUser(Long orgId, InviteRequest request) {
        authorizationService.requireOrganizationRole(orgId, Role.ADMIN);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        // ✅ FIXED: invite the requested user, not currentUser
        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (membershipRepository.findByUserIdAndOrganizationId(invitedUser.getId(), orgId).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this organization");
        }

        Membership membership = Membership.builder()
                .user(invitedUser)
                .organization(org)
                .role(Role.valueOf(request.getRole()))
                .build();
        membershipRepository.save(membership);
    }

    public List<MemberResponse> getMembers(Long orgId) {
        authorizationService.requireOrganizationRole(orgId, Role.ADMIN, Role.DEVELOPER, Role.VIEWER);
        return membershipRepository.findByOrganizationId(orgId).stream()
                .map(m -> MemberResponse.builder()
                        .userId(m.getUser().getId())
                        .name(m.getUser().getName())
                        .email(m.getUser().getEmail())
                        .role(m.getRole().name())
                        .build())
                .toList();
    }

}