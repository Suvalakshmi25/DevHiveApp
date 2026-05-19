package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.membership.UpdateRoleRequest;
import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.enums.Role;
import com.devhive.DevHive.respository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public void updateRole(Long orgId, Long userId, UpdateRoleRequest request) {

        Membership membership = membershipRepository
                .findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        membership.setRole(Role.valueOf(request.getRole()));
        membershipRepository.save(membership);
    }

    public void removeMember(Long orgId, Long userId) {
        membershipRepository.deleteByUserIdAndOrganizationId(userId, orgId);
    }
}