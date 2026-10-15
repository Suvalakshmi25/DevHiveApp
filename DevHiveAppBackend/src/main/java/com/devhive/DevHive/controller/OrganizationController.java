package com.devhive.DevHive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devhive.DevHive.dto.organization.CreateOrgRequest;
import com.devhive.DevHive.dto.organization.InviteRequest;
import com.devhive.DevHive.service.OrganizationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    @PostMapping

    public ResponseEntity<?> createOrg(@Valid @RequestBody CreateOrgRequest request) {
        return ResponseEntity.ok(organizationService.createOrganization(request));
    }

    @GetMapping
    public ResponseEntity<?> getUserOrgs() {
        return ResponseEntity.ok(organizationService.getUserOrganizations());
    }

    @PostMapping("/{orgId}/invite")
    public ResponseEntity<?> inviteUser(@PathVariable Long orgId,
                                        @Valid @RequestBody InviteRequest request) {
        organizationService.inviteUser(orgId, request);
        return ResponseEntity.ok("User invited");
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long orgId) {
        return ResponseEntity.ok(organizationService.getMembers(orgId));
    }
}