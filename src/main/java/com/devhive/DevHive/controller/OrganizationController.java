package com.devhive.DevHive.controller;

import com.devhive.DevHive.dto.organization.CreateOrgRequest;
import com.devhive.DevHive.dto.organization.InviteRequest;
import com.devhive.DevHive.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    @PostMapping

    public ResponseEntity<?> createOrg(@RequestBody CreateOrgRequest request) {
        return ResponseEntity.ok(organizationService.createOrganization(request));
    }

    @GetMapping
    public ResponseEntity<?> getUserOrgs() {
        return ResponseEntity.ok(organizationService.getUserOrganizations());
    }

    @PostMapping("/{orgId}/invite")
    public ResponseEntity<?> inviteUser(@PathVariable Long orgId,
                                        @RequestBody InviteRequest request) {
        organizationService.inviteUser(orgId, request);
        return ResponseEntity.ok("User invited");
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long orgId) {
        return ResponseEntity.ok(organizationService.getMembers(orgId));
    }
}