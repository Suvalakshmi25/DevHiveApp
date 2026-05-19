package com.devhive.DevHive.mapper;

import com.devhive.DevHive.dto.organization.OrganizationResponse;
import com.devhive.DevHive.entity.Organization;
import org.springframework.stereotype.Component;


@Component
public class OrganizationMapper {

    public OrganizationResponse toResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .build();
    }
}