package com.devhive.DevHive.controller;

import com.devhive.DevHive.dto.membership.UpdateRoleRequest;
import com.devhive.DevHive.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PutMapping("/{orgId}/{userId}")
    public ResponseEntity<?> updateRole(@PathVariable Long orgId,
                                        @PathVariable Long userId,
                                        @RequestBody UpdateRoleRequest request) {
        membershipService.updateRole(orgId, userId, request);
        return ResponseEntity.ok("Role updated");
    }

    @DeleteMapping("/{orgId}/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long orgId,
                                          @PathVariable Long userId) {
        membershipService.removeMember(orgId, userId);
        return ResponseEntity.ok("Member removed");
    }
}